package ninja.hassie.android.apps.glaze.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.UUID;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.api.WeatherRetrofit;
import ninja.hassie.android.apps.glaze.api.WeatherService;
import ninja.hassie.android.apps.glaze.model.data.Location;
import ninja.hassie.android.apps.glaze.model.data.Weather;
import ninja.hassie.android.apps.glaze.model.lociq.LocationResult;
import ninja.hassie.android.apps.glaze.model.owm.current.CurrentWeather;
import ninja.hassie.android.apps.glaze.model.owm.forecast.Forecast;
import ninja.hassie.android.apps.glaze.store.Store;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Recycler view adapter for location search results.
 * <p>
 * Reference: Google LLC (no date) Create a List with RecyclerView.
 * Available at: https://developer.android.com/guide/topics/ui/layout/recyclerview
 * (Accessed: 09 May 2020).
 */
public class LocationSearchAdapter extends RecyclerView.Adapter<LocationSearchAdapter.ViewHolder> {

    private static final String TAG = "LocationSearchAdapter"; // Log tag.

    private final Context context; // The context.
    private final List<LocationResult> dataset; // The dataset.
    private final ProgressBar progressBar; // The progress bar.

    private boolean resultClicked; // Whether a result has been clicked.

    /**
     * Initialises a new instance of the location search adapter.
     *
     * @param context     The context.
     * @param dataset     The dataset.
     * @param progressBar The progress bar.
     */
    public LocationSearchAdapter(@NonNull Context context, @NonNull List<LocationResult> dataset,
                                 @NonNull ProgressBar progressBar) {
        this.context = context;
        this.dataset = dataset;
        this.progressBar = progressBar;
    }

    /**
     * The view holder for a recycler view item.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Initialises a new instance of the view holder.
         *
         * @param view The view.
         */
        ViewHolder(MaterialCardView view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // If a result has not been clicked, add the location to the app.
            if (!resultClicked) {
                resultClicked = true;

                // Show progress bar.
                progressBar.setVisibility(View.VISIBLE);

                // Generate unique ID for location.
                String id = UUID.randomUUID().toString();

                // Get location result of click position.
                LocationResult result = dataset.get(getAdapterPosition());

                // Create location object.
                Location location = new Location(id, result.getLat(), result.getLon(), result.getName());

                // Add location, get weather and finish activity.
                try {
                    // Add location.
                    Store.addLocation(context, location);

                    // Get weather data for the location in a new thread.
                    // Using a thread here instead of Retrofit's async execution because two API
                    // calls are required.
                    new Thread(() -> {
                        // Vars to store fetched data.
                        CurrentWeather currentWeather = null;
                        Forecast forecast = null;

                        try {
                            // Get weather service.
                            WeatherService service = WeatherRetrofit.getService();

                            // Get API key.
                            String key = context.getString(R.string.api_key_owm);

                            // Get current weather data.
                            Call<CurrentWeather> currentWeatherCall = service.currentWeather(key, location.getLat(), location.getLon());
                            Response<CurrentWeather> currentWeatherResponse = currentWeatherCall.execute();
                            System.out.println(currentWeatherResponse.message());
                            if (currentWeatherResponse.isSuccessful()) {
                                currentWeather = currentWeatherResponse.body();
                            }

                            // Get forecast weather data.
                            Call<Forecast> forecastCall = service.forecast(key, location.getLat(), location.getLon());
                            Response<Forecast> forecastResponse = forecastCall.execute();
                            if (forecastResponse.isSuccessful()) {
                                forecast = forecastResponse.body();
                            }

                            // Store weather data for location.
                            Store.addWeather(context, new Weather(currentWeather, forecast, location.getId()));
                        } catch (Exception e) {
                            // Log error.
                            Log.e(TAG, e.toString());
                        } finally {
                            // Finish activity.
                            ((Activity) context).finish();
                        }
                    }).start();
                } catch (Exception e) {
                    // Log error.
                    Log.e(TAG, e.toString());

                    // Show toast with error message.
                    Toast.makeText(context, "An error occurred while adding location", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create card view.
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location_result, parent, false);

        return new LocationSearchAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set text.
        MaterialTextView textView = holder.itemView.findViewById(R.id.name);
        textView.setText(dataset.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
