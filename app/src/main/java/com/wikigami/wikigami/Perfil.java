package com.wikigami.wikigami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfil extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    //private CircleImageView profileImageView;
    //private Button save_info;
    TextView UserName, MailData /*change_photo*/;
    Button ActualizarInfo, ActualizarPass, btnCloseSesion;

    private DatabaseReference databaseReference;

    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicsRef;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    DatabaseReference BASE_DE_DATOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

       // profileImageView = findViewById(R.id.profile_image);

       // change_photo = findViewById(R.id.changePhoto);

        UserName = findViewById(R.id.UserName);
        MailData = findViewById(R.id.MailData);

       // save_info = findViewById(R.id.SaveInfo);
        ActualizarInfo = findViewById(R.id.ActualizarInfo);
        ActualizarPass = findViewById(R.id.ActualizarPass);
        btnCloseSesion = findViewById(R.id.btn_CloseSesion);

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        BASE_DE_DATOS = FirebaseDatabase.getInstance().getReference("REGISTRO_DE_USUARIOS");

        //storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");

       /* save_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfileImage();
            }
        });

        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */

        /*Obtencion de datos del usuario*/
        BASE_DE_DATOS.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Si el usuario existe
                if (snapshot.exists()){

                    /*Obtencion de los datos de firebase*/
                    /*Los datos se rescatan tal cual fueron registrados*/

                    String nombre = ""+snapshot.child("nombre").getValue();
                    String correo = ""+snapshot.child("correo").getValue();

                    /*Setear los datos en los textview e imageview*/
                    UserName.setText(nombre);
                    MailData.setText(correo);

                    /*Obtener Imagen*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.perfil);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.inicio:
                        startActivity(new Intent(getApplicationContext(),Inicio.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.favoritos:
                        startActivity(new Intent(getApplicationContext(),Favoritos.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.perfil:
                        return true;
                }

                return false;
            }
        });

        btnCloseSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(Perfil.this, LoginActivity.class));
                finish();
            }
        });
    }

    /*private void getUserinfo(){
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                    if (snapshot.hasChild("image")){
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    } */

   /* private void uploadProfileImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set your profile");
        progressDialog.setMessage("Espere un momento, se esta actualizando la información ");
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileRef = storageProfilePicsRef.child(firebaseAuth.getCurrentUser().getUid()+".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadURL = task.getResult();
                        myUri = downloadURL.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("image",myUri);
                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(userMap);

                        progressDialog.dismiss();
                    }
                }
            });
        }else{
            progressDialog.dismiss();
            Toast.makeText(this, "Imagen no seleccionada", Toast.LENGTH_SHORT).show();
        }
    } */

}