package m.incrementrestservice.poulouloureminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import java.util.List;

import m.incrementrestservice.poulouloureminder.adapter.participantAdapter;
import m.incrementrestservice.poulouloureminder.model.Rdv;
import m.incrementrestservice.poulouloureminder.notifications.APIService;
import m.incrementrestservice.poulouloureminder.notifications.Client;
import m.incrementrestservice.poulouloureminder.notifications.Data;
import m.incrementrestservice.poulouloureminder.notifications.MyResponse;
import m.incrementrestservice.poulouloureminder.notifications.Sender;
import m.incrementrestservice.poulouloureminder.notifications.Token;
import m.incrementrestservice.poulouloureminder.ui.DatePickerFragment;
import m.incrementrestservice.poulouloureminder.ui.HomeFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class NewRdvActivity extends AppCompatActivity implements DialogueParticipant.DialogueParticipantInterface, DatePickerDialog.OnDateSetListener{

    ImageButton shar_btn;
    EditText titleRdv;
    TextView date ;
    ImageButton setdate ;
    EditText adress ;
    EditText text ;
    ImageButton addRdv;
    ImageButton setadress;
    ProgressBar progressBar;
    List<String> listP;
    APIService apiService;

    private  static final String TAG = "NewRdvActivity";
    private  static final int ERROR_DIALOGUE_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rdv);

        if (isServiceOK()){
            init();
        }

        shar_btn = findViewById(R.id.shar_btn);
        titleRdv = findViewById(R.id.Title_Rdv2);
        date = findViewById(R.id.date_Rdv);
        setdate = findViewById(R.id.editdate);
        adress = findViewById(R.id.adress_Rdv);
        text =findViewById(R.id.Rdvtext);
        addRdv = findViewById(R.id.addRdv);
        progressBar = findViewById(R.id.progressBarAddRddv1);

        apiService = Client.getClient("https://googleapi.com/").create(APIService .class);

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
                OpenDialogue();
            }
        });

        setadress = findViewById(R.id.editadress1);
        setadress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRdvActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });
        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        addRdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date1 = date.getText().toString().trim();
                final String titre = titleRdv.getText().toString().trim();
                final String textRdv = text.getText().toString().trim();
                String adress1 = adress.getText().toString().trim();
                if(TextUtils.isEmpty(date1)){
                    Toast.makeText(NewRdvActivity.this, "Please enter the appointment's day",Toast.LENGTH_LONG).show();
                    return;
                } if(TextUtils.isEmpty(titre)){
                    Toast.makeText(NewRdvActivity.this, "Please enter the appointment's title",Toast.LENGTH_LONG).show();
                    return;
                } if(TextUtils.isEmpty(textRdv)){
                    Toast.makeText(NewRdvActivity.this, "Please enter the appointment's text",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.isEmpty(date1) && !TextUtils.isEmpty(titre) && !TextUtils.isEmpty(textRdv)) {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    long time = System.currentTimeMillis();
                    String idRdv =String.valueOf(time)+"-"+firebaseUser.getUid();
                    HashMap<String, String> hashMap = new HashMap<>();
                    if (listP!=null) {

                        for (int i=0;i<listP.size();i++){
                            hashMap.put("id_invit_"+(i+1), listP.get(i));
                        }
                    }else{
                        hashMap.put("id_invit_0", "default");

                    }

                    final DatabaseReference databaseRdv = FirebaseDatabase.getInstance().getReference("Rdv");
                    final Rdv rdv = new Rdv(titre, date1, textRdv,firebaseUser.getUid(),titre.toLowerCase(),idRdv,adress1,hashMap);
                    databaseRdv.child(idRdv).setValue(rdv).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            progressBar.setVisibility(GONE);

                            if (task.isSuccessful()) {
                                sendNotification(listP, "username", titre );
                                Toast.makeText(NewRdvActivity.this, "Appointment addes.",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(NewRdvActivity.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                return;

                            }
                        }
                    });

                }
                onBackPressed();

            }
        });

    }

    private void OpenDialogue() {
        DialogueParticipant dialogueParticipant = new DialogueParticipant();
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
    public void ApplyAdds(List<String> list) {
        listP = list;
    }



    private void sendNotification(final List<String> receiver, final String username, final String msg) {
        final DatabaseReference token = FirebaseDatabase.getInstance().getReference("users");
        for(int i=0;i<receiver.size();i++){
            Query query = token.orderByKey().equalTo(receiver.get(i));
            final int finalI = i;
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Token token1 = snapshot.getValue(Token.class);
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher_round,username+":"+ msg, " New Appointment",receiver.get(finalI) );
                        Sender sender = new Sender(data, token1.getToken());

                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code()==200){
                                            if (response.body().success !=1){
                                                Toast.makeText(NewRdvActivity.this, "Failed! ",Toast.LENGTH_LONG).show();

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

    }


    public  void init(){


    }
    public boolean isServiceOK(){
        Log.d(TAG, "isServiceOK : checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(NewRdvActivity.this);
        if (available== ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOK : google service is working");
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOK : an error accord but we can fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(NewRdvActivity.this, available , ERROR_DIALOGUE_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(NewRdvActivity.this,"We can't make map requests", Toast.LENGTH_LONG).show();
        }
        return false;
    }


}
