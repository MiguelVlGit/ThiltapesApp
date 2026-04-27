package com.example.thiltapesapp.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCollectionActivity extends AppCompatActivity {

    private RecyclerView rvColecao;
    private TextView tvContador;
    private ApiService apiService;
    private int jogoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_collection);

        jogoId = getIntent().getIntExtra("jogo_id", -1);
        rvColecao = findViewById(R.id.rvColecao);
        tvContador = findViewById(R.id.tvContadorColecao);
        rvColecao.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);
        carregarColecao();
    }

    private void carregarColecao() {
        apiService.listarThiltapes(jogoId).enqueue(new Callback<List<Thiltape>>() {
            @Override
            public void onResponse(Call<List<Thiltape>> call, Response<List<Thiltape>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Thiltape> thiltapes = response.body();
                    long capturados = 0;
                    for (Thiltape t : thiltapes) {
                        if (t.isCapturado()) capturados++;
                    }
                    tvContador.setText(capturados + " de " + thiltapes.size() + " capturados");
                    rvColecao.setAdapter(new ColecaoAdapter(thiltapes));
                } else {
                    Toast.makeText(UserCollectionActivity.this, "Erro ao carregar coleção", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Thiltape>> call, Throwable t) {
                Toast.makeText(UserCollectionActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class ColecaoAdapter extends RecyclerView.Adapter<ColecaoAdapter.VH> {

        private final List<Thiltape> thiltapes;

        ColecaoAdapter(List<Thiltape> thiltapes) {
            this.thiltapes = thiltapes;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_thiltape_collection, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Thiltape t = thiltapes.get(position);
            boolean capturado = t.isCapturado();

            holder.tvNome.setText(t.getNome());

            if (capturado) {
                String imagemBase64 = t.getImagemUrl();
                if (imagemBase64 != null && !imagemBase64.isEmpty()) {
                    try {
                        byte[] decoded = android.util.Base64.decode(imagemBase64, android.util.Base64.NO_WRAP);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                        holder.ivThiltape.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        holder.ivThiltape.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                }
                holder.ivThiltape.setVisibility(View.VISIBLE);
                holder.ivPlaceholder.setVisibility(View.GONE);
                holder.tvBadge.setText("✓");
                holder.tvBadge.setTextColor(0xFF7ED0A5);
                holder.tvStatus.setText("Capturado");
                holder.tvStatus.setTextColor(0xFF7ED0A5);
            } else {
                holder.ivThiltape.setVisibility(View.GONE);
                holder.ivPlaceholder.setVisibility(View.VISIBLE);
                holder.tvBadge.setText("");
                holder.tvStatus.setText("Disponível");
                holder.tvStatus.setTextColor(0xFFA0A0A0);
                holder.tvNome.setTextColor(0xFFA0A0A0);
            }
        }
        @Override
        public int getItemCount() {
            return thiltapes.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvBadge, tvNome, tvStatus;
            ImageView ivThiltape;
            View ivPlaceholder;

            VH(@NonNull View itemView) {
                super(itemView);
                tvBadge       = itemView.findViewById(R.id.tvStatusBadge);
                tvNome        = itemView.findViewById(R.id.tvNomeThiltape);
                tvStatus      = itemView.findViewById(R.id.tvStatusThiltape);
                ivThiltape    = itemView.findViewById(R.id.ivThiltape);
                ivPlaceholder = itemView.findViewById(R.id.ivPlaceholder);
            }
        }
    }
}
