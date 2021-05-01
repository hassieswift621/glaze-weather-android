package ninja.hassie.android.apps.glaze.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.manager.WeatherUIManager;
import ninja.hassie.android.apps.glaze.model.data.Location;
import ninja.hassie.android.apps.glaze.store.Store;

/**
 * The weather activity shows the weather for a selected location
 * from the locations fragment.
 */
public class WeatherActivity extends AppCompatActivity {

    // Bundle extras IDs.
    public static final String BUNDLE_EXTRAS_LOCATION_ID = "location_id";

    private Location location; // The location.
    private WeatherUIManager uiManager; // The weather UI manager.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view.
        setContentView(R.layout.activity_weather);

        // Set toolbar.
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);

        // Add click listener for nav icon in toolbar.
        toolbar.setNavigationOnClickListener(v -> {
            // Finish activity.
            finish();
        });

        // Add offset changed listener for app bar layout.
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            // Only allow swipe down refresh if the user has scrolled to the top.
            findViewById(R.id.swipeRefreshLayout).setEnabled(verticalOffset == 0);
        });

        // Add refresh listener to swipe refresh layout.
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Fetch weather.
            uiManager.fetchWeather();
        });

        // Store location.
        location = Store.getLocation(getIntent().getStringExtra(BUNDLE_EXTRAS_LOCATION_ID));

        // Initialise weather UI manager.
        uiManager = new WeatherUIManager(this, location,
                getWindow().getDecorView().getRootView().findViewById(R.id.swipeRefreshLayout));

        // Display data.
        uiManager.displayData();

        // Fetch weather.
        swipeRefreshLayout.setRefreshing(true);
        uiManager.fetchWeather();
    }

}
