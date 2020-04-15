package m.incrementrestservice.poulouloureminder.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import m.incrementrestservice.poulouloureminder.MessageActivity;
import m.incrementrestservice.poulouloureminder.NotesActivity;
import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.model.Notes;
import m.incrementrestservice.poulouloureminder.model.User;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private Context mContext;
    private List<Notes> mNotes;

    public NotesAdapter(Context mContext, List<Notes> mNotes) {
        this.mContext = mContext;
        this.mNotes = mNotes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView Title;
        public CircleImageView noteIcon;
        public  TextView date;


        public  ViewHolder(View itemView){
            super(itemView);

            Title = itemView.findViewById(R.id.Title_note);
            noteIcon = itemView.findViewById(R.id.image_note_item);
            date= itemView.findViewById(R.id.date_note);

        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notes_item, parent,false);
        return new NotesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final  Notes notes= mNotes.get(position);
        holder.Title.setText(notes.title);
        holder.date.setText(notes.date);

        Random random = new Random();
        int red = random.nextInt(255);
        int green= random.nextInt(255);
        int blue = random.nextInt(255);
        holder.noteIcon.setBorderColor(Color.argb(255,red,green,blue));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NotesActivity.class);
                intent.putExtra( "noteid",notes.id_note);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }



}
