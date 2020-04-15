package m.incrementrestservice.poulouloureminder.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import m.incrementrestservice.poulouloureminder.NotesActivity;
import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.RdvActivity;
import m.incrementrestservice.poulouloureminder.model.Notes;
import m.incrementrestservice.poulouloureminder.model.Rdv;

public class RdvAdapter extends RecyclerView.Adapter<RdvAdapter.ViewHolder> {

    private Context mContext;
    private List<Rdv> mRdv;

    public RdvAdapter(Context mContext, List<Rdv> mRdv) {
        this.mContext = mContext;
        this.mRdv = mRdv;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rdv_item, parent,false);
        return new RdvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final  Rdv rdv= mRdv.get(position);
        holder.Title.setText(rdv.title);
        holder.date.setText(rdv.date);
        holder.adress.setText(rdv.adress);

        Random random = new Random();
        int red = random.nextInt(255);
        int green= random.nextInt(255);
        int blue = random.nextInt(255);
        holder.RdvIcon.setBorderColor(Color.argb(255,red,green,blue));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RdvActivity.class);
                intent.putExtra( "rdvid",rdv.id_rdv);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRdv.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView Title;
        public CircleImageView RdvIcon;
        public  TextView date;
        public  TextView adress;


        public  ViewHolder(View itemView){
            super(itemView);

            Title = itemView.findViewById(R.id.Title_Rdv);
            RdvIcon = itemView.findViewById(R.id.image_Rdv_item);
            date= itemView.findViewById(R.id.date_Rdv);
            adress= itemView.findViewById(R.id.adress_Rdv);

        }
    }


}