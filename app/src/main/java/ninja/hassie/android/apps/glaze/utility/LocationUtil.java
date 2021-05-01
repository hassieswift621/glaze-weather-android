package ninja.hassie.android.apps.glaze.utility;

import androidx.annotation.NonNull;

import ninja.hassie.android.apps.glaze.model.data.Location;

/**
 * Location utilities.
 */
public class LocationUtil {

    /**
     * Splits a location name.
     *
     * @param location The location to split.
     * @return The split location name.
     */
    @NonNull
    public static String[] splitName(@NonNull Location location) {
        return location.getName().split(", ");
    }

}
