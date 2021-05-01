package ninja.hassie.android.apps.glaze.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Objects;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.adapter.LocationSearchAdapter;
import ninja.hassie.android.apps.glaze.api.GeocodeService;
import ninja.hassie.android.apps.glaze.fragment.LocationsFragment;
import ninja.hassie.android.apps.glaze.model.data.Location;
import ninja.hassie.android.apps.glaze.model.lociq.LocationResult;
import ninja.hassie.android.apps.glaze.decoration.RvItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The add location activity allows the user to add a location to the app.
 */
public class AddLocationActivity extends AppCompatActivity {

    private Retrofit retrofit; // Retrofit instance.
    private GeocodeService geocodeService; // Geocode service.
    private Call<List<LocationResult>> call; // Current call being made to API service.
    private ProgressBar progressBar; // Progress bar.
    private boolean resultSelected; // Whether a search result has been selected.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view.
        setContentView(R.layout.activity_add_location);

        // Set toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show back button on toolbar.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialise Retrofit.
        retrofit = new Retrofit.Builder()
                .baseUrl("https://eu1.locationiq.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        geocodeService = retrofit.create(GeocodeService.class);

        // Get progress bar.
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Get search view.
        SearchView searchView = findViewById(R.id.search_view);

        // Keep search box expanded.
        searchView.setIconifiedByDefault(false);

        // Focus the search box.
        searchView.requestFocus();

        // Set on text change listener.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Cancel current API call.
                if (call != null) {
                    call.cancel();
                }

                // Show progress bar.
                progressBar.setVisibility(View.VISIBLE);

                // Make API call.
                call = geocodeService.search(getString(R.string.api_key_location_iq), query);
                call.enqueue(new Callback<List<LocationResult>>() {
                    @Override
                    public void onResponse(Call<List<LocationResult>> call, Response<List<LocationResult>> response) {
                        // Hide progress bar.
                        progressBar.setVisibility(View.INVISIBLE);

                        // If response was successful, display results.
                        if (!response.isSuccessful()) {
                            return;
                        }
                        if (response.body() == null) {
                            return;
                        }

                        // Populate recycler view with found search results.
                        populateRV(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<LocationResult>> call, Throwable t) {
                        // Hide progress bar.
                        progressBar.setVisibility(View.INVISIBLE);

                        // Cancel call.
                        call.cancel();
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // If the back button is pressed in the toolbar, cleanup and finish activity.
        if (item.getItemId() == android.R.id.home) {
            // Cancel pending API call.
            if (call != null) {
                call.cancel();
            }

            // Finish activity.
            finish();

            return true;
        }

        return false;
    }


    /**
     * Populates the recycler view with the found results.
     *
     * @param results The search results.
     */
    private void populateRV(List<LocationResult> results) {
        // Get recycler view.
        RecyclerView rv = findViewById(R.id.recycler_view);

        // Create layout manager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddLocationActivity.this);
        rv.setLayoutManager(layoutManager);

        // Add item decoration.
        rv.addItemDecoration(new RvItemDecoration());

        // Set adapter.
        rv.setAdapter(new LocationSearchAdapter(this, results, progressBar));
    }
}
