package ninja.hassie.android.apps.glaze.model.lociq;

import com.google.gson.annotations.SerializedName;

/**
 * Location result JSON model.
 */
public class LocationResult {
    @SerializedName("lat")
    private String lat;

    @SerializedName("lon")
    private String lon;

    @SerializedName("display_name")
    private String name;

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }
}
