package m.incrementrestservice.poulouloureminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import m.incrementrestservice.poulouloureminder.ui.chatFragment;
import m.incrementrestservice.poulouloureminder.ui.HomeFragment;
import m.incrementrestservice.poulouloureminder.ui.notesFragment;
import m.incrementrestservice.poulouloureminder.ui.profileFragment;
import m.incrementrestservice.poulouloureminder.ui.SendFragment;
import m.incrementrestservice.poulouloureminder.ui.ShareFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
      Toolbar toolbar;

    private  TextView Email, userName;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;

    private boolean userPressedAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headview = navigationView.getHeaderView(0);

        Email = headview.findViewById(R.id.email_header);
        userName =headview.findViewById(R.id.username_header);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               Email.setText(dataSnapshot.child("email").getValue().toString());
                userName.setText(dataSnapshot.child("username").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(savedInstanceState==null){

            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            toolbar.setTitle("Home");

        }

    }

    private void  Status(String status){
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        mDatabase.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status("Offline");
    }

    public void onBackPressed()
    {
        if(!userPressedAgain){
            Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new HomeFragment()).commit();
                toolbar.setTitle("Home");
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new profileFragment()).commit();
                toolbar.setTitle("Profil");
                break;

            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new chatFragment()).commit();
                toolbar.setTitle("Chats");
                break;

            case R.id.nav_notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new notesFragment()).commit();
                toolbar.setTitle("Notes");
                break;

            case R.id.nav_send:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new SendFragment()).commit();
                toolbar.setTitle("Send");
                break;

            case R.id.nav_share:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new ShareFragment()).commit();
                toolbar.setTitle("Share");
                break;

            case R.id.nav_exit:

                AlertDialog.Builder alerteDialog = new AlertDialog.Builder(this);
                alerteDialog.setTitle("Sign out !!");
                alerteDialog.setIcon(R.drawable.ic_exit_to_app_fox_24dp);
                alerteDialog.setMessage("would you like to deconnecte ?");
                alerteDialog.setCancelable(false);
                alerteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, login.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                });
                alerteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(MainActivity.this, "Undo", Toast.LENGTH_LONG).show();


                    }
                });

                AlertDialog alertDialogfinal = alerteDialog.create();
                alertDialogfinal.show();

                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}