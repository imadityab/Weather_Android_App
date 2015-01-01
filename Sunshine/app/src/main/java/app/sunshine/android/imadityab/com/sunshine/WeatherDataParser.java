package app.sunshine.android.imadityab.com.sunshine;

/**
 * Created by Aditya on 12/29/2014.
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataParser {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        // TODO: add parsing code here

        JSONObject json= new JSONObject(weatherJsonStr);
        JSONArray listArray = (JSONArray) json.get("list");

        JSONObject day = (JSONObject)listArray.get(dayIndex);
        JSONObject daytemp = (JSONObject) day.get("temp");
        double d = Double.parseDouble(daytemp.get("max").toString());

        return d;
    }

}

