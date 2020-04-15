package m.incrementrestservice.poulouloureminder.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import m.incrementrestservice.poulouloureminder.R;
import m.incrementrestservice.poulouloureminder.model.User;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class profileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private ProgressBar progressbar;
    String userId;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView emailProfile = root.findViewById(R.id.Email_profile);
        final TextView usernameProfile = root.findViewById(R.id.username_profile);
        final  Button confirm = root.findViewById(R.id.confirmSets);

        final TextView txtNewUsername = root.findViewById(R.id.setUsername_profil);
        final TextView txtNewPassword = root.findViewById(R.id.setPassword_profile);
        final TextView txtNewPassword2 = root.findViewById(R.id.setPassword_profile_cofirmation);
        final TextView txtOldPassword = root.findViewById(R.id.oldPassword_profile);

        progressbar = root.findViewById(R.id.progressBarProfile);
        progressbar.setVisibility(VISIBLE);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                emailProfile.setText(dataSnapshot.child("email").getValue().toString());
                usernameProfile.setText(dataSnapshot.child("username").getValue().toString());
                progressbar.setVisibility(GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String newUsername= txtNewUsername.getText().toString();
                final String newPassword = txtNewPassword.getText().toString().trim();
                final String newPassword2 = txtNewPassword2.getText().toString().trim();
                final String oldPassword = txtOldPassword.getText().toString().trim();

                if(firebaseUser!=null && firebaseUser.getEmail()!= null && !oldPassword.isEmpty()){
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),oldPassword);
                    progressbar.setVisibility(VISIBLE);


                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                if(!TextUtils.isEmpty(newUsername) && newUsername.length()>3){

                                    User user = new User(userId,newUsername,firebaseUser.getEmail(),"default", "Offline", newUsername.toLowerCase());
                                    mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressbar.setVisibility(GONE);


                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Username is update",Toast.LENGTH_LONG).show();
                                                return;

                                            }else{
                                                Toast.makeText(getActivity(), task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });

                                }
                                if(!TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(newPassword2) && !TextUtils.isEmpty(oldPassword) ) {
                                    if (newPassword.equals(newPassword2) && newPassword.length()>=6) {
                                        progressbar.setVisibility(GONE);


                                        firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getActivity(), "password is update", Toast.LENGTH_LONG).show();


                                                }else{
                                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getActivity(), "The password do not correspond or is too short", Toast.LENGTH_LONG).show();
                                        return;

                                    }
                                }else{
                                    if(newUsername.isEmpty()) {
                                        progressbar.setVisibility(GONE);
                                        Toast.makeText(getActivity(), "Please enter all the fields", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                }

                            }else{
                                progressbar.setVisibility(GONE);
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }else{
                    progressbar.setVisibility(GONE);
                    Toast.makeText(getActivity(), " Please make sure that all the specifics fields are filled out properly", Toast.LENGTH_LONG).show();
                    return;


                }



            }
        });



        return root;
    }
}