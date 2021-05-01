package ninja.hassie.android.apps.glaze.model.owm.common;

import com.google.gson.annotations.SerializedName;

/**
 * Main JSON model.
 */
public class Main {
    @SerializedName("humidity")
    private int humidity;

    @SerializedName("pressure")
    private int pressure;

    @SerializedName("temp")
    private float tempCurrent;

    @SerializedName("feels_like")
    private float tempFeelsLike;

    @SerializedName("temp_max")
    private float tempMax;

    @SerializedName("temp_min")
    private float tempMin;

    public int getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public float getTempCurrent() {
        return tempCurrent;
    }

    public float getTempFeelsLike() {
        return tempFeelsLike;
    }

    public float getTempMax() {
        return tempMax;
    }

    public float getTempMin() {
        return tempMin;
    }
}
