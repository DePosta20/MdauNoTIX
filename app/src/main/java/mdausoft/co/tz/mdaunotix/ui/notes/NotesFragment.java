package mdausoft.co.tz.mdaunotix.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import mdausoft.co.tz.mdaunotix.DB_helper;
import mdausoft.co.tz.mdaunotix.R;

public class NotesFragment extends Fragment {
    DB_helper my_DB;
    ProgressBar loadingProgressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        my_DB = new DB_helper(requireContext(), 0);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        loadingProgressBar = root.findViewById(R.id.loading);
        ListView subject_list = root.findViewById(R.id.subject_list);
        ArrayList<String> subject_arr = my_DB.get_all_subjects();
        if (subject_arr.size() <= 0){
            subject_list.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, my_DB.get_all_subjects());
            subject_list.setAdapter(arrayAdapter);
            subject_list.setOnItemClickListener((parent, view, position, id) -> {
                Intent i = new Intent(requireContext(), notesNext.class);
                Toast.makeText(requireContext(), "Click ListItem Name " + subject_arr.get(position), Toast.LENGTH_LONG)
                        .show();
            });
        }
        return root;
    }
}