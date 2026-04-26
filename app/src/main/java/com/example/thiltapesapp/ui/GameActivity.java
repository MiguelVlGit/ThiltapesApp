package com.example.thiltapesapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.example.thiltapesapp.model.LogCaptura;
import com.example.thiltapesapp.model.Thiltape;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final float CAPTURE_RADIUS_METERS = 50f;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ApiService apiService;
    private int jogoId;

    private static final float HINT_COLD_METERS   = 300f;
    private static final float HINT_COOL_METERS   = 150f;
    private static final float HINT_WARM_METERS   = 75f;

    private List<Thiltape> thiltapeList = new ArrayList<>();
    private Thiltape nearbyThiltape = null;
    private Location lastLocation = null;
    private float previousNearestDistance = Float.MAX_VALUE;

    private TextView tvMissionName, tvProgress, tvNearbyName, tvHintZone, tvHintTrend;
    private ProgressBar progressBar;
    private CardView cardNearby, cardHint;
    private MaterialButton btnCapturar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        jogoId = getIntent().getIntExtra("jogo_id", -1);
        String jogoNome = getIntent().getStringExtra("jogo_nome");

        apiService = ApiClient.getClient(this).create(ApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tvMissionName = findViewById(R.id.tvMissionName);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.missionProgress);
        cardNearby = findViewById(R.id.cardNearby);
        tvNearbyName = findViewById(R.id.tvNearbyName);
        btnCapturar = findViewById(R.id.btnCapturar);
        cardHint = findViewById(R.id.cardHint);
        tvHintZone = findViewById(R.id.tvHintZone);
        tvHintTrend = findViewById(R.id.tvHintTrend);

        if (jogoNome != null) tvMissionName.setText("Missão: " + jogoNome);

        btnCapturar.setOnClickListener(v -> {
            if (nearbyThiltape != null && lastLocation != null) {
                capturarThiltape(nearbyThiltape, lastLocation);
            }
        });

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ExtendedFloatingActionButton btnInventory = findViewById(R.id.btnInventory);
        btnInventory.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserCollectionActivity.class);
            intent.putExtra("jogo_id", jogoId);
            startActivity(intent);
        });

        apiService.entrarNoJogo(jogoId).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location location = result.getLastLocation();
                if (location != null) {
                    lastLocation = location;
                    verificarProximidade(location);
                }
            }
        };
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        FloatingActionButton fabLocation = findViewById(R.id.fabLocation);
        fabLocation.setOnClickListener(v -> moverParaLocalizacaoAtual());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            iniciarAtualizacoesLocalizacao();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }

        carregarThiltapes();
    }

    private void iniciarAtualizacoesLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(3000)
                .build();

        fusedLocationClient.requestLocationUpdates(request, locationCallback, getMainLooper());

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && googleMap != null) {
                lastLocation = location;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 17f));
            }
        });
    }

    private void carregarThiltapes() {
        apiService.listarThiltapes(jogoId).enqueue(new Callback<List<Thiltape>>() {
            @Override
            public void onResponse(Call<List<Thiltape>> call, Response<List<Thiltape>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    thiltapeList.clear();
                    previousNearestDistance = Float.MAX_VALUE;
                    for (Thiltape t : response.body()) {
                        if (!"capturado".equalsIgnoreCase(t.getStatus())) {
                            thiltapeList.add(t);
                        }
                    }
                    atualizarProgresso(response.body());
                    if (lastLocation != null) verificarProximidade(lastLocation);
                } else {
                    Toast.makeText(GameActivity.this, "Erro ao carregar thiltapes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Thiltape>> call, Throwable t) {
                Toast.makeText(GameActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarProximidade(Location location) {
        if (thiltapeList.isEmpty()) {
            cardNearby.setVisibility(View.GONE);
            cardHint.setVisibility(View.GONE);
            return;
        }

        Thiltape maisProximo = null;
        float menorDistancia = Float.MAX_VALUE;

        for (Thiltape t : thiltapeList) {
            float[] resultado = new float[1];
            Location.distanceBetween(
                    location.getLatitude(), location.getLongitude(),
                    t.getLatitude(), t.getLongitude(),
                    resultado);
            if (resultado[0] < menorDistancia) {
                menorDistancia = resultado[0];
                maisProximo = t;
            }
        }

        // Card de captura
        if (menorDistancia < CAPTURE_RADIUS_METERS) {
            nearbyThiltape = maisProximo;
            tvNearbyName.setText(nearbyThiltape.getNome());
            cardNearby.setVisibility(View.VISIBLE);
            cardHint.setVisibility(View.GONE);
        } else {
            nearbyThiltape = null;
            cardNearby.setVisibility(View.GONE);
            atualizarDica(menorDistancia);
        }

        previousNearestDistance = menorDistancia;
    }

    private void atualizarDica(float distancia) {
        String zona;
        int cor;

        if (distancia > HINT_COLD_METERS) {
            zona = "Gelado";
            cor = 0xFF90CAF9; // azul claro
        } else if (distancia > HINT_COOL_METERS) {
            zona = "Frio";
            cor = 0xFF4FC3F7; // azul
        } else if (distancia > HINT_WARM_METERS) {
            zona = "Morno";
            cor = 0xFFFFB74D; // laranja claro
        } else {
            zona = "Quente";
            cor = 0xFFEF5350; // vermelho
        }

        String tendencia = "";
        int corTendencia = 0xFFAAAAAA;
        if (previousNearestDistance != Float.MAX_VALUE) {
            float diff = previousNearestDistance - distancia;
            if (diff > 2f) {
                tendencia = "aproximando";
                corTendencia = 0xFF7ED0A5;
            } else if (diff < -2f) {
                tendencia = "afastando";
                corTendencia = 0xFFEF9A9A;
            }
        }

        tvHintZone.setText(zona);
        tvHintZone.setTextColor(cor);
        tvHintTrend.setText(tendencia);
        tvHintTrend.setTextColor(corTendencia);
        cardHint.setVisibility(View.VISIBLE);
    }

    private void atualizarProgresso(List<Thiltape> todos) {
        int total = todos.size();
        int capturados = 0;
        for (Thiltape t : todos) {
            if ("capturado".equalsIgnoreCase(t.getStatus())) capturados++;
        }
        tvProgress.setText(capturados + "/" + total + " Thiltapes");
        progressBar.setMax(total);
        progressBar.setProgress(capturados);
    }

    private void capturarThiltape(Thiltape thiltape, Location location) {
        btnCapturar.setEnabled(false);

        Map<String, Double> body = new HashMap<>();
        body.put("latitude", location.getLatitude());
        body.put("longitude", location.getLongitude());

        apiService.capturarThiltape(jogoId, thiltape.getId(), body).enqueue(new Callback<LogCaptura>() {
            @Override
            public void onResponse(Call<LogCaptura> call, Response<LogCaptura> response) {
                btnCapturar.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(GameActivity.this, thiltape.getNome() + " capturado! +XP", Toast.LENGTH_SHORT).show();
                    thiltapeList.remove(thiltape);
                    nearbyThiltape = null;
                    cardNearby.setVisibility(View.GONE);
                    carregarThiltapes();
                } else {
                    Toast.makeText(GameActivity.this, "Não foi possível capturar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LogCaptura> call, Throwable t) {
                btnCapturar.setEnabled(true);
                Toast.makeText(GameActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moverParaLocalizacaoAtual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && googleMap != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 17f));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            iniciarAtualizacoesLocalizacao();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }
            iniciarAtualizacoesLocalizacao();
        }
    }
}
