package ninja.hassie.android.apps.glaze.model.owm.current;

import com.google.gson.annotations.SerializedName;

/**
 * Sys JSON model.
 */
public class Sys {
    @SerializedName("country")
    private String country;

    @SerializedName("sunrise")
    private long sunrise;

    @SerializedName("sunset")
    private long sunset;

    public String getCountry() {
        return country;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }
}
