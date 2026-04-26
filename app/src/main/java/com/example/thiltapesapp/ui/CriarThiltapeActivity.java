package com.example.thiltapesapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.thiltapesapp.R;
import com.example.thiltapesapp.api.ApiClient;
import com.example.thiltapesapp.api.ApiService;
import com.example.thiltapesapp.model.Thiltape;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CriarThiltapeActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;

    private TextInputEditText etNome;
    private ImageView imgPreview;
    private MaterialButton btnFoto, btnSalvar;

    private FusedLocationProviderClient fusedLocationClient;

    private double latitude = 0;
    private double longitude = 0;

    private Bitmap fotoBitmap;

    private int jogoId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_thiltape);

        etNome = findViewById(R.id.etNomeThiltape);
        imgPreview = findViewById(R.id.imgPreview);
        btnFoto = findViewById(R.id.btnFoto);
        btnSalvar = findViewById(R.id.btnSalvarThiltape);

        jogoId = getIntent().getIntExtra("jogo_id", -1);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        pegarLocalizacao();

        btnFoto.setOnClickListener(v -> abrirCamera());

        btnSalvar.setOnClickListener(v -> salvarThiltape());
    }

    private void pegarLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        });
    }

    private void abrirCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            fotoBitmap = (Bitmap) data.getExtras().get("data");
            imgPreview.setImageBitmap(fotoBitmap);
        }
    }

    private void salvarThiltape() {

        String nome = etNome.getText().toString().trim();

        if (nome.isEmpty() || fotoBitmap == null) {
            Toast.makeText(this, "Preencha nome e tire uma foto", Toast.LENGTH_SHORT).show();
            return;
        }

        Thiltape thiltape = new Thiltape();
        thiltape.setNome(nome);
        thiltape.setIdJogo(jogoId);
        thiltape.setLatitude(latitude);
        thiltape.setLongitude(longitude);

        // ⚠️ Aqui você precisaria subir imagem pro backend (URL)
        thiltape.setImagemUrl("imagem_fake_por_enquanto");

        apiService.criarThiltape(jogoId, thiltape)
                .enqueue(new Callback<Thiltape>() {
                    @Override
                    public void onResponse(Call<Thiltape> call, Response<Thiltape> response) {
                        Toast.makeText(CriarThiltapeActivity.this, "Thiltape criado!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Thiltape> call, Throwable t) {
                        Toast.makeText(CriarThiltapeActivity.this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
