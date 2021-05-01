package ninja.hassie.android.apps.glaze.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.activity.HomeActivity;
import ninja.hassie.android.apps.glaze.store.Store;

/**
 * The settings fragment is the UI fragment for the settings screen.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    // Log tag.
    private static final String TAG = "SettingsFragment";

    // Location switch.
    private SwitchPreferenceCompat locationSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Show home toolbar.
        ((HomeActivity) requireActivity()).showHomeToolbar();

        // Inflate view.
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationSwitch = findPreference(getString(R.string.preference_use_current_location_key));
        locationSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            // If the new value is false, delete location and weather data for location.
            if (!((boolean) newValue)) {
                try {
                    Store.deleteLocation(requireContext(), "current");
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                // Update preference value.
                return true;
            }

            // New value was true.
            // Check if we have location permission.
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Update preference value.
                return true;
            }
            // Else, check if we should show an educational dialog
            // explaining why location permission is required.
            else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showLocationInfoDialog();
            }
            // Else request permissions.
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

            return false;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Check if request code is 1.
        if (requestCode == 1) {
            // Check if the permission was granted.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Check preference.
                locationSwitch.setChecked(true);
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

                    // Uncheck preference.
                    locationSwitch.setChecked(false);

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

}
