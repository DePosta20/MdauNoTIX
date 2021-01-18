package mdausoft.co.tz.mdaunotix.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mdausoft.co.tz.mdaunotix.BuildConfig;
import mdausoft.co.tz.mdaunotix.Constants;
import mdausoft.co.tz.mdaunotix.DB_helper;
import mdausoft.co.tz.mdaunotix.Function;
import mdausoft.co.tz.mdaunotix.R;
import mdausoft.co.tz.mdaunotix.model.Notes;

public class HomeFragment extends Fragment {
    DB_helper my_DB;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        my_DB = new DB_helper(requireContext(), 0);
        Button add_new_note = root.findViewById(R.id.add_new_note);
        RecyclerView recyclerView = root.findViewById(R.id.notes_recyclerview);
        LinearLayout no_note = root.findViewById(R.id.no_note);
        ArrayList<Notes> todayNotes_list = notesList();
        if (todayNotes_list.size() <= 0) {
            no_note.setVisibility(View.VISIBLE);
        } else {
            NotesAdapter adapter = new NotesAdapter(requireContext(), todayNotes_list);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        add_new_note.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), mdausoft.co.tz.mdaunotix.addNote.class);
            startActivity(intent);
        });
        return root;
    }

    private ArrayList<Notes> notesList(){
        ArrayList<Notes> list = new ArrayList<>();
        String date_time = Function.get_today_date();
        String sql = "SELECT * FROM "+ Constants.TABLE_NAME_notes + " WHERE date_time='" + date_time + "'";
        Cursor cursor = my_DB.get_from_table(sql);
        if (cursor.moveToFirst()) {
            do {
                Notes notes = new Notes();
                notes.setImage_path(cursor.getString(1));
                notes.setSubject_code(cursor.getString(4));
                notes.setDate_time(cursor.getString(5));
                list.add(notes);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}