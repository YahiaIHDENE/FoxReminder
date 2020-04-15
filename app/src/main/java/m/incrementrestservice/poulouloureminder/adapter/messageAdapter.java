package m.incrementrestservice.poulouloureminder.adapter;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.params.BlackLevelPattern;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import m.incrementrestservice.poulouloureminder.MessageActivity;
import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.model.Chat;
import m.incrementrestservice.poulouloureminder.model.User;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.ViewHolder>{

    public static final  int MSG_TYPE_LEFT = 0;
    public static final  int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;

    FirebaseUser firebaseUser;

    public messageAdapter(Context mContext, List<Chat> mChat){
        this.mContext = mContext;
        this.mChat= mChat;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        //public ImageView profileImage;
        public  TextView textSeen;

        public  TextView lastMessage;

        public  ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.showMessage);
            //profileImage = itemView.findViewById(R.id.image_profile);
            textSeen = itemView.findViewById(R.id.textSeen);
            lastMessage = itemView.findViewById(R.id.last_message);

        }
    }


    @NonNull
    @Override
    public messageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent,false);
            return new messageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent,false);
            return new messageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull messageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getmessage());

        /*if(chat.getImageURL().equals("default")){
            holder.profileImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getImageURL()).into(holder.profileImage);
        }*/

        if(position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.textSeen.setText("Seen at "+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"h"+Calendar.getInstance().get(Calendar.MINUTE)+"mn");
            }else {
                holder.textSeen.setText("Delivred "+  Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"h"+Calendar.getInstance().get(Calendar.MINUTE)+"mn");
            }
        }else {
            holder.textSeen.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }


    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (((firebaseUser.getUid())).equals((mChat.get(position).getSender()))){
            return MSG_TYPE_RIGHT;
        }else {
            return  MSG_TYPE_LEFT;
        }

    }

}
