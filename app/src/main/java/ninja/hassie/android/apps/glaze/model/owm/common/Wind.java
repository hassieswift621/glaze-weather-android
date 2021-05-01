package ninja.hassie.android.apps.glaze.model.owm.common;

import com.google.gson.annotations.SerializedName;

/**
 * Wind JSON model.
 */
public class Wind {
    @SerializedName("deg")
    private int direction;

    @SerializedName("speed")
    private float speed;

    public float getDirection() {
        return direction;
    }

    public float getSpeed() {
        return speed;
    }
}
