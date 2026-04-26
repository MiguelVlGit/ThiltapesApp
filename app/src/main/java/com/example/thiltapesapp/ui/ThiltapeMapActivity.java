package com.example.thiltapesapp.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.thiltapesapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ThiltapeMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitude;
    private double longitude;
    private String nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thiltape_map);

        nome = getIntent().getStringExtra("nome");
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        TextView tvNome = findViewById(R.id.tvThiltapeNomeDetalhe);
        TextView tvCoords = findViewById(R.id.tvThiltapeCoordsDetalhe);
        MaterialButton btnBack = findViewById(R.id.btnBackThiltapeMap);

        tvNome.setText(nome);
        tvCoords.setText(String.format(Locale.getDefault(), "%.6f, %.6f", latitude, longitude));

        btnBack.setOnClickListener(v -> finish());

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapThiltapeContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        LatLng pos = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(pos).title(nome));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17f));
    }
}
