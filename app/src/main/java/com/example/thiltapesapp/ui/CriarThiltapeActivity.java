package com.example.thiltapesapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CriarThiltapeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;

    private TextInputEditText etNome;
    private MaterialButton btnSalvar, btnBack, btnFoto;
    private TextView tvTapHint;
    private ImageView ivPreviewFoto;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private Marker selectedMarker;

    private double latitude = 0;
    private double longitude = 0;
    private Uri fotoUri;
    private File fotoFile;

    private int jogoId;
    private ApiService apiService;

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && fotoUri != null) {
                    ivPreviewFoto.setImageURI(fotoUri);
                    ivPreviewFoto.setVisibility(View.VISIBLE);
                    btnFoto.setText("Trocar foto");
                } else {
                    Toast.makeText(this, "Foto cancelada", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_thiltape);

        etNome        = findViewById(R.id.etNomeThiltape);
        btnSalvar     = findViewById(R.id.btnSalvarThiltape);
        btnBack       = findViewById(R.id.btnBackCriar);
        btnFoto       = findViewById(R.id.btnTirarFoto);
        tvTapHint     = findViewById(R.id.tvTapHint);
        ivPreviewFoto = findViewById(R.id.ivPreviewFoto);

        jogoId = getIntent().getIntExtra("jogo_id", -1);
        apiService = ApiClient.getClient(this).create(ApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapCriarContainer, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        btnBack.setOnClickListener(v -> finish());
        btnFoto.setOnClickListener(v -> abrirCamera());
        btnSalvar.setOnClickListener(v -> salvarThiltape());
    }

    private void abrirCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        lancarCamera();
    }

    private void lancarCamera() {
        try {
            fotoFile = criarArquivoFoto();
            fotoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", fotoFile);
            cameraLauncher.launch(fotoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Erro ao preparar câmera", Toast.LENGTH_SHORT).show();
        }
    }

    private File criarArquivoFoto() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("THILTAPE_" + timeStamp, ".jpg", storageDir);
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17f));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude()), 17f));
                        }
                    });
                }
            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lancarCamera();
            } else {
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
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
        if (fotoFile == null || !fotoFile.exists()) {
            Toast.makeText(this, "Tire uma foto do thiltape", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagemBase64;
        try {
            FileInputStream fis = new FileInputStream(fotoFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n;
            while ((n = fis.read(buffer)) != -1) baos.write(buffer, 0, n);
            fis.close();
            imagemBase64 = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP);
        } catch (IOException e) {
            Toast.makeText(this, "Erro ao processar a foto", Toast.LENGTH_SHORT).show();
            return;
        }

        Thiltape thiltape = new Thiltape();
        thiltape.setNome(nome);
        thiltape.setIdJogo(jogoId);
        thiltape.setLatitude(latitude);
        thiltape.setLongitude(longitude);
        thiltape.setImagemUrl(imagemBase64);

        apiService.criarThiltape(jogoId, thiltape).enqueue(new Callback<Thiltape>() {
            @Override
            public void onResponse(Call<Thiltape> call, Response<Thiltape> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CriarThiltapeActivity.this, "Thiltape criado!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CriarThiltapeActivity.this,
                            "Erro ao salvar (código " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Thiltape> call, Throwable t) {
                Toast.makeText(CriarThiltapeActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}