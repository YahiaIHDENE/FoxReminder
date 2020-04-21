package m.incrementrestservice.poulouloureminder;

import android.app.DatePickerDialog;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

import m.incrementrestservice.poulouloureminder.model.Notes;
import m.incrementrestservice.poulouloureminder.ui.DatePickerFragment;

import static android.view.View.GONE;

public class NewNoteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Toolbar toolbar2;
    EditText titleNote;
    TextView date ;
    EditText text ;
    ImageButton setdate;
    ImageButton addNote;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        titleNote = findViewById(R.id.Title_note2);
        date = findViewById(R.id.date_note);
        setdate = findViewById(R.id.editdate1);
        text =findViewById(R.id.note);
        addNote = findViewById(R.id.AddNote);
        progressBar = findViewById(R.id.progressBarAddNote);


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

        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String date1 = date.getText().toString().trim();
                String titre = titleNote.getText().toString().trim();
                final String note = text.getText().toString().trim();

                if(TextUtils.isEmpty(date1)){
                    Toast.makeText(NewNoteActivity.this, "Please enter the note's day",Toast.LENGTH_LONG).show();
                    return;
                } if(TextUtils.isEmpty(titre)){
                    Toast.makeText(NewNoteActivity.this, "Please enter the note's title",Toast.LENGTH_LONG).show();
                    return;
                } if(TextUtils.isEmpty(note)){
                    Toast.makeText(NewNoteActivity.this, "Please enter the note's text",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.isEmpty(date1) && !TextUtils.isEmpty(titre) && !TextUtils.isEmpty(note)) {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    long time = System.currentTimeMillis();
                    String idNote =String.valueOf(time)+"-"+firebaseUser.getUid();
                    final DatabaseReference databaseNotes = FirebaseDatabase.getInstance().getReference("Notes");
                    final Notes notes = new Notes(titre, date1, note,firebaseUser.getUid(),titre.toLowerCase(),idNote);
                    databaseNotes.child(idNote).setValue(notes).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            progressBar.setVisibility(GONE);

                            if (task.isSuccessful()) {
                                //getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new notesFragment()).commit();
                                //toolbar2 = findViewById(R.id.toolbar);
                                //setSupportActionBar(toolbar2);
                                //toolbar2.setTitle("Notes");
                                Toast.makeText(NewNoteActivity.this, "Note addes.",Toast.LENGTH_LONG).show();

                            }else{
                                Toast.makeText(NewNoteActivity.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                return;

                            }

                        }
                    });

                }

                onBackPressed();
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
}
