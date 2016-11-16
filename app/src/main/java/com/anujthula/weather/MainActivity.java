package com.anujthula.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    static EditText cityName;
    TextView resultTextView;
    Button button;
    public void findWeather(View view) {

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        if(cityName.getText().toString() != null) {
            try {
                String encodedCityName = URLEncoder.encode(cityName.getText().toString().toLowerCase(), "UTF-8");
                DownloadTask task = new DownloadTask();
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=936eb75678a1f2a3dabafe201e3349ae&lang=en-US");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Please enter something na!", Toast.LENGTH_LONG);        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        button = (Button) findViewById(R.id.button);
        button.setEnabled(false);
        cityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()==0){
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls)
        {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try
            {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();

                while (data != -1 )
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            }

            catch (Exception e)
            {
                e.printStackTrace();
//                Toast.makeText(getApplicationContext(),"Could not find weather", Toast.LENGTH_LONG);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);
                for (int i = 0; i < arr.length(); i++)
                {

                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != "")
                    {
                        message += main + " : " + description + "\r\n";
                    }

                }

                if(message == "")
                {
                    Toast.makeText(getApplicationContext(),"Could not find weather", Toast.LENGTH_LONG);

                }
                else{
                    resultTextView.setText(message);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather", Toast.LENGTH_LONG);
            }
        }
    }
}
