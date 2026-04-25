package com.example.thiltapesapp.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.example.thiltapesapp.model.Jogo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditMapActivity extends AppCompatActivity {

    private TextInputEditText etNome;
    private MaterialButton btnSalvar, btnVoltar;

    private ApiService apiService;

    private String modo;
    private int jogoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_edit_map);

        // 🔗 Views
        etNome = findViewById(R.id.etMissionNameV4);
        btnSalvar = findViewById(R.id.btnSaveV4);
        btnVoltar = findViewById(R.id.btnBackIconV4);

        apiService = ApiClient.getClient().create(ApiService.class);

        // 📥 Dados recebidos
        modo = getIntent().getStringExtra("modo");

        if ("edit".equals(modo)) {
            jogoId = getIntent().getIntExtra("jogo_id", -1);
            String nome = getIntent().getStringExtra("nome");

            etNome.setText(nome);
        }

        // 💾 Salvar
        btnSalvar.setOnClickListener(v -> salvar());

        // 🔙 Voltar
        btnVoltar.setOnClickListener(v -> finish());
    }

    private void salvar() {

        String nome = etNome.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Digite o nome da aventura", Toast.LENGTH_SHORT).show();
            return;
        }

        Jogo jogo = new Jogo();
        jogo.setNome(nome);

        // ➕ CRIAR
        if ("add".equals(modo)) {

            apiService.criarJogo(jogo).enqueue(new Callback<Jogo>() {
                @Override
                public void onResponse(Call<Jogo> call, Response<Jogo> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminEditMapActivity.this, "Jogo criado!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AdminEditMapActivity.this, "Erro ao criar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Jogo> call, Throwable t) {
                    Toast.makeText(AdminEditMapActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ✏️ EDITAR
        else if ("edit".equals(modo)) {

            apiService.atualizarJogo(jogoId, jogo).enqueue(new Callback<Jogo>() {
                @Override
                public void onResponse(Call<Jogo> call, Response<Jogo> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminEditMapActivity.this, "Atualizado!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AdminEditMapActivity.this, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Jogo> call, Throwable t) {
                    Toast.makeText(AdminEditMapActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}