package com.wikigami.wikigami;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ClickedItemActivity extends AppCompatActivity {

    ImageView imageView;
    TextView tvNombre;
    TextView tvDescripcion;
    TextView tvDificultad;
    private FirebaseUser user;
    Button btnOpenVideo;
    FloatingActionButton fabStar;
    FloatingActionButton fabReport;
    FloatingActionButton fabEdit;
    FloatingActionButton fabRemove;
    String miniatura;
    String id;

    ArrayList<String> favoritoDe;

    boolean esFavorito = false;

    DatabaseReference bdTutoriales = FirebaseDatabase.getInstance().getReference("TUTORIALES");
    DatabaseReference bdResportes = FirebaseDatabase.getInstance().getReference("REPORTES");
    Context contexto = this;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("TUTORIALES");
    private String nombre;
    private String descripcion;
    private String idVideo;
    private String dificultad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_item);

        user = FirebaseAuth.getInstance().getCurrentUser();

        imageView = findViewById(R.id.imageView);
        tvNombre = findViewById(R.id.tvNombre);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        btnOpenVideo = findViewById(R.id.btnOpenVideo);
        tvDificultad = findViewById(R.id.tvDificultad);
        fabStar = findViewById(R.id.fabStar);
        fabReport = findViewById(R.id.fabReport);
        fabEdit = findViewById(R.id.fabEditar);
        fabRemove = findViewById(R.id.fabEliminar);

        fabStar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (esFavorito) {
                    eliminarDeFavoritos();
                } else {
                    agreagrAFavoritos();
                }
            }
        });

        fabReport.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                reportarTutorial();
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                editarTutorial();
            }
        });

        fabRemove.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                eliminarTutorial();
            }
        });

        Intent intent = getIntent();

        if (intent.getExtras() == null) {
            Toast.makeText(this, "Algo salió mal", Toast.LENGTH_SHORT).show();
            return;
        }

        id = intent.getStringExtra("id");
        Context context = this;
        bdTutoriales.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nombre = "" + snapshot.child("nombre").getValue();
                    miniatura = "" + snapshot.child("miniatura").getValue();
                    descripcion = "" + snapshot.child("descripcion").getValue();
                    idVideo = "" + snapshot.child("idVideo").getValue();
                    dificultad = "" + ((snapshot.child("dificultad").getValue() != null) ? snapshot.child("dificultad").getValue() : "Básico");

                    favoritoDe = (ArrayList<String>) snapshot.child("favoritoDe").getValue();
                    if (favoritoDe == null) {
                        fabStar.setImageDrawable(getDrawable(R.drawable.ic_fav_outline));
                        esFavorito = false;
                    } else {
                        System.out.println(favoritoDe);
                        if (favoritoDe.contains(user.getUid())) {
                            esFavorito = true;
                            fabStar.setImageDrawable(getDrawable(R.drawable.ic_fav_filled));
                        }
                    }
//                    if(nombre.equals("prueba")) {
//                        bdTutoriales.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Toast.makeText(context, "Tutorial Eliminado", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        Toast.makeText(context, "Hola", Toast.LENGTH_SHORT).show();
//                    }

                    Picasso.get().load(miniatura).into(imageView);
                    tvNombre.setText(nombre);
                    tvDescripcion.setText(descripcion);
                    tvDificultad.setText(dificultad);


                    btnOpenVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + idVideo));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage("com.google.android.youtube");
                            startActivity(intent);
                        }
                    });


                } else {
                    System.out.println("No existe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void eliminarTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setTitle("Eliminar tutorial");
//        LayoutInflater inflater = this.getLayoutInflater();
//
//        builder.setView(inflater.inflate(R.layout.report_view, null));

        builder.setMessage("¿Seguro que desea eliminar el tutorial con id:" + id + "?");
        AlertDialog dialog = builder.create();

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialog.getButton(dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(contexto, "Tutorial eliminado", Toast.LENGTH_SHORT).show();
                bdTutoriales.child(id).removeValue();
                Intent intent = new Intent(contexto, Inicio.class);
                startActivity(intent);
            }
        });
    }

    private void editarTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setTitle("Editar tutorial");
        LayoutInflater inflater = this.getLayoutInflater();
        View vista = inflater.inflate(R.layout.editar_view, null);


        Spinner spinnerDificultad = vista.findViewById(R.id.spinnerDificultad);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(vista.getContext(), R.array.dificultades, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerDificultad.setAdapter(adapter);
        EditText etNombre = vista.findViewById(R.id.etNombre);
        EditText etDescripcion = vista.findViewById(R.id.etDescripcion);
        EditText etVideoId = vista.findViewById(R.id.etVideoId);

        etNombre.setText(nombre);
        etDescripcion.setText(descripcion);
        etVideoId.setText(idVideo);

        int selected = -1;
        if(dificultad.equals("Básico")) {
            selected = 0;
        } else if (dificultad.equals("Intermedio")) {
            selected = 1;
        } else if (dificultad.equals("Avanzado")){
            selected = 2;
        }
        spinnerDificultad.setSelection(selected);

        builder.setView(vista);



        AlertDialog dialog = builder.create();



        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialog.getButton(dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreActualizado = etNombre.getText().toString();
                String descripcionActualizada = etDescripcion.getText().toString();
                String idVideoActualizado = etVideoId.getText().toString();
                String dificultadActualizada = (String) spinnerDificultad.getSelectedItem();
                Map<String, Object> tutorial = new HashMap<>();
                tutorial.put("miniatura", miniatura);
                tutorial.put("idCreador", user.getUid());
                tutorial.put("id", id);
                tutorial.put("dificultad", dificultadActualizada);
                tutorial.put("idVideo", idVideoActualizado);
                tutorial.put("descripcion", descripcionActualizada);
                tutorial.put("nombre", nombreActualizado);
                bdTutoriales.child(id).setValue(tutorial);
                Toast.makeText(contexto, "Turorial Actualizado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void reportarTutorial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Reportar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setTitle("Reportar tutorial");
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.report_view, null));

        AlertDialog dialog = builder.create();

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialog.getButton(dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etRazon = dialog.findViewById(R.id.etRazon);
                String razon = etRazon.getText().toString();
                if (razon.equals("")) {
                    Toast.makeText(contexto, "No puede dejar la razón vacía", Toast.LENGTH_SHORT).show();
                    etRazon.setError("Rellene este campo");
                    return;
                }
                UUID uuidObj = UUID.randomUUID();
                String reporteId = uuidObj.toString();
                Map<String, Object> reporte = new HashMap<>();
                reporte.put("autor", user.getUid());
                reporte.put("tutorialReportado", id);
                reporte.put("razon", razon);
                bdResportes.child(reporteId).setValue(reporte);
                Toast.makeText(contexto, "Reporte enviado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void agreagrAFavoritos() {
        if (favoritoDe == null) {
            favoritoDe = new ArrayList<String>();
        }
        favoritoDe.add(user.getUid());

        bdTutoriales.child(id).child("favoritoDe").setValue(favoritoDe);
        fabStar.setImageDrawable(getDrawable(R.drawable.ic_fav_filled));
        esFavorito = true;
        Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void eliminarDeFavoritos() {
        favoritoDe.remove(user.getUid());
        bdTutoriales.child(id).child("favoritoDe").setValue(favoritoDe);
        fabStar.setImageDrawable(getDrawable(R.drawable.ic_fav_outline));
        esFavorito = false;
        Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
    }
}
