package mdausoft.co.tz.mdaunotix.ui.subjects;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mdausoft.co.tz.mdaunotix.Constants;
import mdausoft.co.tz.mdaunotix.DB_helper;
import mdausoft.co.tz.mdaunotix.R;

public class SubjectFragment extends Fragment {
    DB_helper my_DB;
    ProgressBar loadingProgressBar;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        my_DB = new DB_helper(requireContext(), 0);
        View root = inflater.inflate(R.layout.fragment_subjects, container, false);
        loadingProgressBar = root.findViewById(R.id.loading);
        Button add_new_subject_btn = root.findViewById(R.id.add_new_subject_btn);
        Button save_new_subject_btn = root.findViewById(R.id.save_new_subject_btn);
        Button pull_subjects_btn = root.findViewById(R.id.pull_subjects);
        TextView check_subjects = root.findViewById(R.id.check_subjects);
        ListView subject_list = root.findViewById(R.id.subject_list);
        ArrayList<String> subject_arr = my_DB.get_all_subjects();
        if (subject_arr.size() <= 0){
            check_subjects.setVisibility(View.VISIBLE);
            subject_list.setVisibility(View.GONE);
        } else {
            pull_subjects_btn.setVisibility(View.GONE);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, my_DB.get_all_subjects());
            subject_list.setAdapter(arrayAdapter);
            subject_list.setOnItemClickListener((parent, view, position, id) ->
                    Toast.makeText(requireContext(), "Click ListItem Name " + subject_arr.get(position), Toast.LENGTH_LONG)
                    .show());
        }
        LinearLayout subjects_list = root.findViewById(R.id.subjects_list);
        LinearLayout add_subject_form = root.findViewById(R.id.add_subject_form);
        add_new_subject_btn.setOnClickListener(v -> {
            subjects_list.setVisibility(View.GONE);
            add_subject_form.setVisibility(View.VISIBLE);
        });
        //////////////////////////////////////////////////////////////////////////////////////
        save_new_subject_btn.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            TextView subject_name = root.findViewById(R.id.subject_name);
            TextView subject_code = root.findViewById(R.id.subject_code);
            Cursor user_data = my_DB.get_user_data();
            String subjectName = subject_name.getText().toString();
            String subjectCode = subject_code.getText().toString();
            user_data.moveToFirst();
            final String token = user_data.getString(user_data.getColumnIndex("token"));
            final String email = user_data.getString(user_data.getColumnIndex("email"));
            user_data.close();
            final JSONObject data = new JSONObject();
            try {
                data.put("e", email);
                data.put("subject_name", subjectName);
                data.put("subject_code", subjectCode);
                data.put("t", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data_str = data.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_ADD_SUBJECT,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            String success = jsonObject.getString("error");
                            Boolean insert = my_DB.insert_subject(subjectName, subjectCode);
                            if (success.equals("0") && (insert)) {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                subjects_list.setVisibility(View.VISIBLE);
                                add_subject_form.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(requireContext(), "Unexpected Error", Toast.LENGTH_SHORT).show();
                            }
                            loadingProgressBar.setVisibility(View.GONE);
                            assert getFragmentManager() != null;
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false);
                            }
                            ft.detach(this).attach(this).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("data", data_str);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.add(stringRequest);
        });
        //////////////////////////////////////////////////////////////////////////////////////
        pull_subjects_btn.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            Cursor user_data = my_DB.get_user_data();
            user_data.moveToFirst();
            final String token = user_data.getString(user_data.getColumnIndex("token"));
            final String email = user_data.getString(user_data.getColumnIndex("email"));
            user_data.close();
            final JSONObject data = new JSONObject();
            try {
                data.put("e", email);
                data.put("t", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data_str = data.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_GET_SUBJECT,
                    response -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            String success = jsonObject.getString("error");
                            JSONArray subjects_arr = jsonObject.getJSONArray("subjects");
                            int arr_size = subjects_arr.length();
                            for (int i = 0; i < arr_size; i++) {
                                JSONObject objects = subjects_arr.getJSONObject(i);
                                String subject_name = objects.getString("subject_name");
                                String subject_code = objects.getString("subject_code");
                                my_DB.insert_subject(subject_name, subject_code);
                            }
                            if (success.equals("0") && (arr_size > 0)) {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(requireContext(), "Unexpected Error", Toast.LENGTH_SHORT).show();
                            }
                            loadingProgressBar.setVisibility(View.GONE);
                            assert getFragmentManager() != null;
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false);
                            }
                            ft.detach(this).attach(this).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("data", data_str);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.add(stringRequest);
        });
        return root;
    }
}