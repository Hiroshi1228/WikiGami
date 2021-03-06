package com.wikigami.wikigami.CambiarPassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wikigami.wikigami.LoginActivity;
import com.wikigami.wikigami.R;

import java.util.HashMap;

public class CambiarPassword extends AppCompatActivity {

    TextView MisCredencialesTXT,CorreoActualTXT,CorreoActual,PassActualTXT,PassActual;
    EditText ActualPassET, NuevoPassET;
    Button CambiarPassbtn;
    DatabaseReference REGISTRO_DE_USUARIOS;
    FirebaseAuth mAuth;
    FirebaseUser user;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        MisCredencialesTXT = findViewById(R.id.MisCredencialesTXT);
        CorreoActualTXT = findViewById(R.id.CorreoActualTXT);
        CorreoActual = findViewById(R.id.CorreoActual);
        ActualPassET = findViewById(R.id.ActualPassET);
        NuevoPassET = findViewById(R.id.NuevoPassET);
        CambiarPassbtn = findViewById(R.id.CambiarPassbtn);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        REGISTRO_DE_USUARIOS = FirebaseDatabase.getInstance().getReference("REGISTRO_DE_USUARIOS");

        progressDialog = new ProgressDialog(CambiarPassword.this);

        /*Consultaremos el correo y contrase??a del usuario*/
        Query query = REGISTRO_DE_USUARIOS.orderByChild("correo").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    //Obtenci??n de los valores
                    String correo = ""+ds.child("correo").getValue();

                    //Seteamos los datos en los texview
                    CorreoActual.setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Creamos el evento para cambiar contrase??a
        CambiarPassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String PASS_ANTERIOR = ActualPassET.getText().toString().trim();
                String NUEVO_PASS = NuevoPassET.getText().toString().trim();

                //Condiciones
                if(TextUtils.isEmpty(PASS_ANTERIOR)){
                    Toast.makeText(CambiarPassword.this, "El campo contrase??a actual est?? vac??o", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(NUEVO_PASS)){
                    Toast.makeText(CambiarPassword.this, "El campo nueva contrase??a est?? vacio", Toast.LENGTH_SHORT).show();
                }
                if (!NUEVO_PASS.equals("") && NUEVO_PASS.length()>6){
                    //SE EJECUTA EL METODO PARA ACTUALIZAR PASSWORD, EL CUAL RECIBE 2 PARAMETROS
                    Cambio_de_Password(PASS_ANTERIOR,NUEVO_PASS);
                }else{
                    NuevoPassET.setError("La contrase??a debe ser mayor a 6 caracteres");
                    NuevoPassET.setFocusable(true);
                }
            }
        });
    }

    //Metodo para cambiar la contrase??a
    private void Cambio_de_Password(String pass_anterior, String nuevo_pass) {
        progressDialog.show();
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor");
        user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),pass_anterior);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.updatePassword(nuevo_pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        String value = NuevoPassET.getText().toString().trim();
                        HashMap<String , Object> result = new HashMap<>();
                        result.put("contrase??a", value);
                        //SE ACTUALIZA LA NUEVA CONTRASE??A EN LA BD
                        REGISTRO_DE_USUARIOS.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CambiarPassword.this, "Contrase??a cambiada", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            }
                        });
                        //LUEGO SE CIERRA LA SESI??N
                        mAuth.signOut();
                        startActivity(new Intent(CambiarPassword.this, LoginActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CambiarPassword.this, "La contrase??a actual no es la correcta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}