package com.example.thiltapesapp.api;

import com.example.thiltapesapp.model.Jogo;
import com.example.thiltapesapp.model.LogCaptura;
import com.example.thiltapesapp.model.RankingEntry;
import com.example.thiltapesapp.model.Thiltape;
import com.example.thiltapesapp.model.TokenResponse;
import com.example.thiltapesapp.model.Usuario;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // --- AUTH ---
    @POST("auth/registrar")
    Call<Void> registrar(@Body Map<String, String> body);

    @POST("auth/login")
    Call<TokenResponse> login(@Body Map<String, String> body);

    // --- USUÁRIOS ---
    @GET("usuarios/me")
    Call<Usuario> getMe();
    @GET("usuarios/")
    Call<List<Usuario>> listarUsuarios();

    @GET("usuarios/{usuario_id}")
    Call<Usuario> getUsuario(@Path("usuario_id") int id);

    @PATCH("usuarios/{usuario_id}")
    Call<Usuario> atualizarUsuario(@Path("usuario_id") int id, @Body Usuario usuario);

    // --- JOGOS ---
    @GET("jogos/")
    Call<List<Jogo>> listarJogos();

    @POST("jogos/")
    Call<Jogo> criarJogo(@Body Jogo jogo);

    @GET("jogos/{jogo_id}")
    Call<Jogo> getJogo(@Path("jogo_id") int id);

    @PATCH("jogos/{jogo_id}")
    Call<Jogo> atualizarJogo(@Path("jogo_id") int id, @Body Jogo jogo);

    @POST("jogos/{jogo_id}/entrar")
    Call<Void> entrarNoJogo(@Path("jogo_id") int id);

    @GET("jogos/{jogo_id}/ranking")
    Call<List<RankingEntry>> getRanking(@Path("jogo_id") int id);

    // --- THILTAPES ---
    @POST("jogos/{jogo_id}/thiltapes")
    Call<Thiltape> criarThiltape(@Path("jogo_id") int jogoId, @Body Thiltape thiltape);

    @GET("jogos/{jogo_id}/thiltapes")
    Call<List<Thiltape>> listarThiltapes(@Path("jogo_id") int jogoId);

    @GET("jogos/{jogo_id}/thiltapes/{thiltape_id}")
    Call<Thiltape> getThiltape(@Path("jogo_id") int jogoId, @Path("thiltape_id") int thiltapeId);

    @PATCH("jogos/{jogo_id}/thiltapes/{thiltape_id}")
    Call<Thiltape> atualizarThiltape(@Path("jogo_id") int jogoId, @Path("thiltape_id") int thiltapeId, @Body Thiltape thiltape);

    @POST("jogos/{jogo_id}/thiltapes/{thiltape_id}/capturar")
    Call<LogCaptura> capturarThiltape(@Path("jogo_id") int jogoId, @Path("thiltape_id") int thiltapeId, @Body Map<String, Double> body);
}
