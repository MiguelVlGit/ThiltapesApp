package com.example.thiltapesapp.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

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

    private TextInputEditText etNome, etDataInicio, etDataFim;
    private MaterialButton btnSalvar, btnVoltar, btnAddPoint, btnRemovePoint;

    private ApiService apiService;

    private String modo;
    private int jogoId = -1;
    private String jogoNome = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_edit_map);

        etNome = findViewById(R.id.etMissionNameV4);
        etDataInicio = findViewById(R.id.etDataInicio);
        etDataFim = findViewById(R.id.etDataFim);
        btnSalvar = findViewById(R.id.btnSaveV4);
        btnVoltar = findViewById(R.id.btnBackIconV4);
        btnAddPoint = findViewById(R.id.btnAddPointV4);
        btnRemovePoint = findViewById(R.id.btnRemovePointV4);

        etDataInicio.setOnClickListener(v -> mostrarDatePicker(etDataInicio));
        etDataFim.setOnClickListener(v -> mostrarDatePicker(etDataFim));

        apiService = ApiClient.getClient(this).create(ApiService.class);

        modo = getIntent().getStringExtra("modo");

        if ("edit".equals(modo)) {
            jogoId = getIntent().getIntExtra("jogo_id", -1);
            jogoNome = getIntent().getStringExtra("nome");
            etNome.setText(jogoNome);

            String dataInicio = getIntent().getStringExtra("data_inicio");
            String dataFim = getIntent().getStringExtra("data_fim");
            if (dataInicio != null) etDataInicio.setText(dataInicio);
            if (dataFim != null) etDataFim.setText(dataFim);

            btnAddPoint.setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminThiltapesActivity.class);
                intent.putExtra("jogo_id", jogoId);
                intent.putExtra("jogo_nome", jogoNome);
                startActivity(intent);
            });

            btnRemovePoint.setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminThiltapesActivity.class);
                intent.putExtra("jogo_id", jogoId);
                intent.putExtra("jogo_nome", jogoNome);
                startActivity(intent);
            });
        } else {
            // Jogo ainda não existe — esconde os botões de ponto
            btnAddPoint.setVisibility(android.view.View.GONE);
            btnRemovePoint.setVisibility(android.view.View.GONE);
        }

        btnSalvar.setOnClickListener(v -> salvar());
        btnVoltar.setOnClickListener(v -> finish());
    }

    private void mostrarDatePicker(TextInputEditText campo) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String data = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            campo.setText(data);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void salvar() {
        String nome = etNome.getText().toString().trim();
        String dataInicio = etDataInicio.getText().toString().trim();
        String dataFim = etDataFim.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Digite o nome da aventura", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dataInicio.isEmpty() || dataFim.isEmpty()) {
            Toast.makeText(this, "Selecione as datas de início e fim", Toast.LENGTH_SHORT).show();
            return;
        }

        Jogo jogo = new Jogo();
        jogo.setNome(nome);
        jogo.setDataInicio(dataInicio);
        jogo.setDataFim(dataFim);

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
