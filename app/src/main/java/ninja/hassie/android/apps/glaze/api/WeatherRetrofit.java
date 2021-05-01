package ninja.hassie.android.apps.glaze.api;

import androidx.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A singleton for the retrofitted Open Weather Map API.
 */
public class WeatherRetrofit {

    private static final Retrofit client; // The retrofit client.
    private static final WeatherService service; // The retrofit service.

    static {
        // Initialise client.
        client = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Initialise service.
        service = client.create(WeatherService.class);
    }

    /**
     * @return The retrofit client.
     */
    @NonNull
    public static Retrofit getClient() {
        return client;
    }

    /**
     * @return The retrofit service.
     */
    @NonNull
    public static WeatherService getService() {
        return service;
    }

}
