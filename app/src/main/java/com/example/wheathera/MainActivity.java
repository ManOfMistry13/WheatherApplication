package com.example.wheathera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText etCity, etCountry;
    TextView tvResult;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "e53301e27efa0b66d05045d91b2742d3";
    DecimalFormat df = new DecimalFormat("#.##");
    TextView userName;
    Button logout;
    GoogleSignInClient gClient;
    GoogleSignInOptions gOptions;

    private Button saveButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveButton = findViewById(R.id.save_button);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        tvResult = findViewById(R.id.tvResult);
        logout = findViewById(R.id.logout);
        userName = findViewById(R.id.userName);
        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);
        readDataFromSharePreference();

        // Sauvegarder le nom de la ville
        saveButton.setOnClickListener(v -> {
            String s = tvResult.getText().toString();
            Intent intent = new Intent(getApplicationContext(), DataActivity.class);
            intent.putExtra("message_key", s);
            startActivity(intent);
            try {
                saveDataInSharedPreferences(tvResult.getText().toString().trim());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null){
            String gName = gAccount.getDisplayName();
            userName.setText(gName);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
            }
        });
    }

    private void saveDataInSharedPreferences (String city) throws JSONException {
        /*SharedPreferences sharedPreferences = getSharedPreferences("MySharePref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonCity = new JSONObject();
        jsonCity.put("name", city.getCityName());
        jsonCity.put("temp", city.getTemp());
        jsonCity.put("speed", city.getSpeed());
        jsonArray.put(jsonCity);
        myEdit.putString(CityList, jsonArray.toString());
        myEdit.apply();

        /*myEdit.putString("city", city);
        myEdit.commit();
        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show(); */
        SharedPreferences sharedPreferences = getSharedPreferences("MySharePref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putString("city", city);
        myEdit.commit();
        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
        tvResult.setText(city);
    }

    private void readDataFromSharePreference() {
        SharedPreferences sh = getSharedPreferences("MySharePref", MODE_PRIVATE);
        String city = sh.getString("CITY", "");
        if(city.isEmpty()){
            saveButton.setText("Save Data");
        }
        else {
            saveButton.setText("Update data ");
        }
    }

    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        if(city.equals("")){
            tvResult.setText("City field can not be empty!");
        }else{
            if(!country.equals("")){
                tempUrl = url + "?q=" + city + "," + country + "&appid=" + appid;
            }else{
                tempUrl = url + "?q=" + city + "&appid=" + appid;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        tvResult.setTextColor(Color.rgb(68,134,199));
                        output += "Current weather of " + cityName + " (" + countryName + ")"
                                + "\n Temp: " + df.format(temp) + " °C"
                                + "\n Feels Like: " + df.format(feelsLike) + " °C"
                                + "\n Humidity: " + humidity + "%"
                                + "\n Description: " + description
                                + "\n Wind Speed: " + wind + "m/s (meters per second)"
                                + "\n Cloudiness: " + clouds + "%"
                                + "\n Pressure: " + pressure + " hPa";
                        tvResult.setText(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}