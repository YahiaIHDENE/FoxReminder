package m.incrementrestservice.poulouloureminder.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;

import m.incrementrestservice.poulouloureminder.DialogueParticipant;
import m.incrementrestservice.poulouloureminder.MessageActivity;
import m.incrementrestservice.poulouloureminder.NewRdvActivity;
import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.RdvActivity;
import m.incrementrestservice.poulouloureminder.model.User;

import static android.content.Context.MODE_PRIVATE;

public class participantAdapter extends RecyclerView.Adapter<participantAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    public   List<String> list = new LinkedList<>();

    public participantAdapter(Context mContext, List<User> mUsers){
        this.mContext = mContext;
        this.mUsers = mUsers;


    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profileImage;
        public ImageButton checkbox;




        public  ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.Username_chat);
            profileImage = itemView.findViewById(R.id.image_profile);
            checkbox= itemView.findViewById(R.id.checkbox);



        }
    }

    @NonNull
    @Override
    public participantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_item, parent,false);
        return new participantAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final participantAdapter.ViewHolder holder, int position) {

        final  User user = mUsers.get(position);
        holder.username.setText(user.username);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(user.id_user);
                holder.checkbox.setVisibility(View.GONE);


            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                list.add(user.id_user);
                holder.checkbox.setVisibility(View.VISIBLE);
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }





}



