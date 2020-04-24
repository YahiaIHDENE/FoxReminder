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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import m.incrementrestservice.poulouloureminder.NewRdvActivity;
import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.adapter.RdvAdapter;
import m.incrementrestservice.poulouloureminder.model.Rdv;
import m.incrementrestservice.poulouloureminder.notifications.Token;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView_Rdv;
    private RdvAdapter rdvAdapter;
   // public addDelete listner;

    private List<Rdv> mRdv;
    EditText searchRdv;

    ProgressBar progressBar;

    ImageButton btn_add;
    ImageButton removerdvitem;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.progressBarrdv);
        progressBar.setVisibility(View.VISIBLE);

        btn_add = view.findViewById(R.id.addrdvitem);
        removerdvitem = view.findViewById(R.id.addrdvitem);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewRdvActivity.class));


            }
        });


        recyclerView_Rdv = view.findViewById(R.id.rdvlist);
        recyclerView_Rdv.setHasFixedSize(true);
        recyclerView_Rdv.setLayoutManager(new LinearLayoutManager(getContext()));

        searchRdv =view.findViewById(R.id.search_bar_rdv);
        searchRdv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Search_Rdv(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRdv = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBarrdv);
        progressBar.setVisibility(View.VISIBLE);
        Read_Rdv();
        List<String> deleteList;

       /* if (rdvAdapter.hashMap!=null){
            //deleteList = rdvAdapter.list;
            btn_add.setVisibility(View.INVISIBLE);
            removerdvitem.setVisibility(View.VISIBLE);
        }*/


        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }


    private void  updateToken(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);

    }

    private void Search_Rdv(String s) {

        final  FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Rdv").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRdv.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rdv rdv = snapshot.getValue(Rdv.class);
                    assert rdv != null;
                    assert fUser != null;
                    if (fUser.getUid().equals(rdv.owner)) {
                        mRdv.add(rdv);
                    }
                }

                rdvAdapter = new RdvAdapter(getContext(),mRdv);
                recyclerView_Rdv.setAdapter(rdvAdapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void Read_Rdv() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rdv");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchRdv.getText().toString().equals("")){
                    mRdv.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Rdv rdv = snapshot.getValue(Rdv.class);
                        assert rdv!= null;
                        assert firebaseUser!= null;
                         for (Map.Entry mapentry : rdv.particip.entrySet()) {
                                if(firebaseUser.getUid().equals(mapentry.getValue())){
                                    mRdv.add(rdv);
                                }
                         }

                    }

                    Collections.reverse(mRdv);
                    progressBar.setVisibility(View.GONE);
                    rdvAdapter = new RdvAdapter(getContext(),mRdv);
                    recyclerView_Rdv.setAdapter(rdvAdapter);
                    progressBar.setVisibility(View.GONE);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
/*
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listner = (addDelete) context;
    }

    public interface addDelete{
        void delete(HashMap<String,String> hashMap);
    }

 */

}

