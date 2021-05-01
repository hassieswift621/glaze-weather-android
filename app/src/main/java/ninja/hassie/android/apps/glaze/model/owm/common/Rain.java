package ninja.hassie.android.apps.glaze.model.owm.common;

import com.google.gson.annotations.SerializedName;

public class Rain {

    @SerializedName("1h")
    private float rain1Hour;

    @SerializedName("3h")
    private float rain3Hours;

    public float getRain1Hour() {
        return rain1Hour;
    }

    public float getRain3Hours() {
        return rain3Hours;
    }

}
