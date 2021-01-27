package mdausoft.co.tz.mdaunotix.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import mdausoft.co.tz.mdaunotix.DB_helper;
import mdausoft.co.tz.mdaunotix.R;

public class NotesFragment extends Fragment {
    DB_helper my_DB;
    ProgressBar loadingProgressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        my_DB = new DB_helper(requireContext(), 0);
        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        loadingProgressBar = root.findViewById(R.id.loading);
        ListView subject_list = root.findViewById(R.id.subject_list);
        ArrayList<String> subject_arr = my_DB.get_all_subjects();
        if (subject_arr.size() <= 0){
            subject_list.setVisibility(View.GONE);
        } else {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, my_DB.get_all_subjects());
            subject_list.setAdapter(arrayAdapter);
            subject_list.setOnItemClickListener((parent, view, position, id) -> {
                Bundle bundle=new Bundle();
                ConstraintLayout nav_notes_layout = root.findViewById(R.id.nav_notes_layout);
                nav_notes_layout.setVisibility(View.GONE);
                bundle.putString("subject_id", String.valueOf(id));
                Fragment fragment = new notesNext();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_notes_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
        }
        return root;
    }
}