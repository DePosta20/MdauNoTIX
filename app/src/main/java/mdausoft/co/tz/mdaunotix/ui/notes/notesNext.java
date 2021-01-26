package mdausoft.co.tz.mdaunotix.ui.notes;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import mdausoft.co.tz.mdaunotix.R;

public class notesNext extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        String strtext=getArguments().getString("subject_id");
        View root = inflater.inflate(R.layout.fragment_notes_next, container, false);
        Toast.makeText(requireContext(), strtext, Toast.LENGTH_LONG).show();
        return root;
    }

}