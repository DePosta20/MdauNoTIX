package mdausoft.co.tz.mdaunotix.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import mdausoft.co.tz.mdaunotix.R;

public class notesNext extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        String strtext = getArguments().getString("subject_id");
        View root = inflater.inflate(R.layout.fragment_notes_next, container, false);
        return root;
    }

}