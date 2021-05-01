package ninja.hassie.android.apps.glaze.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.activity.WeatherActivity;
import ninja.hassie.android.apps.glaze.fragment.ManageLocationDialogFragment;
import ninja.hassie.android.apps.glaze.model.data.Location;
import ninja.hassie.android.apps.glaze.model.data.Weather;
import ninja.hassie.android.apps.glaze.model.owm.current.CurrentWeather;
import ninja.hassie.android.apps.glaze.store.Store;
import ninja.hassie.android.apps.glaze.utility.WeatherUtil;

/**
 * Recycler view adapter for locations.
 * <p>
 * Reference: Google LLC (no date) Create a List with RecyclerView.
 * Available at: https://developer.android.com/guide/topics/ui/layout/recyclerview
 * (Accessed: 09 May 2020).
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private final Context context; // The context.
    private final List<Location> dataset; // The dataset.

    /**
     * Initialises a new instance of the location adapter.
     *
     * @param context The context.
     * @param dataset The dataset.
     */
    public LocationAdapter(@NonNull Context context, @NonNull List<Location> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    /**
     * The view holder for a recycler view item.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        /**
         * Initialises a new instance of the view holder.
         *
         * @param view The view.
         */
        ViewHolder(@NonNull MaterialCardView view) {
            super(view);

            // Set click listeners.
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Initialise intent to start weather activity.
            Intent intent = new Intent(context, WeatherActivity.class);

            // Pass location ID as bundle extras.
            intent.putExtra(WeatherActivity.BUNDLE_EXTRAS_LOCATION_ID,
                    dataset.get(getAdapterPosition()).getId());

            // Start activity.
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            // Get location result of click position.
            Location location = dataset.get(getAdapterPosition());

            // Initialise bottom sheet dialog fragment to show contextual options.
            ManageLocationDialogFragment dialogFragment = new ManageLocationDialogFragment();

            // Pass location ID and location name as bundle extras.
            Bundle bundle = new Bundle();
            bundle.putString(ManageLocationDialogFragment.BUNDLE_EXTRAS_LOCATION_ID, location.getId());
            bundle.putString(ManageLocationDialogFragment.BUNDLE_EXTRAS_LOCATION_NAME, location.getName());
            dialogFragment.setArguments(bundle);

            // Show fragment.
            dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), dialogFragment.getTag());

            return true;
        }
    }

    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create card view.
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location_card, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get location.
        Location location = dataset.get(position);

        // Get components.
        TextView name = holder.itemView.findViewById(R.id.name);
        TextView temperature = holder.itemView.findViewById(R.id.temperature);
        ImageView icon = holder.itemView.findViewById(R.id.icon);
        TextView condition = holder.itemView.findViewById(R.id.condition);

        // Set location name.
        // Only show the first part of the location.
        name.setText(location.getName().split(", ")[0]);

        // Get weather data for location.
        Weather weather = Store.getWeather(location.getId());

        // If there is no weather data, return.
        if (weather == null || weather.getCurrent() == null) {
            return;
        }

        // Get current weather.
        CurrentWeather currentWeather = weather.getCurrent();

        // Set temperature.
        temperature.setText(context.getString(R.string.temperature,
                WeatherUtil.convertTemperature(context, weather.getCurrent().getMain().getTempCurrent())));

        // Set condition.
        condition.setText(weather.getCurrent().getWeather()[0].getConditionGroup());

        // Set condition icon.
        icon.setImageDrawable(WeatherUtil.getConditionIcon(context, currentWeather.getWeather()[0],
                currentWeather.getSys().getSunrise(), currentWeather.getSys().getSunset(),
                currentWeather.getTimezone()));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
