package ninja.hassie.android.apps.glaze.model.data;

import com.google.gson.annotations.SerializedName;

/**
 * Location data storage JSON model.
 */
public class Location {
    @SerializedName("id")
    private String id;

    @SerializedName("lat")
    private String lat;

    @SerializedName("lon")
    private String lon;

    @SerializedName("name")
    private String name;

    public Location(String id, String lat, String lon, String name) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    /**
     * @return The unique ID (UUID) of the location.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The latitude of the location.
     */
    public String getLat() {
        return lat;
    }

    /**
     * @return The longitude of the location.
     */
    public String getLon() {
        return lon;
    }

    /**
     * @return The name of the location.
     */
    public String getName() {
        return name;
    }
}
