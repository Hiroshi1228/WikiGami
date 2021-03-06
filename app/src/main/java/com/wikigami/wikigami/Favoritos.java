package com.wikigami.wikigami;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Favoritos extends AppCompatActivity {
    GridView gridView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference bd;
    private StorageReference storageReference;

    ArrayList<Tutorial> tutoriales = new ArrayList<Tutorial>();
    ArrayList<Tutorial> tutorialesFiltrados = new ArrayList<Tutorial>();

    BottomNavigationView bottomNavigationView;

    FloatingActionButton addOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritos_activity);

        setSupportActionBar(findViewById(R.id.toolbar));


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        bd = FirebaseDatabase.getInstance().getReference("TUTORIALES");
        storageReference = FirebaseStorage.getInstance().getReference("TUTORIALES");

        addOrg = findViewById(R.id.Add);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.favoritos);

        addOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Favoritos.this, CrearTutorial.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.inicio:
                        startActivity(new Intent(getApplicationContext(), Inicio.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.favoritos:
                        return true;

                    case R.id.perfil:
                        startActivity(new Intent(getApplicationContext(), Perfil.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });

        gridView = findViewById(R.id.gridView);

        Favoritos.CustomAdapter customAdapter = new Favoritos.CustomAdapter(tutorialesFiltrados, this);

        gridView.setAdapter(customAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String idTutorial = tutorialesFiltrados.get(i).getId();

                startActivity(new Intent(Favoritos.this, ClickedItemActivity.class).putExtra("id", idTutorial));


            }
        });
        DatabaseReference bdTutoriales = FirebaseDatabase.getInstance().getReference("TUTORIALES");
        Context contexto = this;
        bdTutoriales.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot ds = task.getResult();
//                System.out.println(ds.toString());
                Iterable<DataSnapshot> hijos = ds.getChildren();
                hijos.forEach(hijo -> {
                    ArrayList<String> favoritoDe = (ArrayList<String>) hijo.child("favoritoDe").getValue();
                    if(favoritoDe != null && favoritoDe.contains(user.getUid())) {
                        tutoriales.add(new Tutorial(hijo.getKey(),
                                hijo.child("nombre").getValue().toString(),
                                hijo.child("descripcion").getValue().toString(),
                                hijo.child("idVideo").getValue().toString(),
                                hijo.child("idCreador").getValue().toString(),
                                hijo.child("miniatura").getValue().toString(),
                                (hijo.child("dificultad").getValue() != null) ? hijo.child("dificultad").getValue().toString() : "B??sico"));
                    }
                });
                tutorialesFiltrados = tutoriales;
                Favoritos.CustomAdapter customAdapter = new Favoritos.CustomAdapter(tutorialesFiltrados, contexto);

                gridView.setAdapter(customAdapter);
                for (Tutorial tutorial : tutoriales) {
                    System.out.println(tutorial);
                }
            }
        });
    }

    public class CustomAdapter extends BaseAdapter {
        private ArrayList<Tutorial> tutoriales;
        private int[] imagesPhoto;
        private Context context;
        private LayoutInflater layoutInflater;

        public CustomAdapter(ArrayList<Tutorial> tutoriales, Context context) {
            this.tutoriales = tutoriales;
            this.imagesPhoto = imagesPhoto;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return tutoriales.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = layoutInflater.inflate(R.layout.row_items, viewGroup, false);

            }

            TextView tvName = view.findViewById(R.id.tvName);
            ImageView imageView = view.findViewById(R.id.imageView);


            tvName.setText(tutoriales.get(i).getNombre());
            Picasso.get().load(tutoriales.get(i).getMiniatura()).into(imageView);

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opciones, menu);

        // Associate searchable configuration with the SearchView
        SearchManager seerchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(seerchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void search(String query) {
        ArrayList<Tutorial> temp = (ArrayList<Tutorial>) tutoriales.clone();
        temp.removeIf(tutorial -> !(tutorial.getNombre().toLowerCase().contains(query.toLowerCase())));

        Favoritos.CustomAdapter customAdapter = new Favoritos.CustomAdapter(temp, this);
        tutorialesFiltrados = (ArrayList<Tutorial>) temp.clone();
        gridView.setAdapter(customAdapter);
    }

}