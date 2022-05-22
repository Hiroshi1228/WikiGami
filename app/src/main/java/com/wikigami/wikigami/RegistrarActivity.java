package com.wikigami.wikigami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrarActivity extends AppCompatActivity {
    private EditText txt_NC;
    private EditText txt_Mail;
    private EditText txt_Pswdr;
    private Button btn_Rgstr;
    private TextView txt_IS;

    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        txt_Mail = findViewById(R.id.txtMailR);
        txt_Pswdr = findViewById(R.id.txtPssR);
        txt_NC = findViewById(R.id.txtNC);
        txt_IS = findViewById(R.id.txtIS);
        btn_Rgstr = findViewById(R.id.btnRC);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btn_Rgstr.setOnClickListener(view -> {
            createUser();
        });

        txt_IS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });
    }

    public void openLoginActivity(){
        Intent intent = new Intent(RegistrarActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void createUser(){
        String name = txt_NC.getText().toString();
        String mail = txt_Mail.getText().toString();
        String psswrd = txt_Pswdr.getText().toString();

        if (TextUtils.isEmpty(name)){
            txt_NC.setError("Debe agregar el nombre");
            txt_NC.requestFocus();
        }else if (TextUtils.isEmpty(mail)){
            txt_Mail.setError("Debe agregar un correo");
            txt_Mail.requestFocus();
        }else if (TextUtils.isEmpty(psswrd)){
            txt_Pswdr.setError("Debe agregar su contraseña");
            txt_Pswdr.requestFocus();
        }else {
            mAuth.createUserWithEmailAndPassword(mail, psswrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("users").document(userID);

                        Map<String,Object> user=new HashMap<>();
                        user.put("nombre", name);
                        user.put("correo", mail);
                        user.put("contraseña", psswrd);

                        //inicialización de la instancia a la base de datos de firebase
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        //Creación de la base de datos
                        DatabaseReference reference = database.getReference("REGISTRO_DE_USUARIOS");
                        //El nombre de la base de datos "No relacional es REGISTRO_DE_USUARIOS"
                        reference.child(userID).setValue(user);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "onSuccess: Datos registrados"+userID);
                            }
                        });
                        Toast.makeText(RegistrarActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrarActivity.this, LoginActivity.class));
                    }else{
                        Toast.makeText(RegistrarActivity.this, "Usuario no registrado"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}