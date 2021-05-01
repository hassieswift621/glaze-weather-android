package ninja.hassie.android.apps.glaze.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.TimeZone;

import ninja.hassie.android.apps.glaze.R;
import ninja.hassie.android.apps.glaze.model.owm.common.Weather;

/**
 * Weather utilities.
 */
public class WeatherUtil {

    /**
     * Converts the temperature into the required units depending on the user's preference.
     *
     * @param context     The context.
     * @param temperature The temperature to convert in celsius.
     * @return The temperature in celsius or fahrenheit based on the user's preference.
     */
    public static int convertTemperature(@NonNull Context context, float temperature) {
        // Get preferences.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get temperature units preference.
        String units = preferences.getString(context.getString(R.string.preference_temperature_units_key), "c");

        // Convert temperature if units are fahrenheit.
        if (units.equals("f")) {
            temperature = (temperature * (float) (9 / 5)) + 32;
        }

        // Round temperature to nearest number.
        return Math.round(temperature);
    }

    /**
     * Converts the temperature into the required units depending on the user's preference.
     *
     * @param context   The context.
     * @param windSpeed The wind speed to convert in meters per second.
     * @return The wind speed in KPH or MPH based on the user's preference.
     */
    @NonNull
    public static String convertWindSpeed(@NonNull Context context, float windSpeed) {
        // Get preferences.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get temperature units preference.
        String units = preferences.getString(context.getString(R.string.preference_wind_speed_units_key), "mph");

        if (units.equals("mph")) {
            return Math.round(windSpeed * 2.23693629f) + " MPH";
        }

        return Math.round(windSpeed * 3.6) + " KPH";
    }

    /**
     * Gets the colour for a weather condition.
     *
     * @param context          The context.
     * @param weather          The weather condition object.
     * @param sunriseTimestamp The sunrise timestamp as Unix seconds timestamp.
     * @param sunsetTimestamp  The sunset timestamp as Unix seconds timestamp.
     * @param timezone         The timezone offset in seconds.
     * @return The colour for the weather condition.
     */
    public static int getConditionColour(@NonNull Context context, @NonNull Weather weather,
                                         long sunriseTimestamp, long sunsetTimestamp, int timezone) {
        // Get condition ID.
        int conditionID = weather.getConditionID();

        // Thunderstorm weather conditions.
        if (conditionID >= 200 && conditionID <= 299) {
            return context.getResources().getColor(R.color.weather_thunderstorm);
        }
        // Drizzle weather conditions.
        else if (conditionID >= 300 && conditionID <= 399) {
            return context.getResources().getColor(R.color.weather_drizzle);
        }
        // Rain weather conditions.
        else if (conditionID >= 500 & conditionID <= 599) {
            return context.getResources().getColor(R.color.weather_rain);
        }
        // Snow weather conditions.
        else if (conditionID >= 600 && conditionID <= 699) {
            return context.getResources().getColor(R.color.weather_snow);
        }
        // Atmosphere weather conditions.
        else if (conditionID >= 700 && conditionID <= 799) {
            return context.getResources().getColor(R.color.weather_atmosphere);
        }
        // Clear weather conditions.
        else if (conditionID == 800) {
            if (isNightTime(sunriseTimestamp, sunsetTimestamp, timezone)) {
                return context.getResources().getColor(R.color.weather_clear_night);
            }
            return context.getResources().getColor(R.color.weather_clear_day);
        }
        // Cloudy weather conditions.
        else if (conditionID >= 801 && conditionID <= 809) {
            if (isNightTime(sunriseTimestamp, sunsetTimestamp, timezone)) {
                return context.getResources().getColor(R.color.weather_cloudy_night);
            }
            return context.getResources().getColor(R.color.weather_cloudy_day);
        }

        return context.getResources().getColor(R.color.colorPrimary);
    }

    /**
     * Gets the icon drawable for a weather condition.
     *
     * @param context          The context.
     * @param weather          The weather condition object.
     * @param sunriseTimestamp The sunrise timestamp as Unix seconds timestamp.
     * @param sunsetTimestamp  The sunset timestamp as Unix seconds timestamp.
     * @param timezone         The timezone offset in seconds.
     * @return The icon drawable for the weather condition.
     */
    @Nullable
    public static Drawable getConditionIcon(@NonNull Context context, @NonNull Weather weather,
                                            long sunriseTimestamp, long sunsetTimestamp, int timezone) {
        // Get condition ID.
        int conditionID = weather.getConditionID();

        // Thunderstorm weather conditions.
        if (conditionID >= 200 && conditionID <= 299) {
            return context.getResources().getDrawable(R.drawable.ic_weather_thunderstorm);
        }
        // Drizzle weather conditions.
        else if (conditionID >= 300 && conditionID <= 399) {
            return context.getResources().getDrawable(R.drawable.ic_weather_drizzle);
        }
        // Rain weather conditions.
        else if (conditionID >= 500 & conditionID <= 599) {
            return context.getResources().getDrawable(R.drawable.ic_weather_rain);
        }
        // Snow weather conditions.
        else if (conditionID >= 600 && conditionID <= 699) {
            return context.getResources().getDrawable(R.drawable.ic_weather_snow);
        }
        // Atmosphere weather conditions.
        else if (conditionID >= 700 && conditionID <= 799) {
            return context.getResources().getDrawable(R.drawable.ic_weather_atmosphere);
        }
        // Clear weather conditions.
        else if (conditionID == 800) {
            if (isNightTime(sunriseTimestamp, sunsetTimestamp, timezone)) {
                return context.getResources().getDrawable(R.drawable.ic_weather_clear_night);
            }
            return context.getResources().getDrawable(R.drawable.ic_weather_clear_day);
        }
        // Cloudy weather conditions.
        else if (conditionID >= 801 && conditionID <= 809) {
            return context.getResources().getDrawable(R.drawable.ic_weather_cloudy);
        }

        return null;
    }

    /**
     * Converts the wind direction from degrees to a compass direction.
     *
     * @param degrees The wind direction in degrees.
     * @return The wind direction as a compass direction.
     */
    @NonNull
    public static String convertWindDirection(float degrees) {
        if (degrees == 360) {
            return "N";
        } else if (degrees >= 0 && degrees < 22.5) {
            return "N";
        } else if (degrees >= 22.5 && degrees < 45) {
            return "NNE";
        } else if (degrees >= 45 && degrees < 67.5) {
            return "NE";
        } else if (degrees >= 67.5 && degrees < 90) {
            return "ENE";
        } else if (degrees >= 90 && degrees < 112.5) {
            return "E";
        } else if (degrees >= 112.5 && degrees < 135) {
            return "ESE";
        } else if (degrees >= 135 && degrees < 157.5) {
            return "SE";
        } else if (degrees >= 157.5 && degrees < 180) {
            return "SSE";
        } else if (degrees >= 180 && degrees < 202.5) {
            return "S";
        } else if (degrees >= 202.5 && degrees < 225) {
            return "SSW";
        } else if (degrees >= 225 && degrees < 247.5) {
            return "SW";
        } else if (degrees >= 247.5 && degrees < 270) {
            return "WSW";
        } else if (degrees >= 270 && degrees < 292.5) {
            return "W";
        } else if (degrees >= 292.5 && degrees < 315) {
            return "WNW";
        } else if (degrees >= 315 && degrees < 337.5) {
            return "NW";
        } else if (degrees >= 337.5 && degrees < 360) {
            return "NNW";
        }

        return "N/A";
    }

    /**
     * Calculates whether a location's time is between sunset and sunrise time.
     *
     * @param sunriseTimestamp The sunrise timestamp as Unix seconds timestamp.
     * @param sunsetTimestamp  The sunset timestamp as Unix seconds timestamp.
     * @param timezone         The timezone offset in seconds.
     * @return true if the location's time is between sunset and sunrise time, false otherwise.
     */
    private static boolean isNightTime(long sunriseTimestamp, long sunsetTimestamp, int timezone) {
        // Get current time, sunrise and sunset time as calender instances with GMT as timezone.
        Calendar current = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar sunrise = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar sunset = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        // Set sunrise and sunset times.
        sunrise.setTimeInMillis(sunriseTimestamp * 1000);
        sunset.setTimeInMillis(sunsetTimestamp * 1000);

        // Add the timezone offset.
        current.add(Calendar.SECOND, timezone);
        sunrise.add(Calendar.SECOND, timezone);
        sunset.add(Calendar.SECOND, timezone);

        // Set sunset date to 00/00/0000.
        // We only want to compare the time.
        sunset.set(0, 0, 0);

        // Offset sunrise time relative to next day.
        sunrise.set(Calendar.DATE, current.get(Calendar.DATE) + 1);

        // Get current time with date set to 00/00/0000 to compare with sunset time.
        Calendar currentNoDate = (Calendar) current.clone();
        currentNoDate.set(0, 0, 0);

        // Compare current time with sunrise and sunset time.
        return currentNoDate.getTimeInMillis() >= sunset.getTimeInMillis() &&
                current.getTimeInMillis() < sunrise.getTimeInMillis();
    }

}
