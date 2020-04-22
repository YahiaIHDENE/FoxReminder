package m.incrementrestservice.poulouloureminder.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import m.incrementrestservice.poulouloureminder.NotesActivity;
import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.model.Notes;

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
        public ImageView checkbox;


        public  ViewHolder(View itemView){
            super(itemView);

            Title = itemView.findViewById(R.id.Title_note);
            noteIcon = itemView.findViewById(R.id.image_note_item);
            date= itemView.findViewById(R.id.date_note);
            checkbox= itemView.findViewById(R.id.selectednote);

        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notes_item, parent,false);
        return new NotesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                delete_notes( notes,holder);

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public  void delete_notes(final Notes notes, final NotesAdapter.ViewHolder holder){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("notes").child(notes.id_note);
        if (firebaseUser.getUid().equals(notes.owner)){
            holder.checkbox.setVisibility(View.VISIBLE);
            AlertDialog.Builder alerteDialog = new AlertDialog.Builder(mContext);
            alerteDialog.setTitle("Delete the appointment!!");
            alerteDialog.setIcon(R.drawable.ic_delete_note);
            alerteDialog.setMessage("would you like to delete the appointment ?");
            alerteDialog.setCancelable(false);
            alerteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mDatabase.removeValue();
                    Toast.makeText(mContext, "appointment "+notes.title+" deleted .",Toast.LENGTH_LONG).show();

                }
            });
            alerteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(mContext, "Undo", Toast.LENGTH_LONG).show();
                    holder.checkbox.setVisibility(View.INVISIBLE);



                }
            });

            AlertDialog alertDialogfinal = alerteDialog.create();
            alertDialogfinal.show();
        }else {

        }


    }

}
