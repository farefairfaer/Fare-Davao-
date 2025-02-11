package com.example.map1;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import com.ortiz.touchview.TouchImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class fareMatrix_view extends AppCompatActivity {
    String[] fileNames = {"fare matrix/Matina Crossing.jpg", "fare matrix/Mintal.jpg",
            "fare matrix/Catalunan Grande.jpg", "fare matrix/Maa.jpg", "fare matrix/Toril.jpg",
            "fare matrix/Sasa via JP Laurel.jpg", "fare matrix/Calinan.jpg"};

    TouchImageView fareMatrixView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fare_matrix_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbarMenuView);
        fareMatrixView = findViewById(R.id.fareMatrixView);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        String selectedRoute = intent.getStringExtra("routeName");
        String selectedRouteFile = "fare matrix/" + selectedRoute + ".jpg";

        for(int i = 0; i < fileNames.length; i++) {
            if(Objects.equals(selectedRouteFile, fileNames[i])) {
                fareMatrixView.setImageBitmap(loadBitmapFromAssets(this, fileNames[i]));
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle(selectedRoute);

    }

    public Bitmap loadBitmapFromAssets(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}