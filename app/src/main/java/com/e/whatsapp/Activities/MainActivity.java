package com.e.whatsapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.e.whatsapp.FragmentsAdapter.TabsAccessorPager;
import com.e.whatsapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

//import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_page_toolbar)
    Toolbar mMainAppBar;
    @BindView(R.id.main_tabs)
    TabLayout myTabLayout;
    @BindView(R.id.main_tabs_pager)
    ViewPager myViewPager;

    // FirebaseUser
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    // fragmentAdapter
    private TabsAccessorPager myTabsAccessorPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mMainAppBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("WhatsApp");

        // Initialize TabsAccessorPager class
        myTabsAccessorPager = new TabsAccessorPager(getSupportFragmentManager());
        // Set TabsAccessorPager in  ViewPager Adapter
        myViewPager.setAdapter(myTabsAccessorPager);
        // Setup TabLayout with ViewPager
        myTabLayout.setupWithViewPager(myViewPager);

        // Instance FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // Get CurrentUser by getCurrentUser() function
        currentUser = mAuth.getCurrentUser();
        // DatabaseReferences
        mRootRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // this function will run at first when RunActivity
        if (currentUser == null) {
            // if user not sign-in will open LoginActivity Automatically
            sendUserToLoginActivity();
        } else {
            // Verify user sign-in if this first time will open SettingActivity, else open MainActivity.
            verifyUserExistence();
        }
    }

    private void verifyUserExistence() {
        // Get CurrentUserId throw FirebaseAuth -> getCurrentUser() function -> getUid()
        String currentUserId = mAuth.getCurrentUser().getUid();
        // DatabaseReferences
        mRootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check Username if exists (User isn't first time, will open MainActivity)
                // if not exists (User is sign-in first time, will open SettingActivity to UpdateUserProfile)
                if (!snapshot.child("name").exists()) {
                    sendUserToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Invoke option_menu Menu
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logout_menu:
                // user Sign-out by signOut() function throw FirebaseAuth, and open LoginActivity
                mAuth.signOut();
                sendUserToLoginActivity();
                Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_menu:
                // Open SettingActivity
                sendUserToSettingActivity();
                break;

            case R.id.find_friend_menu:
                Toast.makeText(this, "Will Development soon...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.createGroup_menu:
                requestCreateGroup();
                break;
        }
        return true;
    }

    private void requestCreateGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("New Group");
        final EditText mGroupNameField = new EditText(MainActivity.this);
        mGroupNameField.setHint("New Group Name");
        builder.setView(mGroupNameField);
        builder.setPositiveButton("Create", (DialogInterface dialogInterface, int i) -> {
            String groupName = mGroupNameField.getText().toString();
            if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(this, "The Group NOT Created, Name is Required ", Toast.LENGTH_LONG).show();
            } else {
                createNewGroup(groupName);
            }
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });
        builder.setCancelable(false);
        builder.setOnCancelListener(dialogInterface -> {
            Toast.makeText(this, "The Group NOT Created!", Toast.LENGTH_SHORT).show();
        });
        builder.show();

    }

    private void createNewGroup(String groupName) {
        mRootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, groupName + " is Created", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivity);
        finish();
    }

    private void sendUserToSettingActivity() {
        Intent settingActivity = new Intent(MainActivity.this, SettingActivity.class);
        settingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingActivity);
        finish();
    }
}