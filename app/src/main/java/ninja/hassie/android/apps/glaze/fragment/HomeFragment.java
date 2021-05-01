package ninja.hassie.android.apps.glaze.fragment;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.awareness.Awareness;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.activity.HomeActivity;
import ninja.hassie.android.apps.glaze.api.GeocodeRetrofit;
import ninja.hassie.android.apps.glaze.api.GeocodeService;
import ninja.hassie.android.apps.glaze.manager.WeatherUIManager;
import ninja.hassie.android.apps.glaze.model.data.Location;
import ninja.hassie.android.apps.glaze.model.lociq.LocationResult;
import ninja.hassie.android.apps.glaze.store.Store;
import ninja.hassie.android.apps.glaze.utility.LocationUtil;
import retrofit2.Call;
import retrofit2.Response;

/**
 * The home fragment displays the weather for the user's set home location.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment"; // Log tag.

    private boolean useCurrentLocation; // Whether user has enabled to use current location.
    private Location location = null; // The home location.
    private WeatherUIManager uiManager; // The weather UI manager.

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate view.
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Get shared preferences.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Get the user's current location preference.
        useCurrentLocation = preferences.getBoolean(getString(R.string.preference_use_current_location_key), false);
        if (useCurrentLocation) {
            // Check if we have permissions.
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Get the last location stored, if exists.
                location = Store.getLocation("current");

                // Hide view.
                root.setVisibility(View.GONE);
            }
            // Else, check if we should show an educational dialog
            // explaining why location permission is required.
            else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionsRequiredContent(root);
                showLocationInfoDialog();
            }
            // Else request permissions.
            else {
                showPermissionsRequiredContent(root);
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            return root;
        }

        // Get the user's home location preference.
        String locationID = preferences.getString(getString(R.string.preference_home_location_key), null);
        if (locationID != null) {
            location = Store.getLocation(locationID);
        }

        // Check if the location exists/is set.
        if (location == null) {
            // Show home toolbar.
            //((HomeActivity) requireActivity()).showHomeToolbar();

            // Show layout view.
            root.setVisibility(View.VISIBLE);

            // Show no location set content.
            showNoLocationSetContent(root);

            return root;
        }

        // Hide view.
        root.setVisibility(View.GONE);

        // Show weather toolbar and content.
        ((HomeActivity) requireActivity()).showWeatherToolbar(LocationUtil.splitName(location)[0]);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add click listener for grant permissions button.
        // This button is visible when the permissions required UI is visible.
        MaterialButton grantPermissions = ((HomeActivity) requireActivity()).findViewById(R.id.grantPermissions);
        grantPermissions.setOnClickListener(v -> {
            // Check if we should show an educational dialog
            // explaining why location permission is required.
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionsRequiredContent(view);
                showLocationInfoDialog();
            }
            // Else request permissions.
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });

        // Get swipe refresh layout.
        SwipeRefreshLayout swipeRefreshLayout = ((HomeActivity) requireActivity()).findViewById(R.id.toolbar_weather);

        // Add offset changed listener for app bar layout.
        AppBarLayout appBarLayout = swipeRefreshLayout.findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            // Only allow swipe down refresh if the user has scrolled to the top.
            swipeRefreshLayout.setEnabled(verticalOffset == 0);
        });

        // Add refresh listener to swipe refresh layout.
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (useCurrentLocation) {
                fetchData();
            } else {
                uiManager.fetchWeather();
            }
        });

        // If current location should be used and there is a last location, display the data first
        // and then attempt to fetch the current location and weather.
        if (useCurrentLocation && location != null) {
            // Show weather toolbar and content.
            ((HomeActivity) requireActivity()).showWeatherToolbar(LocationUtil.splitName(location)[0]);

            // Initialise weather UI manager.
            uiManager = new WeatherUIManager(requireContext(), location,
                    requireActivity().findViewById(R.id.toolbar_weather));

            uiManager.displayData();

            // Fetch current location and weather.
            fetchData();

            return;
        }

        // If current location should be used and there is no last location available,
        // attempt to fetch the current location and weather.
        if (useCurrentLocation) {
            // Fetch current location and weather.
            fetchData();

            return;
        }

        // If there is no home location set, return as there's nothing to do.
        if (location == null) {
            return;
        }

        // Initialise weather UI manager.
        uiManager = new WeatherUIManager(requireContext(), location,
                requireActivity().findViewById(R.id.toolbar_weather));

        // Display the weather data.
        uiManager.displayData();

        // Attempt to fetch latest weather data.
        uiManager.fetchWeather();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if request code is 1.
        if (requestCode == 1) {
            // Check if the permission was granted.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Get the last location stored, if exists.
                location = Store.getLocation("current");

                // Fetch the current location and weather.
                fetchData();
            } else {
                // Show location denied dialog.
                showLocationDeniedDialog();
            }
        }
    }

    /**
     * Shows an educational dialog about location permissions.
     */
    private void showLocationInfoDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.permissions_location_title)
                .setMessage(R.string.permissions_location_content)
                .setPositiveButton(R.string.permissions_location_allow_button, (dialog, which) -> {
                    // Dismiss dialog.
                    dialog.dismiss();

                    // Request permissions.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                })
                .setNegativeButton(R.string.permissions_location_deny_button, (dialog, which) -> {
                    // Dismiss dialog.
                    dialog.dismiss();

                    // Show denied dialog.
                    showLocationDeniedDialog();
                })
                .show();
    }

    /**
     * Shows a education dialog about denying location permissions.
     */
    private void showLocationDeniedDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.permissions_location_title)
                .setMessage(R.string.permissions_location_denied_content)
                .setNegativeButton(android.R.string.ok, (dialog, which) -> {
                    // Dismiss dialog.
                    dialog.dismiss();
                })
                .show();
    }

    /**
     * Shows the no location set content.
     */
    private void showNoLocationSetContent(View view) {
        // Show the home toolbar.
        ((HomeActivity) requireActivity()).showHomeToolbar();

        // Hide the other content.
        view.findViewById(R.id.permissionsRequired).setVisibility(View.GONE);

        // Show the required content.
        view.findViewById(R.id.noLocationSet).setVisibility(View.VISIBLE);

        // Show view.
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the permissions required content.
     */
    private void showPermissionsRequiredContent(View view) {
        // Show the home toolbar.
        ((HomeActivity) requireActivity()).showHomeToolbar();

        // Hide the other content.
        view.findViewById(R.id.noLocationSet).setVisibility(View.GONE);

        // Show the required content.
        view.findViewById(R.id.permissionsRequired).setVisibility(View.VISIBLE);

        // Show view.
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Fetches the user's current location and the weather data for the location.
     */
    private void fetchData() {
        // Get the user's current location using Awareness API.
        Awareness.getSnapshotClient(requireContext()).getLocation()
                .addOnSuccessListener(locationResponse -> {
                    // Get location.
                    android.location.Location location = locationResponse.getLocation();

                    // Reverse geocode and get weather data for the location.
                    new Thread(() -> {
                        try {
                            // Get geocode service.
                            GeocodeService geocodeService = GeocodeRetrofit.getService();

                            // Reverse geocode.
                            Call<LocationResult> call = geocodeService.search(getString(R.string.api_key_location_iq),
                                    location.getLatitude(), location.getLongitude());
                            Response<LocationResult> response = call.execute();
                            if (!response.isSuccessful() || response.body() == null) {
                                return;
                            }

                            // Get result.
                            LocationResult result = response.body();

                            // Replace stored location.
                            Store.deleteLocation(requireContext(), "current");
                            Store.addLocation(requireContext(), new Location("current",
                                    result.getLat(), result.getLon(), result.getName()));

                            // Update UI.
                            requireActivity().runOnUiThread(() -> {
                                // Initialise weather UI manager.
                                uiManager = new WeatherUIManager(requireContext(), Store.getLocation("current"),
                                        requireActivity().findViewById(R.id.toolbar_weather));

                                // Show weather toolbar and content.
                                ((HomeActivity) requireActivity()).showWeatherToolbar(
                                        LocationUtil.splitName(Store.getLocation("current"))[0]);

                                // Fetch weather.
                                uiManager.fetchWeather();
                            });

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }).start();
                });
    }

}
