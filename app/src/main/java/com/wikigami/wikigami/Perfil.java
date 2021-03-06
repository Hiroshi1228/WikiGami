package com.wikigami.wikigami;

import static android.content.ContentValues.TAG;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.wikigami.wikigami.CambiarPassword.CambiarPassword;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfil extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    TextView UserName, MailData;
    Button deleteCuenta, ActualizarPass, btnCloseSesion;
    CircleImageView profileImg;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    DatabaseReference BASE_DE_DATOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        UserName = findViewById(R.id.UserName);
        MailData = findViewById(R.id.MailData);

        deleteCuenta = findViewById(R.id.EliminarCuenta);
        ActualizarPass = findViewById(R.id.ActualizarPass);
        btnCloseSesion = findViewById(R.id.btn_CloseSesion);
        profileImg = findViewById(R.id.profile_image);

        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        BASE_DE_DATOS = FirebaseDatabase.getInstance().getReference("REGISTRO_DE_USUARIOS");

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
                    String img = ""+snapshot.child("imagen").getValue();

                    /*Setear los datos en los textview e imageview*/
                    UserName.setText(nombre);
                    MailData.setText(correo);

                    // Decoding image
                    byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                    Bitmap decodedImg = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    /*Obtener Imagen*/
                        profileImg.setImageBitmap(decodedImg);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ActualizarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mandar a la ventana correspondiente para cambiar la contrase??a
                startActivity(new Intent(Perfil.this, CambiarPassword.class));
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

        deleteCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = database.getReference();
                                    user.getUid();
                                    ref.child("REGISTRO_DE_USUARIOS").child(user.getUid()).removeValue();
                                    Log.d(TAG, "Cuenta de usuario eliminada.");
                                    Toast.makeText(Perfil.this, "Cuenta Eliminada", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Perfil.this, LoginActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });

    }
}