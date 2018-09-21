package iansantos.login.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.view.inputmethod.EditorInfo;
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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        mAuth = FirebaseAuth.getInstance();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayout.VERTICAL));
        adapter = new SearchAdapter(questions);
        recyclerView.setAdapter(adapter);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getData(null);
                return true;
            }
            return false;
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!searchEditText.getText().toString().trim().isEmpty()) {
                swipeRefreshLayout.setRefreshing(true);
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(adapter);
                getData(null);
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "O campo de busca não pode estar vazio", Toast.LENGTH_SHORT).show();
            }
        });
        showAdvertising();
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
        if (!searchEditText.getText().toString().trim().isEmpty()) {
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(progressBar.getVisibility() == View.GONE ? View.VISIBLE : View.INVISIBLE);
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
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, t.getMessage());
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "O campo de busca não pode estar vazio", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAdapter(List<StackOverflowQuestion> questionsList) {
        if (!questionsList.isEmpty()) {
            SearchAdapter adapter = new SearchAdapter(questionsList);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener((int position) -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                String title = questionsList.get(position).getTitle().replace("&#39;", "\'").replace("&amp;", "&").replace("&quot;", "\"");
                alert.setMessage(String.format("Ir para o link da questão: \"%s\" ?", title));
                alert.setCancelable(true);
                alert.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    String url = questionsList.get(position).getLink();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                });
                alert.setNegativeButton(android.R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
                alert.show();
            });
        } else {
            Toast.makeText(MainActivity.this, "Não foram encontrados resultados para a sua busca", Toast.LENGTH_SHORT).show();
        }
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