package ninja.hassie.android.apps.glaze.manager;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.adapter.ForecastAdapter;
import ninja.hassie.android.apps.glaze.api.WeatherRetrofit;
import ninja.hassie.android.apps.glaze.api.WeatherService;
import ninja.hassie.android.apps.glaze.model.data.Location;
import ninja.hassie.android.apps.glaze.model.data.Weather;
import ninja.hassie.android.apps.glaze.model.owm.current.CurrentWeather;
import ninja.hassie.android.apps.glaze.model.owm.forecast.Forecast;
import ninja.hassie.android.apps.glaze.store.Store;
import ninja.hassie.android.apps.glaze.utility.LocationUtil;
import ninja.hassie.android.apps.glaze.utility.WeatherUtil;
import retrofit2.Response;

/**
 * Manages the common functionality for the weather UI
 * used for the home fragment and the weather activity.
 * This prevents having to duplicate all of the logic found in this class.
 */
public class WeatherUIManager {

    private static final String TAG = "WeatherUIManager"; // Log tag.

    private final Context context; // The context.
    private final Location location; // The location.
    private final SwipeRefreshLayout view; // The swipe to refresh view.
    private final RecyclerView forecastRV; // The forecast recycler view.

    /**
     * Initialises a new instance of the weather UI manager.
     *
     * @param context  The context.
     * @param location The location.
     * @param view     The view which contains the UI components.
     */
    public WeatherUIManager(Context context, Location location, SwipeRefreshLayout view) {
        this.context = context;
        this.location = location;
        this.view = view;

        // Get the forecast recycler view and initialise once.
        forecastRV = view.findViewById(R.id.forecastRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        forecastRV.setLayoutManager(layoutManager);
    }

    /**
     * Displays the weather data.
     */
    public void displayData() {
        // Get components.
        ImageView conditionIcon = view.findViewById(R.id.conditionIcon);
        AppCompatTextView conditionName = view.findViewById(R.id.conditionName);
        AppCompatTextView temperature = view.findViewById(R.id.temperature);
        AppCompatTextView minTemperature = view.findViewById(R.id.minTemperature);
        AppCompatTextView maxTemperature = view.findViewById(R.id.maxTemperature);
        AppCompatTextView sunrise = view.findViewById(R.id.sunrise);
        AppCompatTextView sunset = view.findViewById(R.id.sunset);
        AppCompatTextView cloudiness = view.findViewById(R.id.cloudiness);
        AppCompatTextView rain = view.findViewById(R.id.rain);
        AppCompatTextView windSpeed = view.findViewById(R.id.windSpeed);
        AppCompatTextView windDirection = view.findViewById(R.id.windDirection);
        AppCompatTextView humidity = view.findViewById(R.id.humidity);
        AppCompatTextView pressure = view.findViewById(R.id.pressure);
        AppCompatTextView lastRefresh = view.findViewById(R.id.lastRefresh);

        AppBarLayout appBarLayout = view.findViewById(R.id.appBarLayout);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsingToolbarLayout);
        NestedScrollView secondaryContent = view.findViewById(R.id.secondaryContent);

        // Set title of collapsing toolbar to location name.
        collapsingToolbarLayout.setTitle((LocationUtil.splitName(location)[0]));

        // Get weather data.
        Weather weather = Store.getWeather(location.getId());

        // If there is no current weather data, return.
        if (weather == null || weather.getCurrent() == null) {
            return;
        }

        // Get current weather data.
        CurrentWeather currentWeather = weather.getCurrent();

        // Get colour for weather condition.
        int colour = WeatherUtil.getConditionColour(context, currentWeather.getWeather()[0],
                currentWeather.getSys().getSunrise(), currentWeather.getSys().getSunset(),
                currentWeather.getTimezone());

        // Set app bar layout background, collapsing toolbar scrim colour
        // and toolbar background colour.
        appBarLayout.setBackgroundColor(colour);
        collapsingToolbarLayout.setContentScrimColor(colour);
        toolbar.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        secondaryContent.setBackgroundColor(colour);

        // Set condition.
        conditionName.setText(currentWeather.getWeather()[0].getConditionGroup());
        conditionIcon.setImageDrawable(
                WeatherUtil.getConditionIcon(context, currentWeather.getWeather()[0],
                        currentWeather.getSys().getSunrise(), currentWeather.getSys().getSunset(),
                        currentWeather.getTimezone())
        );

        // Temperatures.
        temperature.setText(context.getString(R.string.temperature,
                WeatherUtil.convertTemperature(context,
                        currentWeather.getMain().getTempCurrent())));
        minTemperature.setText(context.getString(R.string.min_temperature,
                WeatherUtil.convertTemperature(context,
                        currentWeather.getMain().getTempMin())));
        maxTemperature.setText(context.getString(R.string.max_temperature,
                WeatherUtil.convertTemperature(context,
                        currentWeather.getMain().getTempMax())));

        // Sunrise and sunset times.
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        sunrise.setText(dateFormat.format(new Date(
                (currentWeather.getSys().getSunrise() + currentWeather.getTimezone()) * 1000)));
        sunset.setText(dateFormat.format(new Date(
                (currentWeather.getSys().getSunset() + currentWeather.getTimezone()) * 1000)));

        // Cloudiness.
        if (currentWeather.getClouds() != null) {
            cloudiness.setText(context.getString(R.string.weather_cloudiness, currentWeather.getClouds().getCloudiness()));
        }

        // Rain.
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        rain.setText(context.getString(R.string.weather_rain,
                decimalFormat.format(
                        currentWeather.getRain() != null ?
                                currentWeather.getRain().getRain1Hour() : 0
                )));

        // Wind.
        if (currentWeather.getWind() != null) {
            windDirection.setText(WeatherUtil.convertWindDirection(currentWeather.getWind().getDirection()));
            windSpeed.setText(WeatherUtil.convertWindSpeed(context, currentWeather.getWind().getSpeed()));
        }

        // Humidity.
        humidity.setText(context.getString(R.string.weather_humidity, currentWeather.getMain().getHumidity()));

        // Pressure.
        pressure.setText(context.getString(R.string.weather_pressure, currentWeather.getMain().getPressure()));

        // Last refresh.
        SimpleDateFormat refreshFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        lastRefresh.setText(context.getString(R.string.weather_last_refresh,
                refreshFormat.format(new Date(currentWeather.getTimestamp()))));

        // Forecast.
        if (weather.getForecast() != null) {
            forecastRV.setAdapter(new ForecastAdapter(context, weather));
        }

    }

    /**
     * Fetches weather for the location.
     */
    public void fetchWeather() {
        // Fetch weather data in background thread.
        new Thread(() -> {
            try {
                // Get weather service.
                WeatherService weatherService = WeatherRetrofit.getService();

                // Current weather.
                Response<CurrentWeather> current =
                        weatherService.currentWeather(context.getString(R.string.api_key_owm),
                                location.getLat(), location.getLon()).execute();
                if (current.isSuccessful() && current.body() != null) {
                    Store.addCurrentWeather(context, current.body(), location.getId());
                }

                // Forecast.
                Response<Forecast> forecast =
                        weatherService.forecast(context.getString(R.string.api_key_owm),
                                location.getLat(), location.getLon()).execute();
                if (forecast.isSuccessful() && forecast.body() != null) {
                    Store.addForecastWeather(context, forecast.body(), location.getId());
                }

                // Update UI.
                ((FragmentActivity) context).runOnUiThread(() -> {
                    // Set refreshing to false.
                    view.setRefreshing(false);

                    // Display data.
                    displayData();
                });
            } catch (Exception e) {
                // Set refreshing to false.
                view.setRefreshing(false);

                // Log.
                Log.e(TAG, e.toString());
            }
        }).start();
    }

}
