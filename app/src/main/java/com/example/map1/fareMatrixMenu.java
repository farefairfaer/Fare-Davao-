package com.example.map1;

import static com.example.map1.MainActivity.routeNames;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;
import java.util.List;

public class fareMatrixMenu extends AppCompatActivity {

    private ListView fareMatrixListView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fare_matrix_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbarMenu);
        fareMatrixListView = findViewById(R.id.fareMatrixListView);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("List of Fare Matrix");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                routeNames);
        fareMatrixListView.setAdapter(arrayAdapter);

                fareMatrixListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRoute = routeNames[position];

            Intent intent = new Intent(fareMatrixMenu.this, fareMatrix_view.class);
            intent.putExtra("routeName", selectedRoute);
            startActivity(intent);
        });
    }
}