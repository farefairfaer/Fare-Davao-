package com.example.map1;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class searchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private searchAdapter adapter;
    private List<resultData> searchResults;
    private String buttonType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        buttonType = getIntent().getStringExtra("buttonType");
        recyclerView = findViewById(R.id.idRVCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResults = new ArrayList<>();

        recyclerView.setAdapter(adapter);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new searchAdapter(this, searchResults, data -> {
            Log.d("SearchActivity", "Item clicked: " + data.getDisplay_name());
            Intent resultIntent = new Intent("searchResult");
            resultIntent.putExtra("lat", data.getLat());
            resultIntent.putExtra("lon", data.getLon());
            resultIntent.putExtra("display_name", data.getDisplay_name());
            resultIntent.putExtra("buttonType", buttonType);
            setResult(RESULT_OK, resultIntent);
            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
            finish();
        });
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();

        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchSearchResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
        return true;
    }

    private void fetchSearchResults(String query) {
        APIRequest service = APIClient.getService();
        Call<List<resultData>> call = service.geocode(
                query + " Davao City",
                "json",
                1,
                "125.2175921, 6.9562097, 125.6971945, 7.5864748",
                1
        );
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<resultData>> call, @NonNull Response<List<resultData>> response) {
                List<resultData> filteredResults = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null) {
                    for (resultData result : response.body()) {
                        if (result.getDisplay_name() != null && result.getDisplay_name().contains("Davao City")) {
                            filteredResults.add(result);
                        }
                    }
                    searchResults = filteredResults;
                    adapter.updateList(searchResults);
                } else {
                    Toast.makeText(searchActivity.this, "No results found", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<resultData>> call, @NonNull Throwable t) {
                Toast.makeText(searchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}



