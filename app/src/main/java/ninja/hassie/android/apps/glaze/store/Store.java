package ninja.hassie.android.apps.glaze.store;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ninja.hassie.android.apps.glaze.model.data.Location;
import ninja.hassie.android.apps.glaze.model.data.Weather;
import ninja.hassie.android.apps.glaze.model.owm.current.CurrentWeather;
import ninja.hassie.android.apps.glaze.model.owm.forecast.Forecast;

/**
 * Contains utility functions to manage the app data stored in data files.
 */
public class Store {

    // The GSON instance.
    private static final Gson GSON = new Gson();
    // The locations data filename.
    private static final String LOCATIONS_FILENAME = "locations.glaze";
    // The weather data filename.
    private static final String WEATHER_FILENAME = "weather.glaze";

    // The locations store.
    private static Map<String, Location> locationsStore = new HashMap<>();
    // The weather store.
    private static Map<String, Weather> weatherStore = new HashMap<>();

    /**
     * Loads the app data from the data files.
     *
     * @param context The context.
     * @throws Exception If an error occurred.
     */
    public static void loadStore(@NonNull Context context) throws Exception {
        // Load locations.
        loadLocations(context);

        // Load weather.
        loadWeather(context);
    }

    /**
     * Adds a location to the app data store.
     *
     * @param context  The context.
     * @param location The location to add.
     * @throws Exception If an error occurred.
     */
    public static void addLocation(@NonNull Context context, @NonNull Location location) throws Exception {
        // Add location to map.
        locationsStore.put(location.getId(), location);

        // Write to data file.
        writeLocationsFile(context);
    }

    /**
     * Adds weather data for a location.
     *
     * @param context The context.
     * @param weather The weather data to add.
     * @throws Exception If an error occurred.
     */
    public static void addWeather(@NonNull Context context, @NonNull Weather weather) throws Exception {
        // Add weather data to map.
        weatherStore.put(weather.getLocationId(), weather);

        // Write to data file.
        writeWeatherFile(context);
    }

    /**
     * Adds current weather data for a location.
     *
     * @param context    The context.
     * @param current    The current weather data.
     * @param locationID The unique ID (UUID) of the location.
     * @throws Exception If an error occurred.
     */
    public static void addCurrentWeather(@NonNull Context context, @NonNull CurrentWeather current,
                                         @NonNull String locationID) throws Exception {
        // Check if data exists for the location.
        // Overwrite the current weather data if it does, else create weather object.
        if (weatherStore.containsKey(locationID)) {
            Weather weather = weatherStore.get(locationID);
            weather.setCurrent(current);
        } else {
            Weather weather = new Weather(current, null, locationID);
            weatherStore.put(locationID, weather);
        }

        // Write to data file.
        writeWeatherFile(context);
    }

    /**
     * Adds forecast weather data for a location.
     *
     * @param context    The context.
     * @param forecast   The forecast weather data.
     * @param locationID The unique ID (UUID) of the location.
     * @throws Exception If an error occurred.
     */
    public static void addForecastWeather(@NonNull Context context, @NonNull Forecast forecast,
                                          @NonNull String locationID) throws Exception {
        // Check if data exists for the location.
        // Overwrite the forecast weather data if it does, else create weather object.
        if (weatherStore.containsKey(locationID)) {
            Weather weather = weatherStore.get(locationID);
            weather.setForecast(forecast);
        } else {
            Weather weather = new Weather(null, forecast, locationID);
            weatherStore.put(locationID, weather);
        }

        // Write to data file.
        writeWeatherFile(context);
    }

    /**
     * Deletes a location and its associated data.
     *
     * @param context    The context.
     * @param locationID The unique ID (UUID) of the location.
     * @throws Exception If an error occurred.
     */
    public static void deleteLocation(@NonNull Context context, @NonNull String locationID) throws Exception {
        // Delete location.
        locationsStore.remove(locationID);

        // Delete weather data.
        weatherStore.remove(locationID);

        // Write to data files.
        writeLocationsFile(context);
        writeWeatherFile(context);
    }

    /**
     * @param locationID The unique ID (UUID) of the location.
     * @return The location.
     */
    public static Location getLocation(@NonNull String locationID) {
        return locationsStore.get(locationID);
    }

    /**
     * @return An unmodifiable list of locations.
     */
    public static List<Location> getLocations() {
        return Collections.unmodifiableList(new ArrayList<>(locationsStore.values()));
    }

    /**
     * @param locationID The unique ID (UUID) of the location.
     * @return The weather data for a location.
     */
    @Nullable
    public static Weather getWeather(@NonNull String locationID) {
        return weatherStore.get(locationID);
    }

    /**
     * Loads locations from the data file.
     *
     * @param context The context.
     * @throws Exception If an error occurred.
     */
    private static void loadLocations(@NonNull Context context) throws Exception {
        // Check if file exists.
        File file = new File(context.getFilesDir(), LOCATIONS_FILENAME);
        if (!file.exists()) {
            return;
        }

        // Create string builder to hold content of file.
        StringBuilder stringBuilder = new StringBuilder();

        // Read data file.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.openFileInput(LOCATIONS_FILENAME), "UTF-8"))) {
            // Read line.
            String line = reader.readLine();

            // While there are lines, continue to read.
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = reader.readLine();
            }

            // Convert JSON to map.
            String json = stringBuilder.toString();
            Type mapType = new TypeToken<Map<String, Location>>() {
            }.getType();
            locationsStore = GSON.fromJson(json, mapType);
        }
    }

    /**
     * Loads weather from the data file.
     *
     * @param context The context.
     * @throws Exception If an error occurred.
     */
    private static void loadWeather(@NonNull Context context) throws Exception {
        // Check if file exists.
        File file = new File(context.getFilesDir(), WEATHER_FILENAME);
        if (!file.exists()) {
            return;
        }

        // Create string builder to hold content of file.
        StringBuilder stringBuilder = new StringBuilder();

        // Read data file.
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.openFileInput(WEATHER_FILENAME), "UTF-8"))) {
            // Read line.
            String line = reader.readLine();

            // While there are lines, continue to read.
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = reader.readLine();
            }

            // Convert JSON to map.
            String json = stringBuilder.toString();
            Type mapType = new TypeToken<Map<String, Weather>>() {
            }.getType();
            weatherStore = GSON.fromJson(json, mapType);
        }
    }

    /**
     * Writes the locations to the locations data file.
     *
     * @param context The context.
     * @throws Exception If an error occurred.
     */
    private static void writeLocationsFile(@NonNull Context context) throws Exception {
        // Convert locations map to JSON.
        String json = GSON.toJson(locationsStore);

        // Write the JSON to the data file.
        try (FileOutputStream stream = context.openFileOutput(LOCATIONS_FILENAME, Context.MODE_PRIVATE)) {
            byte[] bytes = json.getBytes("UTF-8");
            stream.write(bytes);
        }
    }

    /**
     * Writes the weather data to the weather data file.
     *
     * @param context The context.
     * @throws Exception If an error occurrs.
     */
    private static void writeWeatherFile(@NonNull Context context) throws Exception {
        // Convert weather map to JSON.
        String json = GSON.toJson(weatherStore);

        // Write the JSON to the data file.
        try (FileOutputStream stream = context.openFileOutput(WEATHER_FILENAME, Context.MODE_PRIVATE)) {
            byte[] bytes = json.getBytes("UTF-8");
            stream.write(bytes);
        }
    }

}
