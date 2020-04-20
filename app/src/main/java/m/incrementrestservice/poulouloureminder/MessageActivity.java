package m.incrementrestservice.poulouloureminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import m.incrementrestservice.poulouloureminder.adapter.messageAdapter;
import m.incrementrestservice.poulouloureminder.model.Chat;
import m.incrementrestservice.poulouloureminder.model.User;
import m.incrementrestservice.poulouloureminder.notifications.APIService;
import m.incrementrestservice.poulouloureminder.notifications.Client;
import m.incrementrestservice.poulouloureminder.notifications.Data;
import m.incrementrestservice.poulouloureminder.notifications.MyResponse;
import m.incrementrestservice.poulouloureminder.notifications.Sender;
import m.incrementrestservice.poulouloureminder.notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    ImageView image_profile;
    TextView username;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    ImageButton btn_send;
    EditText textSend;

    Intent intent;

    messageAdapter messageAdapter1;
    List<Chat> mchat;

   RecyclerView recyclerView;
   ValueEventListener seenListner;

    String useridIntent;
    boolean notify = false;

    APIService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/

                onBackPressed();


            }
        });

        apiService = Client.getClient("https://fcm.googleapi.com/").create(APIService.class);

        image_profile =findViewById(R.id.image_profile);
        username = findViewById(R.id.Username_chat);
        btn_send = findViewById(R.id.buttonSend);
        textSend = findViewById(R.id.textSend);

        intent= getIntent();
        useridIntent = intent.getStringExtra("userid");


        recyclerView =findViewById(R.id.messagesSend);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(useridIntent);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                username.setText(user.username);
               /*if(user.ImageURL().equals("default")){
                    profile_image.setImageRessource(R.mipmap.ic_launcher_round)
                }else{
                    Glide.with(getApplicationContext().load(user.getImageURL()).intro(image_profile));
                }*/

               readMessage(firebaseUser.getUid(),useridIntent);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify =true;
                String msg = textSend.getText().toString();
                if (!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),useridIntent,msg);

                }else{
                    Toast.makeText(MessageActivity.this, "You cant send empty message", Toast.LENGTH_LONG).show();
                }
                textSend.setText("");
            }
        });

        Seen_message(useridIntent);

    }

    private  void  Seen_message(final String userid){

        mDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        seenListner = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if ((chat.getreceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid))) {
                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("isseen" ,true);

                        snapshot.getRef().updateChildren(hashMap);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void sendMessage(String sender, final String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("sender" ,sender);
        hashMap.put("receiver" ,receiver);
        hashMap.put("message" ,message);
        hashMap.put("isseen" ,false);

        reference.push().setValue(hashMap);

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(useridIntent);
        final DatabaseReference chatref2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(useridIntent).child(firebaseUser.getUid());

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatref.child("id").setValue(useridIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatref2.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //sendNotification(receiver, user.username, msg);
                if (notify){
                    sendNotification(receiver, user.username, msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendNotification(String receiver, final String username, final String msg) {
        final DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher_round,username+":"+ msg, " New message", useridIntent);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code()==200){
                                        if (response.body().success !=1){
                                            Toast.makeText(MessageActivity.this, "Failed! ",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void readMessage(final String myId, final String userId/*, imageURL*/){

        mchat = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if ((chat.getreceiver().equals(myId) && chat.getSender().equals(userId)) || (chat.getreceiver().equals(userId) && chat.getSender().equals(myId))){
                        mchat.add(chat);

                    }

                    messageAdapter1 = new messageAdapter(MessageActivity.this, mchat/*,   imageURL*/);
                    recyclerView.setAdapter(messageAdapter1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void  Status(String status){
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        mDatabase.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.removeEventListener(seenListner);
        Status("Offline");
    }
}
