package m.incrementrestservice.poulouloureminder.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import m.incrementrestservice.poulouloureminder.MessageActivity;
import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.model.Chat;
import m.incrementrestservice.poulouloureminder.model.User;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    String theLastMessage;

    public userAdapter(Context mContext, List<User> mUsers,boolean isChat ){
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profileImage;
        public ImageView img_on;
        public ImageView img_off;
        public  TextView last_message;
        public ImageButton checkbox;



        public  ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.Username_chat);

            profileImage = itemView.findViewById(R.id.image_profile);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_message= itemView.findViewById(R.id.last_message);
            checkbox= itemView.findViewById(R.id.checkbox);

        }
    }

    private void lastMessage(final String userid, final TextView lastmsg){

        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getreceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getreceiver().equals(userid)&& chat.getreceiver().equals(firebaseUser.getUid() )) {

                            theLastMessage = chat.getmessage();


                            if(chat.isIsseen()== true){

                                lastmsg.setTextColor(Color.argb(100,0,0,0));
                            }else{
                                lastmsg.setTextColor(Color.argb(255,63,81,181));
                            }
                        }
                    }

                    switch (theLastMessage){

                        case "default":
                            lastmsg.setText("No message");
                            break;

                            default:
                                lastmsg.setText(theLastMessage);
                                break;
                    }
                    theLastMessage = "default";
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_item, parent,false);
        return new userAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final  User user = mUsers.get(position);
        holder.username.setText(user.username);
        if(user.ImageURL.equals("default")){
            holder.profileImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.ImageURL).into(holder.profileImage);
        }


        if (isChat){

            lastMessage(user.id_user,holder.last_message);
        }else {

            holder.last_message.setVisibility(View.GONE);
        }

        if(isChat){
            if(user.status.equals("Online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);

            }else {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }
        }else{
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra( "userid",user.id_user);
                mContext.startActivity(intent);
            }
        });

    }





    @Override
    public int getItemCount() {
        return mUsers.size();
    }



}
