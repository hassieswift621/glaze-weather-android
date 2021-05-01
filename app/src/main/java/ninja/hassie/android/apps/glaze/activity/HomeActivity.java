package ninja.hassie.android.apps.glaze.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.store.Store;

/**
 * The home activity is the main activity which encompasses
 * the home fragment, locations fragment and settings fragment.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity"; // Log tag.

    private AppBarConfiguration mAppBarConfiguration; // App bar config.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set view.
        setContentView(R.layout.activity_home);

        // Set toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get drawer.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Create app bar config, setting the fragments as top level locations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_locations, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();

        // Set nav controller.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Load app data from files.
        try {
            Store.loadStore(this);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Failed to load app data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Back navigation.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Shows the default home toolbar.
     */
    public void showHomeToolbar() {
        // Hide weather toolbar layout.
        SwipeRefreshLayout weather = findViewById(R.id.toolbar_weather);
        weather.setVisibility(View.GONE);

        // Set home toolbar layout.
        CoordinatorLayout home = findViewById(R.id.toolbar_home);
        home.setVisibility(View.VISIBLE);

        // Set home toolbar.
        MaterialToolbar toolbar = home.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Shows the weather collapsing toolbar, which includes the weather content.
     *
     * @param title The title of the toolbar.
     */
    public void showWeatherToolbar(String title) {
        // Hide home toolbar layout.
        CoordinatorLayout home = findViewById(R.id.toolbar_home);
        home.setVisibility(View.GONE);

        // Show weather toolbar layout.
        SwipeRefreshLayout weather = findViewById(R.id.toolbar_weather);
        weather.setVisibility(View.VISIBLE);

        // Set weather toolbar.
        MaterialToolbar toolbar = weather.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        // Set collapsing toolbar title.
        CollapsingToolbarLayout collapsingToolbarLayout = weather.findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(title);
    }

}
