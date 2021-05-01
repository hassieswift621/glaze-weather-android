package ninja.hassie.android.apps.glaze.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.adapter.LocationAdapter;
import ninja.hassie.android.apps.glaze.store.Store;

/**
 * The manage location bottom sheet dialog fragment is a contextual menu shown
 * at the bottom of the screen when a location is long pressed.
 */
public class ManageLocationDialogFragment extends BottomSheetDialogFragment {

    // Bundle extras IDs.
    public static final String BUNDLE_EXTRAS_LOCATION_ID = "location_id";
    public static final String BUNDLE_EXTRAS_LOCATION_NAME = "location_name";

    // Log tag.
    private static final String TAG = "ManageLocation";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate view.
        View root = inflater.inflate(R.layout.modal_location_card, container, false);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get host fragment hosting this dialog, i.e. LocationsFragment.
        Fragment host = requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Fragment fragment = host.getChildFragmentManager().getFragments().get(0);

        // Set location name.
        TextView textView = view.findViewById(R.id.name);
        textView.setText(getArguments().getString(BUNDLE_EXTRAS_LOCATION_NAME));

        // Add delete click listener.
        MaterialButton delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(v -> deleteLocation(fragment.requireView()));

        // Add set as home location click listener.
        MaterialButton setHomeLocation = view.findViewById(R.id.setHomeLocation);
        setHomeLocation.setOnClickListener(v -> setHomeLocation(fragment.requireView()));
    }

    /**
     * Deletes the location.
     */
    private void deleteLocation(View view) {
        try {
            // Delete location.
            Store.deleteLocation(requireContext(), getArguments().getString(BUNDLE_EXTRAS_LOCATION_ID));

            // Get recycler view and update adapter.
            RecyclerView rv = view.findViewById(R.id.recycler_view);
            rv.setAdapter(new LocationAdapter(view.getContext(), Store.getLocations()));

            // Dismiss dialog.
            dismiss();

            // Show snackbar with success message.
            Snackbar.make(view, getString(R.string.locations_delete_success), Snackbar.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            // Log error.
            Log.e(TAG, e.toString());

            // Show snackbar with error message.
            Snackbar.make(view, getString(R.string.locations_delete_fail), Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Sets the location as the home location.
     */
    private void setHomeLocation(View view) {
        // Get location ID.
        String locationID = getArguments().getString(BUNDLE_EXTRAS_LOCATION_ID);

        // Get preferences.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        preferences.edit()
                .putString(requireContext().getString(R.string.preference_home_location_key), locationID)
                .apply();

        // Get location name and split.
        String[] locationName = getArguments().getString(BUNDLE_EXTRAS_LOCATION_NAME).split(", ");

        // Dismiss dialog.
        dismiss();

        // Show snackbar with success message.
        Snackbar.make(view,
                requireContext().getString(R.string.locations_set_as_home_location_success,
                        locationName[0] + ", " + locationName[locationName.length - 1]),
                Snackbar.LENGTH_LONG)
                .show();
    }

}
