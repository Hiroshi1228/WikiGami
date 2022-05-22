package com.wikigami.wikigami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    Button btn_ResCon;
    EditText txt_EmailR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btn_ResCon = findViewById(R.id.btnResCon);
        txt_EmailR = findViewById(R.id.txtEmailR);

        btn_ResCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }
    public void validate() {
        String email = txt_EmailR.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txt_EmailR.setError("Correo invalido");
            return;
        }

        sendEmail(email);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Correo enviado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(ForgotPassword.this, "Correo invalido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}