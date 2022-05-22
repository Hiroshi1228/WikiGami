package com.wikigami.wikigami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Favoritos extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.favoritos);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.inicio:
                        startActivity(new Intent(getApplicationContext(),Inicio.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.favoritos:
                        return true;

                    case R.id.perfil:
                        startActivity(new Intent(getApplicationContext(),Perfil.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });
    }
}