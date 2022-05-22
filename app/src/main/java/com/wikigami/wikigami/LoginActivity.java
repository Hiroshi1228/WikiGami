package com.wikigami.wikigami;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText txt_Email;
    private EditText txt_Pass;
    private Button btn_Log;
    private TextView txt_Regis;
    private TextView rest_Pass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_Email = findViewById(R.id.txtEmail);
        txt_Pass = findViewById(R.id.txtPass);
        btn_Log = findViewById(R.id.btnLog);
        txt_Regis = findViewById(R.id.txtRegis);
        rest_Pass = findViewById(R.id.restPass);

        mAuth = FirebaseAuth.getInstance();

        btn_Log.setOnClickListener(view -> {
            userLogin();
        });

        txt_Regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegistrarActivity();
            }
        });

        rest_Pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void openRegistrarActivity(){
        Intent intent = new Intent(LoginActivity.this, RegistrarActivity.class);
        startActivity(intent);
    }

    public void userLogin(){
        String mail = txt_Email.getText().toString();
        String password = txt_Pass.getText().toString();

        if (TextUtils.isEmpty(mail)){
            txt_Email.setError("Por favor, ingresa un correo válido");
            txt_Email.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Faltó la contraseña", Toast.LENGTH_SHORT).show();
            txt_Email.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Correcto logeo", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, Inicio.class));
                    }else{
                        Log.w("TAG", "Error:", task.getException());
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(LoginActivity.this, Inicio.class));
        }
    }
}