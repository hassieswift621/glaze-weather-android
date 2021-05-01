package ninja.hassie.android.apps.glaze.api;

import androidx.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A singleton for the retrofitted LocationIQ API.
 */
public class GeocodeRetrofit {

    private static final Retrofit client; // The retrofit client.
    private static final GeocodeService service; // The retrofit service.

    static {
        // Initialise client.
        client = new Retrofit.Builder()
                .baseUrl("https://eu1.locationiq.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Initialise service.
        service = client.create(GeocodeService.class);
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
    public static GeocodeService getService() {
        return service;
    }

}
