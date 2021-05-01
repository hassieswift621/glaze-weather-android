package ninja.hassie.android.apps.glaze.api;

import ninja.hassie.android.apps.glaze.model.owm.current.CurrentWeather;
import ninja.hassie.android.apps.glaze.model.owm.forecast.Forecast;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofitted Open Weather Map API service.
 */
public interface WeatherService {
    /**
     * Gets the current weather for a location.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return The current weather.
     */
    @GET("weather?units=metric")
    Call<CurrentWeather> currentWeather(@Query("appid") String key,
                                        @Query("lat") String lat, @Query("lon") String lon);

    /**
     * Gets the weather forecast for a location.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return The forecast.
     */
    @GET("forecast?units=metric")
    Call<Forecast> forecast(@Query("appid") String key,
                            @Query("lat") String lat, @Query("lon") String lon);
}
