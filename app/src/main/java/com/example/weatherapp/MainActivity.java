package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static String BaseUrl = "http://api.openweathermap.org/";
    public static String AppId = "42336339585316ea1ab95b215c6f4e7c";
    private TextView weatherData;
    private EditText cityName;
    private Button btn;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherData = findViewById(R.id.weatherInfo);
        cityName = findViewById(R.id.cityText);
        btn = findViewById(R.id.btn);
        image =  findViewById(R.id.currentWeather);
        getCurrentData();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentData();
            }
        });


    }


    void getCurrentData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData(cityName.getText().toString(),"metric", AppId);
        call.enqueue(new Callback<WeatherResponse>() {


            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    String stringBuilder = "Country: " +
                            weatherResponse.sys.country +
                            "\n" +
                            "Temperature: " +
                            weatherResponse.main.temp + " °C " +
                            "\n" +
                            "Temperature(Min): " +
                            weatherResponse.main.temp_min + " °C " +
                            "\n" +
                            "Temperature(Max): " +
                            weatherResponse.main.temp_max + " °C " +
                            "\n" +
                            "Humidity: " +
                            weatherResponse.main.humidity +
                            "\n" +
                            "Pressure: " +
                            weatherResponse.main.pressure + " hPa" +
                            "\n" +
                            "Wind " +
                            weatherResponse.wind.speed + " M/S" +
                            "\n" +
                            "Clouds " +
                            weatherResponse.clouds.all + " %";


                   if(weatherResponse.rain != null && weatherResponse.clouds.all < 50.0){
                       image.setImageResource(R.drawable.sunny);
                    }
                    else{
                       image.setImageResource(R.drawable.rainclouds);
                   }

                    if(weatherResponse.clouds.all > 50.0){
                        image.setImageResource(R.drawable.cloudy);
                    }

                    if(weatherResponse.wind.speed > 3.5 && weatherResponse.clouds.all < 50){
                        image.setImageResource(R.drawable.wind);
                    }

                    if(weatherResponse.wind.speed > 3.5 && weatherResponse.clouds.all > 50){
                        image.setImageResource(R.drawable.cloudywindy);
                    }
                    else{
                        image.setImageResource(R.drawable.sunny);
                    }

                    weatherData.setText(stringBuilder);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherData.setText(t.getMessage());
            }
        });
    }
}
