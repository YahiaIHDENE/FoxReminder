package m.incrementrestservice.poulouloureminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Chargement extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargement);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null && firebaseUser.isEmailVerified()){
            startActivity(new Intent(Chargement.this,MainActivity.class));
        }else{

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    //demarrer une page
                    Intent intent= new Intent(getApplicationContext(),login.class);
                    startActivity(intent);
                    finish();
                }
            };

            new Handler().postDelayed(runnable,3000);
        }
        }

}
