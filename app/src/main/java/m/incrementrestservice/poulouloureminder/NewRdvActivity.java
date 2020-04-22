package m.incrementrestservice.poulouloureminder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import m.incrementrestservice.poulouloureminder.model.Rdv;
import m.incrementrestservice.poulouloureminder.model.User;
import m.incrementrestservice.poulouloureminder.notifications.APIService;
import m.incrementrestservice.poulouloureminder.notifications.Client;
import m.incrementrestservice.poulouloureminder.notifications.Data;
import m.incrementrestservice.poulouloureminder.notifications.MyResponse;
import m.incrementrestservice.poulouloureminder.notifications.Sender;
import m.incrementrestservice.poulouloureminder.notifications.Token;
import m.incrementrestservice.poulouloureminder.ui.DatePickerFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class NewRdvActivity extends AppCompatActivity implements DialogueParticipant.DialogueParticipantInterface, DatePickerDialog.OnDateSetListener {

    ImageButton shar_btn;
    EditText titleRdv;
    TextView date;
    ImageButton setdate;
    public EditText adress;
    EditText text;
    ImageButton addRdv;
    ImageButton setadress;
    ProgressBar progressBar;
    HashMap<String, String> hashMapp;
    APIService apiService;
    boolean notify = false;

    private static final String TAG = "NewRdvActivity";
    private static final int ERROR_DIALOGUE_REQUEST = 9001;

    public String addressplace;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String myStr = data.getStringExtra("adresse");
                adress.setText(myStr);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rdv);

        shar_btn = findViewById(R.id.shar_btn);
        titleRdv = findViewById(R.id.Title_Rdv2);
        date = findViewById(R.id.date_Rdv);
        setdate = findViewById(R.id.editdate);
        adress = findViewById(R.id.adress_Rdv);
        text = findViewById(R.id.Rdvtext);
        addRdv = findViewById(R.id.addRdv);
        progressBar = findViewById(R.id.progressBarAddRddv1);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        final Toolbar toolbar = findViewById(R.id.toolbarNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        shar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialogue(hashMapp);
            }
        });

        setadress = findViewById(R.id.editadress1);
        setadress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRdvActivity.this, MapActivity.class);
                startActivityForResult(intent, 1);

            }
        });
        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        adress.setText(addressplace);
        addRdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date1 = date.getText().toString().trim();
                final String titre = titleRdv.getText().toString().trim();
                final String textRdv = text.getText().toString().trim();
                String adress1 = adress.getText().toString().trim();
                if (TextUtils.isEmpty(date1)) {
                    Toast.makeText(NewRdvActivity.this, "Please enter the appointment's day", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(titre)) {
                    Toast.makeText(NewRdvActivity.this, "Please enter the appointment's title", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(textRdv)) {
                    Toast.makeText(NewRdvActivity.this, "Please enter the appointment's text", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.isEmpty(date1) && !TextUtils.isEmpty(titre) && !TextUtils.isEmpty(textRdv)) {

                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    long time = System.currentTimeMillis();
                    final  String idRdv = String.valueOf(time) + "-" + firebaseUser.getUid();
                    HashMap<String, String> hashMap = new HashMap<>();
                    if (hashMapp != null) {

                        hashMap = hashMapp;

                    } else {
                        hashMap.put("id_invit_0", firebaseUser.getUid());

                    }


                    final DatabaseReference databaseRdv = FirebaseDatabase.getInstance().getReference("Rdv");
                    final Rdv rdv = new Rdv(titre, date1, textRdv, firebaseUser.getUid(), titre.toLowerCase(), idRdv, adress1, hashMap);
                    databaseRdv.child(idRdv).setValue(rdv).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            progressBar.setVisibility(GONE);

                            if (task.isSuccessful()) {
                                //sendNotification(hashMapp, "username", titre );
                                Toast.makeText(NewRdvActivity.this, "Appointment added.", Toast.LENGTH_LONG).show();

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users").child(rdv.owner);
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        for (Map.Entry mapentry : rdv.particip.entrySet()) {
                                            sendNotification(mapentry.getValue().toString(), user.username, rdv.title, rdv.id_rdv);
                                            if (notify) {

                                                sendNotification(mapentry.getValue().toString(), user.username, rdv.title, rdv.id_rdv);
                                            }
                                        }
                                        notify = false;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Toast.makeText(NewRdvActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                return;

                            }
                        }
                    });

                }
                onBackPressed();

            }
        });

    }

    private void OpenDialogue(HashMap<String, String> hashMap) {
        DialogueParticipant dialogueParticipant = new DialogueParticipant(hashMap);
        dialogueParticipant.show(getSupportFragmentManager(), "Participator dialogue ");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentdateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        TextView setRdv = findViewById(R.id.date_Rdv);
        setRdv.setText(currentdateString);
    }

    @Override
    public void ApplyAdds(HashMap<String, String> hashMap) {
        hashMapp = hashMap;
    }


    private void sendNotification(final String receiver, final String username, final String msg, final String idrdv) {

        final DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    Data data = new Data("="+firebaseUser.getUid()+"+"+idrdv+"#", R.drawable.logo, "appointment title :" + msg, username+" Added you", receiver);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(NewRdvActivity.this, "Failed!" + response, Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(NewRdvActivity.this, "Notification sent " + response.message(), Toast.LENGTH_LONG).show();
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


}


