package ninja.hassie.android.apps.glaze.model.owm.forecast;

import com.google.gson.annotations.SerializedName;

/**
 * Forecast JSON model.
 */
public class Forecast {
    @SerializedName("list")
    private ForecastData[] list;

    public ForecastData[] getList() {
        return list;
    }
}
