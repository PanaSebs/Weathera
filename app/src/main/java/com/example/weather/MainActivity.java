package com.example.weather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView weatherTextView;
    private Button getWeatherButton;

    private static final String API_KEY = "384f5168eb6ba9afd904d8d50f7cf718";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        weatherTextView = findViewById(R.id.weatherTextView);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString().trim();
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;

                FetchWeatherTask task = new FetchWeatherTask();
                task.execute(url);
            }
        });
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Leer la respuesta de la API
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String description = weatherObject.getString("description");

                    JSONObject mainObject = jsonObject.getJSONObject("main");
                    double temperature = mainObject.getDouble("temp");

                    String weather = "Clima actual: " + description + "\n";
                    weather += "Temperatura: " + temperature + "°C";

                    weatherTextView.setText(weather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

