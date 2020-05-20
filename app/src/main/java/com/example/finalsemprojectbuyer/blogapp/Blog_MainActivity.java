package com.example.finalsemprojectbuyer.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalsemprojectbuyer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Blog_MainActivity extends AppCompatActivity
{

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    private Blog_HomeFragment homeFragment;
    private Blog_NotificationFragment notificationFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_blog_activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        ref_listnere ref = new ref_listnere()
        {
            @Override
            public void isOrderToRefresh(boolean isRefresh)
            {

            }
        };
        getSupportActionBar().setTitle("Photo Blog");

        if (mAuth.getCurrentUser() != null)
        {

            mainbottomNav = findViewById(R.id.mainBottomNav);

            // FRAGMENTS
            homeFragment = new Blog_HomeFragment();
            notificationFragment = new Blog_NotificationFragment();

            initializeFragment();

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item)
                {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId())
                    {

                        case R.id.bottom_action_home:
                            homeFragment.loadPost();
                            replaceFragment(homeFragment, currentFragment);
                            return true;
                        case R.id.bottom_action_notif:
                            replaceFragment(notificationFragment, currentFragment);
                            return true;
                        default:
                            return false;
                    }

                }
            });


            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent newPostIntent = new Intent(Blog_MainActivity.this, Blog_NewPostActivity.class);
                    startActivity(newPostIntent);
                }
            });

        }


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
        {
            sendToLogin();
        } else
        {

            current_user_id = mAuth.getCurrentUser().getUid();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("user").child(firebaseAuth.getUid());
            databaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Blog_UserProfile userProfile = dataSnapshot.getValue(Blog_UserProfile.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    Toast.makeText(getApplicationContext(), "Check your Int", Toast.LENGTH_SHORT).show();
                }
            });
//            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
//            {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task)
//                {
//
//                    if (task.isSuccessful())
//                    {
//
//                        if (!task.getResult().exists())
//                        {
//
//                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
//                            startActivity(setupIntent);
//                            finish();
//
//                        }
//
//                    } else
//                    {
//
//                        String errorMessage = task.getException().getMessage();
//                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
//
//
//                    }
//
//                }
//            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.blog_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.action_logout_btn:
                logOut();
                return true;
            default:
                return false;
        }

    }

    private void logOut()
    {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin()
    {


    }

    private void initializeFragment()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment)
    {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment == homeFragment)
        {
            fragmentTransaction.hide(notificationFragment);
        }
        if (fragment == notificationFragment)
        {
            fragmentTransaction.hide(homeFragment);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();

    }

}