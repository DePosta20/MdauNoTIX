package mdausoft.co.tz.mdaunotix.ui.notes;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import mdausoft.co.tz.mdaunotix.Constants;
import mdausoft.co.tz.mdaunotix.DB_helper;
import mdausoft.co.tz.mdaunotix.R;

public class notesNext extends Fragment {
    DB_helper my_DB;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        my_DB = new DB_helper(requireContext(), 0);
        String subject_id = getArguments().getString("subject_id");
        View root = inflater.inflate(R.layout.fragment_notes_next, container, false);
        ListView subject_notes = root.findViewById(R.id.subject_notes);
        String sql = "SELECT * FROM "+ Constants.TABLE_NAME_notes + " WHERE subject_id='" + subject_id + "'";
        Cursor rows = my_DB.get_from_table(sql);
        ArrayList<String> date_list = new ArrayList<>();
        if (rows.moveToFirst()) {
            do {
                date_list.add(rows.getString(5));
            } while (rows.moveToNext());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, date_list);
        subject_notes.setAdapter(arrayAdapter);
        return root;
    }
}