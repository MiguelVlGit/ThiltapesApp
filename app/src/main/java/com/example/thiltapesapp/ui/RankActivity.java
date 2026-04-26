package com.example.thiltapesapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.example.thiltapesapp.model.RankingEntry;
import com.example.thiltapesapp.utils.UserManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankActivity extends AppCompatActivity {

    private RecyclerView rvRanking;
    private CardView cardMinhaPos;
    private TextView tvMinhaPosicao, tvMinhaPontuacao;
    private ApiService apiService;
    private int jogoId;
    private int meuUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        jogoId = getIntent().getIntExtra("jogo_id", -1);
        meuUserId = new UserManager(this).getUserId();

        rvRanking = findViewById(R.id.rvRanking);
        cardMinhaPos = findViewById(R.id.cardMinhaPos);
        tvMinhaPosicao = findViewById(R.id.tvMinhaPosicao);
        tvMinhaPontuacao = findViewById(R.id.tvMinhaPontuacao);

        rvRanking.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient(this).create(ApiService.class);
        carregarRanking();
    }

    private void carregarRanking() {
        apiService.getRanking(jogoId).enqueue(new Callback<List<RankingEntry>>() {
            @Override
            public void onResponse(Call<List<RankingEntry>> call, Response<List<RankingEntry>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RankingEntry> ranking = response.body();
                    mostrarMinhaPosicao(ranking);
                    rvRanking.setAdapter(new RankingAdapter(ranking, meuUserId));
                } else {
                    Toast.makeText(RankActivity.this, "Erro ao carregar ranking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RankingEntry>> call, Throwable t) {
                Toast.makeText(RankActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarMinhaPosicao(List<RankingEntry> ranking) {
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).getIdUsuario() == meuUserId) {
                cardMinhaPos.setVisibility(View.VISIBLE);
                tvMinhaPosicao.setText("#" + (i + 1) + " — " + ranking.get(i).getNomeUsuario());
                tvMinhaPontuacao.setText(ranking.get(i).getPontuacao() + " XP");
                return;
            }
        }
    }

    private static class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.VH> {

        private final List<RankingEntry> ranking;
        private final int meuUserId;

        RankingAdapter(List<RankingEntry> ranking, int meuUserId) {
            this.ranking = ranking;
            this.meuUserId = meuUserId;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ranking, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            RankingEntry entry = ranking.get(position);
            int pos = position + 1;

            holder.tvPosicao.setText("#" + pos);
            holder.tvNome.setText(entry.getNomeUsuario());
            holder.tvPontuacao.setText(entry.getPontuacao() + " XP");

            boolean souEu = entry.getIdUsuario() == meuUserId;
            int bgColor = souEu ? 0xFF254D3D : 0xFF1B3A2E;
            holder.card.setCardBackgroundColor(bgColor);

            // Medalhas para top 3
            if (pos == 1) holder.tvPosicao.setText("🥇");
            else if (pos == 2) holder.tvPosicao.setText("🥈");
            else if (pos == 3) holder.tvPosicao.setText("🥉");
        }

        @Override
        public int getItemCount() {
            return ranking.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvPosicao, tvNome, tvPontuacao;
            CardView card;

            VH(@NonNull View itemView) {
                super(itemView);
                tvPosicao = itemView.findViewById(R.id.tvPosicao);
                tvNome = itemView.findViewById(R.id.tvNomeJogador);
                tvPontuacao = itemView.findViewById(R.id.tvPontuacao);
                card = itemView.findViewById(R.id.cardRankingItem);
            }
        }
    }
}
