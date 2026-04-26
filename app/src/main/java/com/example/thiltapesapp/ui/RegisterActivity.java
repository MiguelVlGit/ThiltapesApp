package com.example.thiltapesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNome, etLogin, etSenha, etConfirmarSenha;
    private MaterialButton btnCriarConta;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNome = findViewById(R.id.etNome);
        etLogin = findViewById(R.id.etLogin);
        etSenha = findViewById(R.id.etSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        btnCriarConta = findViewById(R.id.btnCriarConta);
        TextView tvJaTenhoConta = findViewById(R.id.tvJaTenhoConta);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        btnCriarConta.setOnClickListener(v -> performRegister());
        tvJaTenhoConta.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String nome = etNome.getText() != null ? etNome.getText().toString().trim() : "";
        String login = etLogin.getText() != null ? etLogin.getText().toString().trim() : "";
        String senha = etSenha.getText() != null ? etSenha.getText().toString().trim() : "";
        String confirmar = etConfirmarSenha.getText() != null ? etConfirmarSenha.getText().toString().trim() : "";

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(confirmar)) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("nome", nome);
        body.put("login", login);
        body.put("senha", senha);

        btnCriarConta.setEnabled(false);

        apiService.registrar(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnCriarConta.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Conta criada! Faça login.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "sem corpo";
                        Log.e("Register", "HTTP " + response.code() + " — " + errorBody);
                        Toast.makeText(RegisterActivity.this, "Erro " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Erro " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnCriarConta.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
