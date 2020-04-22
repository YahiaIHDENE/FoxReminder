package m.incrementrestservice.poulouloureminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.model.User;

public class participantAdapter extends RecyclerView.Adapter<participantAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    public HashMap<String,String> hashMap;

    public participantAdapter(Context mContext, List<User> mUsers, HashMap<String,String> hashMap){
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.hashMap = hashMap;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profileImage;
        public ImageView checkbox;

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
        if(user.ImageURL.equals("default")){
            holder.profileImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.ImageURL).into(holder.profileImage);
        }
        if (hashMap!=null){
            for (Map.Entry<String,String> mapentry : hashMap.entrySet()){
                if (user.id_user.equals(mapentry.getValue())){
                    holder.checkbox.setVisibility(View.VISIBLE);
                }
            }

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                /*if (i<Integer.getInteger(entry.getKey())){
                    i = Integer.getInteger(entry.getKey())+1;
                }*/
            }
        }else{
            hashMap= new HashMap<>();
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String id_owner = (String)hashMap.get("invite_0");
        if (id_owner!=null){
            if (id_owner.equals(firebaseUser.getUid())) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hashMap.values().remove(user.id_user);
                        holder.checkbox.setVisibility(View.GONE);

                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (!hashMap.containsValue(user.id_user)){

                            String time = String.valueOf(System.currentTimeMillis());
                            hashMap.put("invite_" + time, user.id_user);
                            holder.checkbox.setVisibility(View.VISIBLE);
                        }
                        return true;
                    }
                });
            }
        }else {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hashMap.values().remove(user.id_user);
                        holder.checkbox.setVisibility(View.GONE);

                    }


                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String time = String.valueOf(System.currentTimeMillis());
                        hashMap.put("invite_" + time, user.id_user);
                        holder.checkbox.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}



