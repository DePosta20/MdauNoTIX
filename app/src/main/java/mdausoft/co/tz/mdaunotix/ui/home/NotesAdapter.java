package mdausoft.co.tz.mdaunotix.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mdausoft.co.tz.mdaunotix.R;
import mdausoft.co.tz.mdaunotix.model.Notes;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Notes> notesArrayList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name_subject, date_time;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name_subject = view.findViewById(R.id.name_subject);
            date_time = view.findViewById(R.id.date_time);
            thumbnail = view.findViewById(R.id.pic_thumbnail);
        }
    }

    // Constructor
    public NotesAdapter(Context ctx, ArrayList<Notes> ModelArrayList){
        inflater = LayoutInflater.from(ctx);
        this.notesArrayList = ModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.this_week_notes, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.MyViewHolder holder, int position) {
        final Notes notes = notesArrayList.get(position);
        holder.name_subject.setText(notes.getSubject_code());
        holder.date_time.setText(notes.getDate_time());
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }
}
