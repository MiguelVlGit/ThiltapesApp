package com.example.thiltapesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.example.thiltapesapp.model.Jogo;
import com.example.thiltapesapp.model.Usuario;
import com.example.thiltapesapp.utils.TokenManager;
import com.example.thiltapesapp.utils.UserManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private ApiService apiService;
    private RecyclerView rvJogos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        rvJogos = findViewById(R.id.rvJogos);

        // IMPORTANTE
        rvJogos.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);

        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
        carregarDadosUsuario();
        carregarJogos();
    }

    private void carregarDadosUsuario() {
        apiService.getMe().enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();
                    tvWelcome.setText("Bem-vindo, " + usuario.getLogin() + "!");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(UserHomeActivity.this, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show();
            }
        });
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
                            View view = getLayoutInflater().inflate(R.layout.item_jogo, parent, false);
                            return new RecyclerView.ViewHolder(view) {};
                        }

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                            Jogo jogo = jogos.get(position);

                            TextView tvNome = holder.itemView.findViewById(R.id.tvNomeJogo);
                            MaterialButton btnJogar = holder.itemView.findViewById(R.id.btnJogar);
                            MaterialButton btnRanking = holder.itemView.findViewById(R.id.btnRanking);

                            tvNome.setText(jogo.getNome());

                            btnJogar.setOnClickListener(v -> {
                                Intent intent = new Intent(UserHomeActivity.this, GameActivity.class);
                                intent.putExtra("jogo_id", jogo.getId());
                                intent.putExtra("jogo_nome", jogo.getNome());
                                startActivity(intent);
                            });

                            btnRanking.setOnClickListener(v -> {
                                Intent intent = new Intent(UserHomeActivity.this, RankActivity.class);
                                intent.putExtra("jogo_id", jogo.getId());
                                startActivity(intent);
                            });
                        }
                    };

                    rvJogos.setAdapter(adapter);

                } else {
                    Toast.makeText(UserHomeActivity.this, "Erro ao carregar jogos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Jogo>> call, Throwable t) {
                Toast.makeText(UserHomeActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
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
