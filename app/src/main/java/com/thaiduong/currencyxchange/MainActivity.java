package com.thaiduong.currencyxchange;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // List of currencies
    private final String[] currencies = {
            "USD",
            "AED",
            "ARS",
            "AUD",
            "BGN",
            "BRL",
            "BSD",
            "CAD",
            "CHF",
            "CLP",
            "CNY",
            "COP",
            "CZK",
            "DKK",
            "DOP",
            "EGP",
            "EUR",
            "FJD",
            "GBP",
            "GTQ",
            "HKD",
            "HRK",
            "HUF",
            "IDR",
            "ILS",
            "INR",
            "ISK",
            "JPY",
            "KRW",
            "KZT",
            "MXN",
            "MYR",
            "NOK",
            "NZD",
            "PAB",
            "PEN",
            "PHP",
            "PKR",
            "PLN",
            "PYG",
            "RON",
            "RUB",
            "SAR",
            "SEK",
            "SGD",
            "THB",
            "TRY",
            "TWD",
            "UAH",
            "UYU",
            "VND",
            "ZAR"
    };
    private final ArrayList<String> currencyList = new ArrayList<>();

    // Spinners to select currencies to exchange
    private Spinner fromSpinner;
    private Spinner toSpinner;

    // Currencies from & to exchange
    private String fromCurrency = "";
    private String toCurrency = "";

    // Input amount to exchange
    private EditText inputEditText;
    private double rateNumber;

    private TextView rateNumberTextView;

    // Request queue from API
    private RequestQueue requestQueue;

    // Current device's vibrating functionality
    private Vibrator vibrator;
    private final int vibratingDuration = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        handleSpinner(fromSpinner);
        handleSpinner(toSpinner);
    }

    private void handleSpinner(final Spinner spinner) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, currencyList);
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner == fromSpinner) {
                    fromCurrency = currencies[position];
                } else {
                    toCurrency = currencies[position];
                }

                vibrator.vibrate(vibratingDuration);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initialize() {
        // Add all currencies to currency list
        currencyList.addAll(Arrays.asList(currencies));

        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);

        inputEditText = findViewById(R.id.inputEditText);
        rateNumberTextView = findViewById(R.id.rateNumberTextView);

        requestQueue = Volley.newRequestQueue(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void fetchData(final double input, String from, final String to) {
        String url = "https://api.exchangerate-api.com/v4/latest/" + from;

        JsonObjectRequest apiRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject rateObject = response.getJSONObject("rates");
                            // Get current currency rate
                            rateNumber = input * rateObject.getDouble(to);
                            // Update text view to display exchanged rate
                            rateNumberTextView.setText(String.format("%.2f", rateNumber));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add created request to request queue to start fetching.
        requestQueue.add(apiRequest);
    }

    public void onConvertButtonClicked(View view) {
        vibrator.vibrate(vibratingDuration);
        if (inputEditText.getText().length() == 0) {
            Toast.makeText(this, "Please enter a value to exchange", Toast.LENGTH_SHORT).show();
        } else {
            double inputNumber = Double.parseDouble(inputEditText.getText().toString());
            fetchData(inputNumber, fromCurrency, toCurrency);
        }
    }
}
