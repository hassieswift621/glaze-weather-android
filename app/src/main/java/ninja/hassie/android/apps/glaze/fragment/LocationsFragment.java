package ninja.hassie.android.apps.glaze.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.activity.AddLocationActivity;
import ninja.hassie.android.apps.glaze.activity.HomeActivity;
import ninja.hassie.android.apps.glaze.adapter.LocationAdapter;
import ninja.hassie.android.apps.glaze.decoration.RvItemDecoration;
import ninja.hassie.android.apps.glaze.model.data.Location;
import ninja.hassie.android.apps.glaze.store.Store;

/**
 * The locations fragment allows the user to manage locations.
 */
public class LocationsFragment extends Fragment {

    private RecyclerView recyclerView; // The recycler view.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Show home toolbar.
        ((HomeActivity) requireActivity()).showHomeToolbar();

        // Inflate view.
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add click listener to FAB.
        // Start add location activity on click.
        FloatingActionButton addLocation = view.findViewById(R.id.addLocation);
        addLocation.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddLocationActivity.class)));

        // Get recycler view.
        recyclerView = view.findViewById(R.id.recycler_view);

        // Create layout manager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        // Add item decoration.
        recyclerView.addItemDecoration(new RvItemDecoration());

        // Set adapter.
        recyclerView.setAdapter(new LocationAdapter(requireContext(), Store.getLocations()));
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the weather shown for the locations.
        recyclerView.getAdapter().notifyDataSetChanged();

        // Check if there is a new location to add to the recycler view, i.e. location added
        // using the add location activity.
        List<Location> locations = Store.getLocations();
        if (recyclerView.getAdapter().getItemCount() < locations.size()) {
            // Update recycler view.
            recyclerView.setAdapter(new LocationAdapter(requireContext(), locations));
        }
    }

}
