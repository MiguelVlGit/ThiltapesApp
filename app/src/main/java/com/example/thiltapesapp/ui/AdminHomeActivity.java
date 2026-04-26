package com.example.thiltapesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.example.thiltapesapp.model.Jogo;
import com.example.thiltapesapp.utils.TokenManager;
import com.example.thiltapesapp.utils.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHomeActivity extends AppCompatActivity {

    private RecyclerView rvJogosAdmin;
    private ApiService apiService;

    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);

        fabAdd = findViewById(R.id.fabAdd);
        rvJogosAdmin = findViewById(R.id.rvJogosAdmin);
        rvJogosAdmin.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, AdminEditMapActivity.class);
            intent.putExtra("modo", "add");
            startActivity(intent);
        });
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarJogos();
    }

    private void carregarJogos() {
        apiService.listarJogos().enqueue(new Callback<List<Jogo>>() {
            @Override
            public void onResponse(Call<List<Jogo>> call, Response<List<Jogo>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Jogo> jogos = response.body();

                    RecyclerView.Adapter adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

                        @Override
                        public int getItemCount() {
                            return jogos.size();
                        }

                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = getLayoutInflater().inflate(R.layout.item_jogo_admin, parent, false);
                            return new RecyclerView.ViewHolder(view) {};
                        }

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

                            Jogo jogo = jogos.get(position);

                            TextView tvNome = holder.itemView.findViewById(R.id.tvNomeJogo);
                            MaterialButton btnEditar = holder.itemView.findViewById(R.id.btnEditar);
                            MaterialButton btnExcluir = holder.itemView.findViewById(R.id.btnExcluir);
                            MaterialButton btnThiltapes = holder.itemView.findViewById(R.id.btnThiltapes);
                            tvNome.setText(jogo.getNome());

                            btnThiltapes.setOnClickListener(v -> {
                                Intent intent = new Intent(AdminHomeActivity.this, AdminThiltapesActivity.class);
                                intent.putExtra("jogo_id", jogo.getId());
                                intent.putExtra("jogo_nome", jogo.getNome());
                                startActivity(intent);
                            });
                            btnEditar.setOnClickListener(v -> {
                                Intent intent = new Intent(AdminHomeActivity.this, AdminEditMapActivity.class);
                                intent.putExtra("modo", "edit");
                                intent.putExtra("jogo_id", jogo.getId());
                                intent.putExtra("nome", jogo.getNome());
                                intent.putExtra("data_inicio", jogo.getDataInicio());
                                intent.putExtra("data_fim", jogo.getDataFim());
                                startActivity(intent);
                            });

                            btnExcluir.setOnClickListener(v -> {

                                // chama API
                                jogo.setStatus("inativo");

                                apiService.atualizarJogo(jogo.getId(), jogo)
                                        .enqueue(new Callback<Jogo>() {
                                            @Override
                                            public void onResponse(Call<Jogo> call, Response<Jogo> response) {

                                                // REMOVE DA LISTA
                                                int pos = holder.getAdapterPosition();
                                                if (pos != RecyclerView.NO_POSITION) {
                                                    jogos.remove(pos);
                                                    notifyItemRemoved(pos);
                                                }

                                                Toast.makeText(AdminHomeActivity.this, "Removido", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(Call<Jogo> call, Throwable t) {
                                                Toast.makeText(AdminHomeActivity.this, "Erro ao excluir", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            });

                        }
                    };

                    rvJogosAdmin.setAdapter(adapter);

                } else {
                    Toast.makeText(AdminHomeActivity.this, "Erro ao carregar jogos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Jogo>> call, Throwable t) {
                Toast.makeText(AdminHomeActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        new TokenManager(this).clearToken();
        new UserManager(this).clearUser();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
