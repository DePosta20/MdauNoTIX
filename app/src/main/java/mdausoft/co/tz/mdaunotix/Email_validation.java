package mdausoft.co.tz.mdaunotix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Email_validation extends AppCompatActivity {
    private DB_helper myDB;
    private Function func_obj = new Function();
    private Constants constant = new Constants();
    ProgressBar loadingProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_email_validation);
        myDB = new DB_helper(this, 0);
        Objects.requireNonNull(getSupportActionBar()).hide();
        final EditText confirmation_code = findViewById(R.id.confirmation_code);
        final Button confirm_button = findViewById(R.id.confirm_button);
        loadingProgressBar = findViewById(R.id.loading);
        super.onCreate(savedInstanceState);
        confirm_button.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            final String code = confirmation_code.getText().toString();
            Cursor user_data = myDB.get_user_data();
            try {
                user_data.moveToFirst();
                final String token = user_data.getString(user_data.getColumnIndex("token"));
                final String email = user_data.getString(user_data.getColumnIndex("email"));
                user_data.close();
                final JSONObject data = new JSONObject();
                try {
                    data.put("t", token);
                    data.put("e", email);
                    data.put("code", code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String data_str = data.toString();
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Constants.URL_EMAIL_VERIFICATION,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String message = jsonObject.getString("message");
                                    String success = jsonObject.getString("error");
                                    if (success.equals("")){
                                            Intent i = new Intent(Email_validation.this, MainActivity.class);
                                            startActivity(i);
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("data", data_str);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void resend_code(final View view) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        Cursor user_data = myDB.get_user_data();
        try {
            user_data.moveToFirst();
            final String token = user_data.getString(user_data.getColumnIndex("token"));
            final String email = user_data.getString(user_data.getColumnIndex("email"));
            user_data.close();
            final JSONObject data = new JSONObject();
            try {
                data.put("t", token);
                data.put("e", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String data_str = data.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_RESEND_CODE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String message = jsonObject.getString("message");
                                String success = jsonObject.getString("error");
                                if (success.equals("")){
                                    Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                     }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("data", data_str);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}