package ninja.hassie.android.apps.glaze.model.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import ninja.hassie.android.apps.glaze.model.owm.current.CurrentWeather;
import ninja.hassie.android.apps.glaze.model.owm.forecast.Forecast;

/**
 * Weather data storage JSON model.
 */
public class Weather {
    @SerializedName("current")
    private CurrentWeather current;

    @SerializedName("forecast")
    private Forecast forecast;

    @SerializedName("location_id")
    private String locationID;

    public Weather(@Nullable CurrentWeather current, @Nullable Forecast forecast,
                   @NonNull String locationID) {
        this.current = current;
        this.forecast = forecast;
        this.locationID = locationID;
    }

    /**
     * @return The current weather object.
     */
    public CurrentWeather getCurrent() {
        return current;
    }

    /**
     * @return The forecast object.
     */
    public Forecast getForecast() {
        return forecast;
    }

    /**
     * @return The unique ID (UUID) of the location.
     */
    public String getLocationId() {
        return locationID;
    }

    public void setCurrent(@NonNull CurrentWeather current) {
        this.current = current;
    }

    public void setForecast(@NonNull Forecast forecast) {
        this.forecast = forecast;
    }
}
