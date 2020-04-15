package m.incrementrestservice.poulouloureminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
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
import m.incrementrestservice.poulouloureminder.model.Notes;
import m.incrementrestservice.poulouloureminder.model.User;
import m.incrementrestservice.poulouloureminder.ui.DatePickerFragment;
import m.incrementrestservice.poulouloureminder.ui.notesFragment;

public class NotesActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    CircleImageView icone;
    TextView title_note;
    TextView title_note2;
    TextView date_note;
    ImageButton setdate;
    EditText text ;
    ImageButton setNote ,deletenote;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    String noteid;
    Intent intent;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

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


        icone = findViewById(R.id.image_note);
        title_note = findViewById(R.id.Title_note);
        title_note2 = findViewById(R.id.Title_note2);
        date_note = findViewById(R.id.date_note);
        setdate = findViewById(R.id.editdate1);
        text =findViewById(R.id.note);

        setNote = findViewById(R.id.setNote);
        deletenote = findViewById(R.id.deletenote);

        progressBar = findViewById(R.id.progressBarEdit);


        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });




        intent= getIntent();
        noteid = intent.getStringExtra("noteid");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Notes").child(noteid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Notes notes = dataSnapshot.getValue(Notes.class);
                if(notes!=null){
                    title_note.setText(notes.title);
                    title_note.setHint(notes.title);
                    title_note2.setText(notes.title);
                    title_note2.setHint(notes.title);
                    date_note.setText(notes.date);
                    date_note.setHint(notes.date);
                    text.setText(notes.text);
                    text.setHint(notes.text);

                    Random random = new Random();
                    int red = random.nextInt(255);
                    int green= random.nextInt(255);
                    int blue = random.nextInt(255);
                    icone.setBorderColor(Color.argb(255,red,green,blue));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deletenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_Note();
            }
        });


        setNote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                set_Note();
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
        TextView newdate = findViewById(R.id.date_note);
        newdate.setText(currentdateString);
    }



    public  void delete_Note(){
        AlertDialog.Builder alerteDialog = new AlertDialog.Builder(this);
        alerteDialog.setTitle("Delete the appointment!!");
        alerteDialog.setIcon(R.drawable.ic_delete_note);
        alerteDialog.setMessage("would you like to delete the appointment ?");
        alerteDialog.setCancelable(false);
        alerteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase = FirebaseDatabase.getInstance().getReference("Notes").child(noteid);
                progressBar.setVisibility(View.VISIBLE);
                mDatabase.removeValue();
                //getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new notesFragment()).commit();
                Toast.makeText(NotesActivity.this, "Note "+title_note2.getText().toString() +" deleted.",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                onBackPressed();


            }
        });
        alerteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(NotesActivity.this, "Undo", Toast.LENGTH_LONG).show();


            }
        });

        AlertDialog alertDialogfinal = alerteDialog.create();
        alertDialogfinal.show();

    }

    public  void set_Note(){
        AlertDialog.Builder alerteDialog = new AlertDialog.Builder(this);
        alerteDialog.setTitle("Set note");
        alerteDialog.setIcon(R.drawable.ic_modify_note);
        alerteDialog.setMessage("would you like to update the bote ?");
        alerteDialog.setCancelable(false);
        alerteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDatabase = FirebaseDatabase.getInstance().getReference("Notes").child(noteid);
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.VISIBLE);


                        String date = date_note.getText().toString().trim();
                        String titre = title_note2.getText().toString().trim();
                        String note = text.getText().toString().trim();

                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("title" ,titre);
                        hashMap.put("date" ,date);
                        hashMap.put("text" ,note);
                        hashMap.put("owner" ,firebaseUser.getUid());
                        hashMap.put("search" ,titre.toLowerCase());
                        hashMap.put("id_note" ,noteid);

                        mDatabase.updateChildren(hashMap);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(NotesActivity.this, "Note "+title_note2.getText().toString() +" updated.",Toast.LENGTH_LONG).show();



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

                Toast.makeText(NotesActivity.this, "Undo", Toast.LENGTH_LONG).show();


            }
        });

        AlertDialog alertDialogfinal = alerteDialog.create();
        alertDialogfinal.show();

    }
}
