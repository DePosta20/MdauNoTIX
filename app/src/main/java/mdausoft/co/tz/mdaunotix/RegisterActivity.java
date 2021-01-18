package mdausoft.co.tz.mdaunotix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RegisterActivity extends AppCompatActivity {
    private DB_helper mydb;
    private Function func_obj = new Function();
    private Constants constant = new Constants();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        final String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mydb = new DB_helper(RegisterActivity.this, 0);
        final EditText full_name = findViewById(R.id.full_name);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText phone_no_ = findViewById(R.id.phone_no);
        final Button register_btn = findViewById(R.id.register_btn);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final RadioGroup radioGroup = findViewById(R.id.gender);
        String gender = "";
        int selectedradio = radioGroup.getCheckedRadioButtonId();
        RadioButton genderradioButton = findViewById(selectedradio);
        if(selectedradio == R.id.female){
            gender = "Female";
        } else{
            gender = "Male";
        }
        final String phone_brand = Build.BRAND;
        final String phone_model = Build.MODEL;
        final String finalGender = gender;
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                final String fullname = full_name.getText().toString();
                final String email_id = email.getText().toString();
                final String pass = password.getText().toString();
                final String phone_no = phone_no_.getText().toString();
                String password = func_obj.md5(pass);
                final JSONObject data = new JSONObject();
                try {
                    data.put("fullname", fullname);
                    data.put("email", email_id);
                    data.put("password", password);
                    data.put("phone_no", phone_no);
                    data.put("gender", finalGender);
                    data.put("phone_brand", phone_brand);
                    data.put("phone_model", phone_model);
                    data.put("ip_address", ipAddress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String data_str = data.toString();
                if (fullname.equals("") || email_id.equals("") || pass.equals("")){
                    loadingProgressBar.setVisibility(View.GONE);
                    String welcome = "Please Fill the Required";
                    Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
                } else {
                    Boolean check_user = mydb.check_username(email_id);
                    if (check_user.equals(false)){
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                Constants.URL_REGISTER,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String message = jsonObject.getString("message");
                                            String success = jsonObject.getString("error");
                                            String token = jsonObject.getString("t");
                                            if (success.equals("")){
                                                Boolean insert = mydb.insert_user_data(fullname, email_id, phone_no, pass, phone_brand, phone_model, token);
                                                if (insert.equals(true)) {
                                                    Intent i = new Intent(RegisterActivity.this, Email_validation.class);
                                                    startActivity(i);
                                                }else{
                                                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                                }
                                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                            } else {
                                                System.out.println(jsonObject);
                                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                                loadingProgressBar.setVisibility(View.GONE);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                loadingProgressBar.setVisibility(View.GONE);
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
                    }
                }
            }
        });
    }
    public void goToLoginActivity(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}