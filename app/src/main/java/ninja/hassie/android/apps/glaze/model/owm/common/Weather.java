package ninja.hassie.android.apps.glaze.model.owm.common;

import com.google.gson.annotations.SerializedName;

/**
 * Weather JSON model.
 */
public class Weather {
    @SerializedName("description")
    private String conditionDescription;

    @SerializedName("id")
    private int conditionID;

    @SerializedName("main")
    private String conditionGroup;

    public String getConditionDescription() {
        return conditionDescription;
    }

    public int getConditionID() {
        return conditionID;
    }

    public String getConditionGroup() {
        return conditionGroup;
    }
}
