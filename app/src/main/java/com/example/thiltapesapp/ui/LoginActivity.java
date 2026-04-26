package com.example.thiltapesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.example.thiltapesapp.model.TokenResponse;
import com.example.thiltapesapp.model.Usuario;
import com.example.thiltapesapp.utils.TokenManager;
import com.example.thiltapesapp.utils.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsuario;
    private TextInputEditText etSenha;
    private MaterialButton btnEntrar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etSenha = findViewById(R.id.etSenha);
        btnEntrar = findViewById(R.id.btnEntrar);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        btnEntrar.setOnClickListener(v -> performLogin());
        findViewById(R.id.tvCriarConta).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void performLogin() {
        String login = etUsuario.getText() != null ? etUsuario.getText().toString().trim() : "";
        String senha = etSenha.getText() != null ? etSenha.getText().toString().trim() : "";

        if (login.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> loginData = new HashMap<>();
        loginData.put("login", login);
        loginData.put("senha", senha);

        btnEntrar.setEnabled(false);

        apiService.login(loginData).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                btnEntrar.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {

                    String token = response.body().getAccessToken();

                    // Salva token
                    TokenManager tokenManager = new TokenManager(LoginActivity.this);
                    tokenManager.saveToken(token);

                    // Busca dados do usuário
                    apiService.getMe().enqueue(new Callback<Usuario>() {
                        @Override
                        public void onResponse(Call<Usuario> call, Response<Usuario> responseMe) {
                            if (responseMe.isSuccessful() && responseMe.body() != null) {

                                Usuario user = responseMe.body();

                                // 🔥 SALVAR USUÁRIO
                                UserManager userManager = new UserManager(LoginActivity.this);
                                userManager.saveUser(user.getId(), user.getLogin());

                                // Redirecionamento
                                if ("administrador".equalsIgnoreCase(user.getTipo())) {
                                    startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                                } else {
                                    startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                                }

                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Erro ao obter perfil", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Usuario> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(LoginActivity.this, "Usuário ou senha incorretos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                btnEntrar.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
