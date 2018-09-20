package iansantos.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import iansantos.login.R;
import iansantos.login.adapter.SearchAdapter;
import iansantos.login.api.SearchService;
import iansantos.login.model.StackOverflowQuestion;
import iansantos.login.model.StackOverflowSearch;
import iansantos.login.model.StackOverflowUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static iansantos.login.api.SearchService.BASE_URL;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private SearchAdapter adapter;
    private FirebaseAuth mAuth;
    private Retrofit retrofit;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<StackOverflowQuestion> questions = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        searchEditText = findViewById(R.id.search_editText);
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        mAuth = FirebaseAuth.getInstance();
        showAdvertising();
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayout.VERTICAL));
        adapter = new SearchAdapter(questions);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            recyclerView.setAdapter(adapter);
            getData(null);
        });
    }

    public void signOut(View view) {
        if (mAuth != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(String.format("Desconectar da conta %s?", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()));
            alert.setCancelable(false);
            alert.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                Toast.makeText(MainActivity.this, "Desconectado", Toast.LENGTH_LONG).show();
                finish();
            });
            alert.setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        } else {
            Toast.makeText(MainActivity.this, "Falha ao desconectar", Toast.LENGTH_LONG).show();
        }
    }

    public void showAdvertising() {
        if (LoginActivity.mInterstitialAd != null && LoginActivity.mInterstitialAd.isLoaded()) {
            LoginActivity.mInterstitialAd.show();
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet.");
        }
    }

    public void getData(View view) {
        recyclerView.setAdapter(adapter);
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
        hideKeyboard();
        SearchService searchService = retrofit.create(SearchService.class);
        Call<StackOverflowSearch> requestData = searchService.getSearch(searchEditText.getText().toString());
        requestData.enqueue(new Callback<StackOverflowSearch>() {
            @Override
            public void onResponse(@NonNull Call<StackOverflowSearch> call, @NonNull Response<StackOverflowSearch> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    StackOverflowSearch search = response.body();
                    for (StackOverflowQuestion question : Objects.requireNonNull(search).items) {
                        StackOverflowUser user = question.getOwner();
                        Log.i(TAG, String.format("%s \n%s \n%s", question.getTitle(), question.getLink(), user.getName()));
                    }
                    initAdapter(search.getItems());
                } else {
                    Log.e(TAG, String.valueOf(response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<StackOverflowSearch> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void initAdapter(List<StackOverflowQuestion> questionsList) {
        SearchAdapter adapter = new SearchAdapter(questionsList);
        recyclerView.setAdapter(adapter);
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            Objects.requireNonNull(inputManager).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}