package ninja.hassie.android.apps.glaze.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.model.data.Weather;
import ninja.hassie.android.apps.glaze.model.owm.current.CurrentWeather;
import ninja.hassie.android.apps.glaze.model.owm.forecast.ForecastData;
import ninja.hassie.android.apps.glaze.utility.WeatherUtil;

/**
 * Recycler view adapter for weather forecast.
 * <p>
 * Reference: Google LLC (no date) Create a List with RecyclerView.
 * Available at: https://developer.android.com/guide/topics/ui/layout/recyclerview
 * (Accessed: 09 May 2020).
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private final Context context; // The context.
    private final Weather weather; // The weather data.

    /**
     * Initialises a new instance of the forecast adapter.
     *
     * @param context The context.
     * @param weather The weather data.
     */
    public ForecastAdapter(@NonNull Context context, @NonNull Weather weather) {
        this.context = context;
        this.weather = weather;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create view.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get forecast data.
        ForecastData forecast = weather.getForecast().getList()[position];

        // Get current weather data.
        CurrentWeather current = weather.getCurrent();

        // Get components.
        AppCompatTextView date = holder.itemView.findViewById(R.id.date);
        ImageView conditionIcon = holder.itemView.findViewById(R.id.conditionIcon);
        AppCompatTextView temperature = holder.itemView.findViewById(R.id.temperature);

        // Set date.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM\nHH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        date.setText(dateFormat.format(
                new Date((forecast.getTimestamp() + current.getTimezone()) * 1000)));

        // Set condition icon.
        conditionIcon.setImageDrawable(WeatherUtil.getConditionIcon(context, forecast.getWeather()[0],
                current.getSys().getSunrise(), current.getSys().getSunset(),
                current.getTimezone()));

        // Set temperature.
        temperature.setText(context.getString(R.string.temperature,
                WeatherUtil.convertTemperature(context, forecast.getMain().getTempCurrent())));
    }

    @Override
    public int getItemCount() {
        return weather.getForecast().getList().length;
    }

    /**
     * The view holder for a recycler view item.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Initialises a new instance of the view holder.
         *
         * @param view The view.
         */
        public ViewHolder(@NonNull View view) {
            super(view);
        }
    }

}
