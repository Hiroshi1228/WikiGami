package com.wikigami.wikigami;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Inicio extends AppCompatActivity {
    GridView gridView;

    String[] names = {"image1","image2","image3","image4","image5","image6","image7","image8","image9","image10","image11","image12"};
    int[] images = {R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4,R.drawable.image5,R.drawable.image6,R.drawable.image7,R.drawable.image8,R.drawable.image9,R.drawable.image10,R.drawable.image11,R.drawable.image12 };

    BottomNavigationView bottomNavigationView;

    FloatingActionButton addOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        addOrg = findViewById(R.id.Add);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.inicio);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.inicio:
                        return true;

                    case R.id.favoritos:
                        startActivity(new Intent(getApplicationContext(),Favoritos.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.perfil:
                        startActivity(new Intent(getApplicationContext(),Perfil.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });

        gridView = findViewById(R.id.gridView);

        CustomAdapter customAdapter = new CustomAdapter(names,images,this);

        gridView.setAdapter(customAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedName = names[i];
                int selectedImage = images[i];

                startActivity(new Intent(Inicio.this,ClickedItemActivity.class).putExtra("name",selectedName).putExtra("image",selectedImage));



            }
        });
    }
    public class CustomAdapter extends BaseAdapter{
        private String[] imageNames;
        private int[] imagesPhoto;
        private Context context;
        private LayoutInflater layoutInflater;

        public CustomAdapter(String[] imageNames, int[] imagesPhoto, Context context) {
            this.imageNames = imageNames;
            this.imagesPhoto = imagesPhoto;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagesPhoto.length;
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

            if(view == null){
                view = layoutInflater.inflate(R.layout.row_items, viewGroup, false);

            }

            TextView tvName = view.findViewById(R.id.tvName);
            ImageView imageView = view.findViewById(R.id.imageView);


            tvName.setText(imageNames[i]);
            imageView.setImageResource(imagesPhoto[i]);



            return view;
        }
    }

}