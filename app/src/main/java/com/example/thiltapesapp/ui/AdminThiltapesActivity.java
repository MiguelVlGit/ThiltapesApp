package com.example.thiltapesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.example.thiltapesapp.model.Thiltape;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminThiltapesActivity extends AppCompatActivity {

    private RecyclerView rvThiltapes;
    private ApiService apiService;
    private int jogoId;
    private String jogoNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_thiltapes);

        jogoId = getIntent().getIntExtra("jogo_id", -1);
        jogoNome = getIntent().getStringExtra("jogo_nome");

        TextView tvNomeJogo = findViewById(R.id.tvNomeJogoAdmin);
        if (jogoNome != null) tvNomeJogo.setText(jogoNome);

        rvThiltapes = findViewById(R.id.rvThiltapesAdmin);
        rvThiltapes.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);

        FloatingActionButton fabAdicionar = findViewById(R.id.fabAdicionarThiltape);
        fabAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CriarThiltapeActivity.class);
            intent.putExtra("jogo_id", jogoId);
            startActivity(intent);
        });

        carregarThiltapes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarThiltapes();
    }

    private void carregarThiltapes() {
        apiService.listarThiltapes(jogoId).enqueue(new Callback<List<Thiltape>>() {
            @Override
            public void onResponse(Call<List<Thiltape>> call, Response<List<Thiltape>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rvThiltapes.setAdapter(new ThiltapeAdminAdapter(response.body()));
                } else {
                    Toast.makeText(AdminThiltapesActivity.this, "Erro ao carregar thiltapes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Thiltape>> call, Throwable t) {
                Toast.makeText(AdminThiltapesActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ThiltapeAdminAdapter extends RecyclerView.Adapter<ThiltapeAdminAdapter.VH> {

        private final List<Thiltape> thiltapes;

        ThiltapeAdminAdapter(List<Thiltape> thiltapes) {
            this.thiltapes = thiltapes;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_thiltape_admin, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Thiltape t = thiltapes.get(position);
            holder.tvNome.setText(t.getNome());

            holder.btnVerMapa.setOnClickListener(v -> {
                Intent intent = new Intent(AdminThiltapesActivity.this, ThiltapeMapActivity.class);
                intent.putExtra("nome", t.getNome());
                intent.putExtra("latitude", t.getLatitude());
                intent.putExtra("longitude", t.getLongitude());
                startActivity(intent);
            });

            boolean ativo = !"inativo".equalsIgnoreCase(t.getStatus());
            holder.tvStatus.setText(ativo ? "Ativo" : "Inativo");
            holder.tvStatus.setTextColor(ativo ? 0xFF7ED0A5 : 0xFFA0A0A0);

            holder.btnExcluir.setOnClickListener(v -> {
                Thiltape atualizar = new Thiltape();
                atualizar.setStatus("inativo");

                apiService.atualizarThiltape(jogoId, t.getId(), atualizar)
                        .enqueue(new Callback<Thiltape>() {
                            @Override
                            public void onResponse(Call<Thiltape> call, Response<Thiltape> response) {
                                int pos = holder.getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    thiltapes.remove(pos);
                                    notifyItemRemoved(pos);
                                }
                                Toast.makeText(AdminThiltapesActivity.this, "Thiltape removido", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Thiltape> call, Throwable t2) {
                                Toast.makeText(AdminThiltapesActivity.this, "Erro ao remover", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }

        @Override
        public int getItemCount() {
            return thiltapes.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvNome, tvStatus;
            MaterialButton btnExcluir, btnVerMapa;

            VH(@NonNull View itemView) {
                super(itemView);
                tvNome = itemView.findViewById(R.id.tvNomeThiltapeAdmin);
                tvStatus = itemView.findViewById(R.id.tvStatusThiltapeAdmin);
                btnExcluir = itemView.findViewById(R.id.btnExcluirThiltape);
                btnVerMapa = itemView.findViewById(R.id.btnVerMapa);
            }
        }
    }
}
