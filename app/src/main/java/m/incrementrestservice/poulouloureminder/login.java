package m.incrementrestservice.poulouloureminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button login2,signup;
    private ProgressBar progressBarlogin;
    private TextView forgotPassword;
    private boolean userPressedAgain;
    private EditText txtemail, txtpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        txtemail = (EditText) findViewById(R.id.email_tel);
        txtpassword = (EditText) findViewById(R.id.password2);
        login2 = (Button)findViewById(R.id.login2);
        signup = (Button)findViewById(R.id.signup);
        progressBarlogin = findViewById(R.id.progressBarlogin);
        forgotPassword =findViewById(R.id.forgotPassword);

        mAuth = FirebaseAuth.getInstance();


        login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtemail.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(login.this, "Please enter E-mail and  Password",Toast.LENGTH_LONG).show();
                    return;
                }else{

                    progressBarlogin.setVisibility(View.VISIBLE);


                    mAuth.signInWithEmailAndPassword(   email, password)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBarlogin.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        if(mAuth.getCurrentUser().isEmailVerified()){
                                            startActivity(new Intent(login.this,MainActivity.class));
                                        }else{
                                            Toast.makeText(login.this, "Please verifie you E-mail adress",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(login.this, task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }
            }



        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, sign_up.class));
                finish();

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,fgtPassword.class));
                finish();

            }
        });




    }

    public void onBackPressed()
    {
        if(!userPressedAgain){
            Toast.makeText(login.this, "Press back again to exit", Toast.LENGTH_LONG).show();
            userPressedAgain=true;
        }else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished) {


            }

            @Override
            public void onFinish() {
                userPressedAgain = false;
            }
        }.start();

    }



}
