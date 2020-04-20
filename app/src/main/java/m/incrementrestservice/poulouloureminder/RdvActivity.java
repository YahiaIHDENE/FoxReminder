package m.incrementrestservice.poulouloureminder;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import m.incrementrestservice.poulouloureminder.model.Rdv;
import m.incrementrestservice.poulouloureminder.ui.DatePickerFragment;

import static android.view.View.GONE;

public class RdvActivity extends AppCompatActivity implements DialogueParticipant.DialogueParticipantInterface, DatePickerDialog.OnDateSetListener{

    CircleImageView icone;
    TextView titleRdv2;
    TextView titleRdv;
    //ImageButton shar_btn;
    ImageButton added_shar ;
    TextView date ;
    ImageButton setdate;
    EditText adress_rdv ;
    ImageButton setadress;
    EditText text ;
    TextView owner ;
    ImageButton setRdv ,deleterdv;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    String rdvid;
    Intent intent;
    HashMap<String, String> hashMap;
    HashMap<String, String> hashMapp;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rdv);

        Toolbar toolbar = findViewById(R.id.toolbarNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        icone = findViewById(R.id.image_Rdv);
        titleRdv = findViewById(R.id.Title_Rdv);
        titleRdv2 = findViewById(R.id.Title_Rdv2);
        added_shar = findViewById(R.id.added_btn);
        date = findViewById(R.id.date_Rdv);
        adress_rdv = findViewById(R.id.adress_Rdv1);
        text =findViewById(R.id.Rdvtext);
        owner =findViewById(R.id.owner);

        setdate = findViewById(R.id.editdate);
        setadress = findViewById(R.id.editadress);

        deleterdv = findViewById(R.id.deleteRdv);
        setRdv = findViewById(R.id.setRdv);
        progressBar = findViewById(R.id.progressBarAddRddv);

        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        intent= getIntent();
        rdvid = intent.getStringExtra("rdvid");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Rdv").child(rdvid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final  Rdv rdvs = dataSnapshot.getValue(Rdv.class);
                if(rdvs!=null){
                    titleRdv.setText(rdvs.title);
                    titleRdv.setHint(rdvs.title);
                    titleRdv2.setText(rdvs.title);
                    titleRdv2.setHint(rdvs.title);
                    date.setText(rdvs.date);
                    date.setHint(rdvs.date);
                    adress_rdv.setText(rdvs.adress);
                    adress_rdv.setHint(rdvs.adress);
                    text.setText(rdvs.text);
                    text.setHint(rdvs.text);

                     hashMap = rdvs.particip;


                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(rdvs.owner);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            owner.setText("Edited  by : "+dataSnapshot.child("username").getValue().toString());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Random random = new Random();
                    int red = random.nextInt(255);
                    int green= random.nextInt(255);
                    int blue = random.nextInt(255);
                    icone.setBorderColor(Color.argb(255,red,green,blue));

                    added_shar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OpenDialogue(hashMap);

                        }
                    });

                    deleterdv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delete_Rdv(rdvs);
                        }
                    });

                    setRdv.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            set_Rdv(rdvs);
                        }

                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentdateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        TextView date = findViewById(R.id.date_Rdv);
        date.setText(currentdateString);
    }

    public  void delete_Rdv(Rdv rdv){
        if (firebaseUser.getUid().equals(rdv.owner)){
            AlertDialog.Builder alerteDialog = new AlertDialog.Builder(this);
            alerteDialog.setTitle("Delete the appointment!!");
            alerteDialog.setIcon(R.drawable.ic_delete_note);
            alerteDialog.setMessage("would you like to delete the appointment ?");
            alerteDialog.setCancelable(false);
            alerteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mDatabase = FirebaseDatabase.getInstance().getReference("Rdv").child(rdvid);
                    progressBar.setVisibility(View.VISIBLE);
                    mDatabase.removeValue();
                    Toast.makeText(RdvActivity.this, "appointment "+titleRdv.getText().toString()+" deleted .",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(GONE);
                    onBackPressed();

                }
            });
            alerteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(RdvActivity.this, "Undo", Toast.LENGTH_LONG).show();


                }
            });

            AlertDialog alertDialogfinal = alerteDialog.create();
            alertDialogfinal.show();
        }else {
            Toast.makeText(RdvActivity.this, "You are not the appointment's owner .", Toast.LENGTH_LONG).show();

        }


    }

    public  void set_Rdv(Rdv rdv){
        if (firebaseUser.getUid().equals(rdv.owner)){
            AlertDialog.Builder alerteDialog = new AlertDialog.Builder(this);
            alerteDialog.setTitle("Set appointment");
            alerteDialog.setIcon(R.drawable.ic_modify_note);
            alerteDialog.setMessage("would you like to update the appointment ?");
            alerteDialog.setCancelable(false);
            alerteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("Rdv").child(rdvid);
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            progressBar.setVisibility(View.VISIBLE);


                            String date1 = date.getText().toString().trim();
                            String titre = titleRdv2.getText().toString().trim();
                            String rdv = text.getText().toString().trim();
                            String adress = adress_rdv.getText().toString().trim();
                            HashMap<String, String> hashMap1 = new HashMap<>();

                            if (hashMapp!=null) {

                                hashMap = hashMapp;
                            }else{
                                hashMap1.put("id_invit_0", "default");

                            }

                            HashMap<String , Object> hashMap = new HashMap<>();
                            hashMap.put("title" ,titre);
                            hashMap.put("date" ,date1);
                            hashMap.put("text" ,rdv);
                            hashMap.put("owner" ,firebaseUser.getUid());
                            hashMap.put("search" ,titre.toLowerCase());
                            hashMap.put("id_rdv" ,rdvid);
                            hashMap.put("adress" ,adress);
                            hashMap.put("particip" ,hashMap1);
                            Toast.makeText(RdvActivity.this, "appointment "+titleRdv.getText().toString()+" updated .",Toast.LENGTH_LONG).show();

                            mDatabase.updateChildren(hashMap);
                            progressBar.setVisibility(GONE);



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    onBackPressed();


                }
            });
            alerteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(RdvActivity.this, "Undo", Toast.LENGTH_LONG).show();


                }
            });

            AlertDialog alertDialogfinal = alerteDialog.create();
            alertDialogfinal.show();
        }else {
            Toast.makeText(RdvActivity.this, "You are not the appointment's owner !", Toast.LENGTH_LONG).show();

        }

    }

    private void OpenDialogue(HashMap<String, String> hashMap) {
        DialogueParticipant dialogueParticipant = new DialogueParticipant(hashMap);
        dialogueParticipant.show(getSupportFragmentManager(), "Participator dialogue ");
    }

    @Override
    public void ApplyAdds(HashMap<String, String> hashMap) {
        hashMapp = hashMap;
    }



}
