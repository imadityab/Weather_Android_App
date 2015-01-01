package app.sunshine.android.imadityab.com.sunshine;

/**
 * Created by Aditya on 12/25/2014.
 */

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    String LOG_TAG= ForecastFragment.class.getSimpleName();
private ArrayAdapter<String> mForecastAdapter;
    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // this.getActivity().getMenuInflater(R.menu.forecastfragment, menu);

        inflater.inflate(R.menu.forecastfragment,menu);
       // return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        if(id == R.id.action_settings){

            Intent intent= new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {


        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = pref.getString(getString(R.string.pref_default_loc_key),
                getString(R.string.pref_default_loc_value));

        FetchWeatherTask task= new FetchWeatherTask();

        Log.v(LOG_TAG,"Calling weather service with location "+location);

        task.execute(location);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> weekForecast = new ArrayList<String>();

        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview,weekForecast);

        final ListView weatherListView= (ListView)rootView.findViewById(R.id.listview_forecast);

        weatherListView.setAdapter(mForecastAdapter);

        weatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Context context=null;
                CharSequence text = weatherListView.getAdapter().getItem(i).toString();
               // int duration = Toast.LENGTH_SHORT;
                Intent intent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,text);
                //Toast toast = Toast.makeText(getActivity(),text,duration);

                //toast.show()

                startActivity(intent);
            }
        });
          return rootView;
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, ArrayList<String>> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

         String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        String country = "US";
         String mode = "json";
         String query = "27606";
         String units = "metric";
         String days = "7";

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (strings!=null){
                mForecastAdapter.clear();
                mForecastAdapter.addAll(strings);
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Log.v(LOG_TAG,"Finally in do In Background");
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            final String BASE_PARAM = baseUrl;
            final String COUNTRY_PARAM = "country";
            final String MODE_PARAM = "mode";
            final String QUERY_PARAM = "q";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";

            ArrayList<String> wlist=null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {

                query = params[0];
                //http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
                Uri weatherUri=Uri.parse(BASE_PARAM).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,query)
                        .appendQueryParameter(MODE_PARAM,mode)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(COUNTRY_PARAM,country)
                        .appendQueryParameter(DAYS_PARAM,days).build();

                URL url= new URL(weatherUri.toString());

                Log.v(LOG_TAG,url.toString());
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG,forecastJsonStr);

                String[] weatherArr= this.getWeatherDataFromJson(forecastJsonStr,Integer.parseInt(days));
                wlist = new ArrayList<String>();


                for(String dayString: weatherArr) {
                    Log.v(LOG_TAG,dayString);
                    wlist.add(dayString);
                }


            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {



                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                return wlist;
            }
           // return null;
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
            return format.format(date).toString();
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String unitType= prefs.getString(getString(R.string.pref_temp_units_key),
                    getString(R.string.pref_temp_units_metric));

            if(unitType.equalsIgnoreCase(getString(R.string.pref_temp_units_imperial))){
                high = high*1.8 +32;
                low = low*1.8 +32;
            }
            else if(!unitType.equalsIgnoreCase(getString(R.string.pref_temp_units_metric)))
            {
                Log.v(LOG_TAG,"Unit Type not found : "+ unitType);
            }

            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DATETIME = "dt";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;
        }
    }
}
