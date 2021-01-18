package mdausoft.co.tz.mdaunotix;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mdausoft.co.tz.mdaunotix.DB_helper;
import mdausoft.co.tz.mdaunotix.R;

public class LoginActivity extends AppCompatActivity {
    private Function func_obj = new Function();
    private DB_helper mydb;
    private Constants constant = new Constants();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mydb = new DB_helper(LoginActivity.this, 0);

        final EditText email_ = findViewById(R.id.email);
        final EditText password_ = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login_btn);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        final String phone_brand = Build.BRAND;
        final String phone_model = Build.MODEL;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                final String email = email_.getText().toString();
                final String password = func_obj.md5(password_.getText().toString());
                final JSONObject data = new JSONObject();
                try {
                    data.put("email", email);
                    data.put("password", password);
                    data.put("phone_brand", phone_brand);
                    data.put("phone_model", phone_model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String data_str = data.toString();
                if (password.equals("") || email.equals("")) {
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Please Fill the Required", Toast.LENGTH_LONG).show();
                } else {
                    Boolean check_user = mydb.check_username(email);
                    if (check_user.equals(false)) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                Constants.URL_LOGIN,
                                response -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String message = jsonObject.getString("message");
                                        String success = jsonObject.getString("error");
                                        if (success.equals("")) {
                                            String fullname = jsonObject.getString("fullname");
                                            String phone_no = jsonObject.getString("phone_no");
                                            String token = jsonObject.getString("t");
                                            String is_verified = jsonObject.getString("e_verification");
                                            Boolean insert = mydb.insert_user_data(fullname, email, phone_no, password, phone_brand, phone_model, token);
                                            if (insert.equals(true)) {
                                                if (is_verified.equals("1")){
                                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Intent i = new Intent(LoginActivity.this, Email_validation.class);
                                                    startActivity(i);
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                            }
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                            loadingProgressBar.setVisibility(View.GONE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("data", data_str);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }
                }
            }
        });
    }


    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    //////////////////////////////////////////////////////////////////
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void goToRegisterActivity(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }
}