package com.app.whatsweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.API_KEY;
    public TextView outputTextView;
    private EditText cityNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityNameEditText = findViewById(R.id.cityName);
        outputTextView = findViewById(R.id.out);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure ?")
                .setMessage("You want to exit ?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.aboutItem) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("About")
                    .setMessage("A Weather By Vishal")
                    .setPositiveButton("Ok", null).show();
            return true;
        } else if (menuItemId == R.id.exitItem) {
            onBackPressed();
            return true;
        } else {
            return false;
        }
    }

    public void getWeather(View view) {
        try {
            String cityName = cityNameEditText.getText().toString();
            String URL = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=";
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(URL + API_KEY);

            // Hide Keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(cityNameEditText.getWindowToken(), 0);
        } catch (Exception e) {
            Toast.makeText(this, "Could not found weather :(", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = inputStreamReader.read();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Could not found weather :(", Toast.LENGTH_SHORT).show());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                StringBuilder message = new StringBuilder();

                JSONArray array = new JSONArray(weatherInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonPart = array.getJSONObject(i);
                    String main, description;
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                    if (!main.equals("") && !description.equals("")) {
                        message.append(main).append(": ").append(description).append("\r\n");
                    }
                }

                if (message.toString().equals("")) {
                    MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Could not found weather :(", Toast.LENGTH_SHORT).show());
                } else {
                    outputTextView.setText(message);
                }

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.this.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Could not found weather :(", Toast.LENGTH_SHORT).show());
            }


        }
    }
}