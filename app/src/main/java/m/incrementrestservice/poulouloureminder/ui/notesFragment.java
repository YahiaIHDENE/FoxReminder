package m.incrementrestservice.poulouloureminder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.util.Collections;
import java.util.List;

import m.incrementrestservice.poulouloureminder.NewNoteActivity;
import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.adapter.NotesAdapter;
import m.incrementrestservice.poulouloureminder.model.Notes;

public class notesFragment extends Fragment {

    private RecyclerView recyclerView_notes;
    private NotesAdapter notesAdapter;
    private List<Notes> mNotes;
    EditText searchNotes;

    ProgressBar progressBar;

    ImageButton btn_add;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        progressBar = view.findViewById(R.id.progressBarUsers);
        progressBar.setVisibility(View.VISIBLE);

        btn_add = view.findViewById(R.id.addNoteBtn);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewNoteActivity.class));

            }
        });



        recyclerView_notes = view.findViewById(R.id.noteslist);
        recyclerView_notes.setHasFixedSize(true);
        recyclerView_notes.setLayoutManager(new LinearLayoutManager(getContext()));

        searchNotes =view.findViewById(R.id.search_bar_note);
        searchNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Search_Notes(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNotes = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBarUsers);
        progressBar.setVisibility(View.VISIBLE);
        Read_Notes();
        return view ;
    }

    private void Search_Notes(String s) {

        final  FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Notes").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNotes.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Notes notes = snapshot.getValue(Notes.class);
                    assert notes != null;
                    assert fUser != null;
                    if (fUser.getUid().equals(notes.owner)) {
                        mNotes.add(notes);
                    }
                }

                Collections.reverse(mNotes);
                notesAdapter = new NotesAdapter(getContext(),mNotes);
                recyclerView_notes.setAdapter(notesAdapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void Read_Notes() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notes");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchNotes.getText().toString().equals("")){
                    mNotes.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Notes notes = snapshot.getValue(Notes.class);
                        assert notes!= null;
                        assert firebaseUser!= null;
                        if(firebaseUser.getUid().equals(notes.owner)){
                            mNotes.add(notes);
                        }


                    }
                progressBar.setVisibility(View.GONE);
                notesAdapter = new NotesAdapter(getContext(),mNotes);
                    recyclerView_notes.setAdapter(notesAdapter);
                    progressBar.setVisibility(View.GONE);


                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}