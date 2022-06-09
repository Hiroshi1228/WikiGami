package com.wikigami.wikigami;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrearTutorial extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference bd;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    // Datos del tutorial
    private String nombre;
    private String descripcion;
    private String idYTVideo;
    private String idCreador;
    //miniatura

    private EditText tutNombre;
    private EditText tutDescripcion;
    private EditText tutIdVideo;
    private Button btnTutMiniatura;
    private TextView idUsuario;
    private ImageView imageView;
    private Button btnGuardar;
    private ProgressBar progressBar;
    private Spinner spinnerDificultad;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_tutorial);

        tutNombre = findViewById(R.id.tut_nombre);
        tutDescripcion = findViewById(R.id.tut_descripcion);
        tutIdVideo = findViewById(R.id.tut_id_video);
        btnTutMiniatura = findViewById(R.id.tut_miniatura);
        idUsuario = findViewById(R.id.id_usuario);
        imageView = findViewById(R.id.image_view);
        btnGuardar = findViewById(R.id.btn_guardar_tutorial);
        progressBar = findViewById(R.id.progress_bar);
        spinnerDificultad = findViewById(R.id.spinnerDificultad);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dificultades, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerDificultad.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        bd = FirebaseDatabase.getInstance().getReference("TUTORIALES");
        storageReference = FirebaseStorage.getInstance().getReference("TUTORIALES");

        idCreador = user.getUid().toString();
        idUsuario.setText("Id de creador: " + user.getUid());

        btnTutMiniatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTutorial();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void guardarTutorial() {
        String dificultadSeleccionada = (String) spinnerDificultad.getSelectedItem();

        nombre = tutNombre.getText().toString();
        descripcion = tutDescripcion.getText().toString();
        idYTVideo = tutIdVideo.getText().toString();
        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(CrearTutorial.this, "Debe agregar un nombre", Toast.LENGTH_SHORT).show();
            tutNombre.requestFocus();
        } else if (TextUtils.isEmpty(descripcion)) {
            Toast.makeText(CrearTutorial.this, "Debe agregar una descripción", Toast.LENGTH_SHORT).show();
            tutDescripcion.requestFocus();
        } else if (TextUtils.isEmpty(idYTVideo)) {
            Toast.makeText(CrearTutorial.this, "Debe agregar un id de vídeo", Toast.LENGTH_SHORT).show();
            tutIdVideo.requestFocus();
        } else if (imageUri == null) {
            Toast.makeText(CrearTutorial.this, "Debe agregar una miniatura", Toast.LENGTH_SHORT).show();
            btnTutMiniatura.requestFocus();
        } else {
            UUID uuidObj = UUID.randomUUID();
            String uuid = uuidObj.toString();
            StorageReference fileReference = storageReference.child(uuid);

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            storageReference.child(uuid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String miniatura = uri.toString();
                                    Toast.makeText(CrearTutorial.this, "Guardado con éxito", Toast.LENGTH_LONG).show();
                                    Map<String, Object> tutorial = new HashMap<>();
                                    tutorial.put("miniatura", miniatura);
                                    tutorial.put("idCreador", idCreador);
                                    tutorial.put("id", uuid);
                                    tutorial.put("dificultad", dificultadSeleccionada);
                                    tutorial.put("idVideo", idYTVideo);
                                    tutorial.put("descripcion", descripcion);
                                    tutorial.put("nombre", nombre);
                                    String uploadId = uuid;
                                    bd.child(uploadId).setValue(tutorial);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CrearTutorial.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(imageView);
        }
    }
}