package ninja.hassie.android.apps.glaze.model.owm.current;

import com.google.gson.annotations.SerializedName;

import ninja.hassie.android.apps.glaze.model.owm.common.Clouds;
import ninja.hassie.android.apps.glaze.model.owm.common.Main;
import ninja.hassie.android.apps.glaze.model.owm.common.Rain;
import ninja.hassie.android.apps.glaze.model.owm.common.Weather;
import ninja.hassie.android.apps.glaze.model.owm.common.Wind;

/**
 * Current Weather JSON model.
 */
public class CurrentWeather {
    @SerializedName("clouds")
    private Clouds clouds;

    @SerializedName("coord")
    private Coord coord;

    @SerializedName("main")
    private Main main;

    @SerializedName("rain")
    private Rain rain;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("weather")
    private Weather[] weather;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("name")
    private String city;

    private long timestamp;

    @SerializedName("timezone")
    private int timezone;

    /**
     * Initialises a new instance of the current weather object.
     */
    public CurrentWeather() {
        // Set timestamp.
        timestamp = System.currentTimeMillis();
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Coord getCoord() {
        return coord;
    }

    public Main getMain() {
        return main;
    }

    public Rain getRain() {
        return rain;
    }

    public Sys getSys() {
        return sys;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public Wind getWind() {
        return wind;
    }

    public String getCity() {
        return city;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getTimezone() {
        return timezone;
    }

}
