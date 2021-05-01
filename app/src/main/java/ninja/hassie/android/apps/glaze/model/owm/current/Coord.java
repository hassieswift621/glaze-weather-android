package ninja.hassie.android.apps.glaze.model.owm.current;

import com.google.gson.annotations.SerializedName;

/**
 * Coord JSON model.
 */
public class Coord {
    @SerializedName("lat")
    private double lat;

    @SerializedName("lon")
    private double lon;

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
