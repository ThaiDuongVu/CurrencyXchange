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

    private String[] currencies = {
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
    private ArrayList<String> currencyList = new ArrayList<>();

    private Spinner fromSpinner;
    private Spinner toSpinner;

    private String fromCurrency = "";
    private String toCurrency = "";

    private EditText inputEditText;
    private double rateNumber;

    private TextView rateNumberTextView;

    private RequestQueue requestQueue;

    private Vibrator vibrator;
    private int vibratingDuration = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        spinnerHandler(fromSpinner);
        spinnerHandler(toSpinner);
    }

    private void spinnerHandler(final Spinner spinner) {
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
        currencyList.addAll(Arrays.asList(currencies));

        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);

        inputEditText = findViewById(R.id.inputEditText);
        rateNumberTextView = findViewById(R.id.rateNumberTextView);

        requestQueue = Volley.newRequestQueue(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void exchangeCurrency(final double input, String from, final String to) {
        String url = "https://api.exchangerate-api.com/v4/latest/" + from;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject rates = response.getJSONObject("rates");
                            rateNumber = input * rates.getDouble(to);
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
        requestQueue.add(request);
    }

    public void onConvertButtonClicked(View view) {
        vibrator.vibrate(vibratingDuration);
        if (inputEditText.getText().length() == 0) {
            Toast.makeText(this, "Please enter a value to exchange", Toast.LENGTH_SHORT).show();
        } else {
            double inputNumber = Double.parseDouble(inputEditText.getText().toString());
            exchangeCurrency(inputNumber, fromCurrency, toCurrency);
        }
    }
}
