package iansantos.login.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import iansantos.login.R;
import iansantos.login.model.User;

@SuppressWarnings("deprecation")
public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";
    private CollectionReference databaseReference;
    private EditText name;
    private EditText lastName;
    private EditText email;
    private EditText cpf;
    private EditText password;
    private EditText passwordConfirmation;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        View view = findViewById(R.id.constraint_layout);
        view.requestFocus();
        name = findViewById(R.id.name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        cpf = findViewById(R.id.cpf);
        password = findViewById(R.id.password);
        passwordConfirmation = findViewById(R.id.password_confirmation);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        databaseReference = database.collection("users");
    }

    public void registerNewUser(View view) {
        hideKeyboard();
        if (areValidFields() && passwordsMatch()) {
            dialog = ProgressDialog.show(RegisterActivity.this, "", "Criando sua conta...", true);
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    saveUser();
                    Toast.makeText(RegisterActivity.this, "Cadastrado", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "createUserWithEmail:success");
                    finish();
                } else {
                    dialog.dismiss();
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "Verifique os campos obrigatórios", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveUser() {
        User user = new User(name.getText().toString(), lastName.getText().toString(), email.getText().toString(), cpf.getText().toString(), password.getText().toString());
        Map<String, Object> User = new HashMap<>();
        User.put("fullName", user.getName() + " " + user.getLastName());
        User.put("email", user.getEmail());
        User.put("cpf", user.getCpf());
        User.put("password", user.getPassword());
        databaseReference.document(Objects.requireNonNull(mAuth.getUid())).set(User);
    }

    public boolean areValidFields() {
        boolean areValidFields = true;
        if (TextUtils.isEmpty(name.getText().toString().trim())) {
            name.setError("Digite o nome");
            areValidFields = false;
        }
        if (TextUtils.isEmpty(lastName.getText().toString().trim())) {
            lastName.setError("Digite o sobrenome");
            areValidFields = false;
        }
        if (TextUtils.isEmpty(email.getText().toString().trim())) {
            email.setError("Digite o email");
            areValidFields = false;
        }
        if (TextUtils.isEmpty(cpf.getText().toString().trim())) {
            cpf.setError("Digite o CPF");
            areValidFields = false;
        }
        if (TextUtils.isEmpty(password.getText().toString().trim())) {
            password.setError("Digite a senha");
            areValidFields = false;
        }
        if (TextUtils.isEmpty(passwordConfirmation.getText().toString().trim())) {
            passwordConfirmation.setError("Re-digite a senha");
            areValidFields = false;
        }
        return areValidFields;
    }

    public boolean passwordsMatch() {
        boolean passwordsMatch = true;
        if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            passwordsMatch = false;
            password.setError("Senhas não correspondem");
            passwordConfirmation.setError("Senhas não correspondem");
        }
        return passwordsMatch;
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