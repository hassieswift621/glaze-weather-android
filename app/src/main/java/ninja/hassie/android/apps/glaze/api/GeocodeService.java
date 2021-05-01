package ninja.hassie.android.apps.glaze.api;

import java.util.List;

import ninja.hassie.android.apps.glaze.model.lociq.LocationResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofitted LocationIQ forward geocode API service.
 */
public interface GeocodeService {

    /**
     * Searches for a location using reverse geocoding.
     *
     * @param key The API key.
     * @param lat The latitude.
     * @param lon The longitude.
     * @return The found result.
     */
    @GET("reverse.php?format=json")
    Call<LocationResult> search(@Query("key") String key, @Query("lat") double lat,
                                @Query("lon") double lon);

    /**
     * Searches for a location using forward geocoding.
     *
     * @param key   The API key.
     * @param query The search query.
     * @return A list of search results.
     */
    @GET("search.php?format=json")
    Call<List<LocationResult>> search(@Query("key") String key, @Query("q") String query);
}
