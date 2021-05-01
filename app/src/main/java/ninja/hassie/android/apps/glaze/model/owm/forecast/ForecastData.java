package ninja.hassie.android.apps.glaze.model.owm.forecast;

import com.google.gson.annotations.SerializedName;

import ninja.hassie.android.apps.glaze.model.owm.common.Main;
import ninja.hassie.android.apps.glaze.model.owm.common.Weather;

/**
 * Forecast data JSON model.
 */
public class ForecastData {
    @SerializedName("main")
    private Main main;

    @SerializedName("dt")
    private long timestamp;

    @SerializedName("weather")
    private Weather[] weather;

    public Main getMain() {
        return main;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Weather[] getWeather() {
        return weather;
    }
}
