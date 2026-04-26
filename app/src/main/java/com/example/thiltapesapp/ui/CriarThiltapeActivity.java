package com.example.thiltapesapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.example.thiltapesapp.model.Thiltape;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CriarThiltapeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextInputEditText etNome;
    private MaterialButton btnSalvar, btnBack;
    private TextView tvTapHint;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private Marker selectedMarker;

    private double latitude = 0;
    private double longitude = 0;

    private int jogoId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_thiltape);

        etNome = findViewById(R.id.etNomeThiltape);
        btnSalvar = findViewById(R.id.btnSalvarThiltape);
        btnBack = findViewById(R.id.btnBackCriar);
        tvTapHint = findViewById(R.id.tvTapHint);

        jogoId = getIntent().getIntExtra("jogo_id", -1);
        apiService = ApiClient.getClient(this).create(ApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapCriarContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        btnBack.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> salvarThiltape());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.setOnMapClickListener(latLng -> {
            latitude = latLng.latitude;
            longitude = latLng.longitude;

            if (selectedMarker != null) selectedMarker.remove();
            selectedMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Thiltape aqui"));

            tvTapHint.setVisibility(View.GONE);
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17f));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(location.getLatitude(), location.getLongitude()), 17f));
                    }
                });
            }
        }
    }

    private void salvarThiltape() {
        String nome = etNome.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Preencha o nome do thiltape", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude == 0 && longitude == 0) {
            Toast.makeText(this, "Toque no mapa para posicionar o thiltape", Toast.LENGTH_SHORT).show();
            return;
        }

        Thiltape thiltape = new Thiltape();
        thiltape.setNome(nome);
        thiltape.setIdJogo(jogoId);
        thiltape.setLatitude(latitude);
        thiltape.setLongitude(longitude);
        thiltape.setImagemUrl("imagem_fake_por_enquanto");

        apiService.criarThiltape(jogoId, thiltape).enqueue(new Callback<Thiltape>() {
            @Override
            public void onResponse(Call<Thiltape> call, Response<Thiltape> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CriarThiltapeActivity.this, "Thiltape criado!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CriarThiltapeActivity.this, "Erro ao salvar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Thiltape> call, Throwable t) {
                Toast.makeText(CriarThiltapeActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
