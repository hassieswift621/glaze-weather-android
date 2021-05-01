package ninja.hassie.android.apps.glaze.model.owm.common;

import com.google.gson.annotations.SerializedName;

/**
 * Clouds JSON model.
 */
public class Clouds {
    @SerializedName("all")
    private int cloudiness;

    public int getCloudiness() {
        return cloudiness;
    }
}
