package com.example.jim84_000.myweather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private ListView lv1;
    private ArrayList<String> weatherData = new ArrayList<String>();
    private static final String TAG="MyActivity";
    private String currentlocation="Taipei";

    Button btnJSON;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        lv1=(ListView)findViewById(R.id.ListView01);
        lv1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, weatherData));
        btnJSON = (Button) findViewById(R.id.btnJSON);
        btnJSON.setOnClickListener(new Button.OnClickListener()

        {
            public void onClick(View v) {
                EditText input_location = (EditText) findViewById(R.id.input_edittext);
                currentlocation=input_location.getText().toString();
                new AsyncGetUrl().execute();
            }
        });
    }

    private class AsyncGetUrl extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            examineJSON();
            return null;
        }

        protected void onPostExecute(final Void unused) {
            lv1.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1 , weatherData));
            this.dialog.dismiss();
        }

    }


    void examineJSON()
    {
        try
        {
            weatherData.clear(); //clear list if button is pressed again

            String weatherUrl = "http://api.openweathermap.org/data/2.5/forecast?q="+currentlocation+"&units=metric&mode=json&APPID=8492ad2f91e6cf20cbb1589a65bc90ce";
            String jsontext = getStringContent(weatherUrl);

            JSONObject weather = new JSONObject(jsontext);
            JSONObject city = weather.getJSONObject("city");
            Log.e(TAG, weather.toString());
            Log.e(TAG, city.toString());
            JSONArray list = weather.getJSONArray("list");
            Log.e(TAG, String.valueOf(list.length()));

            for(int i = 0 ; i < list.length(); i++){
                Log.e(TAG, list.getJSONObject(i).getString("dt_txt") + formatForecast(list.getJSONObject(i).getJSONObject("main")));
            }

            String currentWeatherDescription = list.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
            Log.e(TAG, currentWeatherDescription);
            String currentTemperature = list.getJSONObject(0).getJSONObject("main").getString("temp");
            Log.e(TAG, currentTemperature);


            String currentWeather = currentTemperature + " C° ";
            currentWeather += currentWeatherDescription;
            Log.e(TAG, currentWeather);

            //JSONArray weatherForecast = city.getJSONArray("weather");

            weatherData.add("Current Weather");
            weatherData.add(currentWeather); //current weather

            weatherData.add("");
            weatherData.add("Five Day Forecast");

            for(int i = 1 ; i < list.length(); i++){
                weatherData.add(list.getJSONObject(i).getString("dt_txt") + formatForecast(list.getJSONObject(i).getJSONObject("main")));
            }
            lv1=(ListView)findViewById(R.id.ListView01);

        }
        catch (Exception je)
        {

        }
    }

    private String formatForecast(JSONObject main) throws JSONException {
        String result = "";
        String high = main.getString("temp_max");
        String low = main.getString("temp_min");
        result = ": Hi " + high + "C° - " + "Lo " + low + "C°";
        return result;
    }


    public static String getStringContent(String uri) throws Exception {

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(uri));
            HttpResponse response = client.execute(request);
            InputStream ips  = response.getEntity().getContent();
            BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));

            StringBuilder sb = new StringBuilder();
            String s;
            while(true )
            {
                s = buf.readLine();
                if(s==null || s.length()==0)
                    break;
                sb.append(s);

            }
            buf.close();
            ips.close();
            return sb.toString();

        }
        finally {
            // any cleanup code...
        }
    }


}