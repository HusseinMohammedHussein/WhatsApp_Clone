package com.e.whatsapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.btnUpdate_profile)
    Button mBtnUpdateProfile;
    @BindView(R.id.et_username_profile)
    EditText mEtUsernameProfile;
    @BindView(R.id.et_status_profile)
    EditText mEtStatusProfile;
    @BindView(R.id.profile_image)
    CircleImageView mProfileImage;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    // Use to hold ID of CurrentUser
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initializeFields();
        retrieveUserInfo();
    }

    private void initializeFields() {
        mBtnUpdateProfile.setOnClickListener(view -> {
            updateSetting();
        });
        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
//        mEtUsernameProfile.setVisibility(View.INVISIBLE);
    }

    private void updateSetting() {
        String usernameProfile = mEtUsernameProfile.getText().toString();
        String statusProfile = mEtStatusProfile.getText().toString();
        // Verify EditText is not Empty
        if (TextUtils.isEmpty(usernameProfile)) {
            Toast.makeText(this, "Username Required!", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(statusProfile)) {
            Toast.makeText(this, "Status Required", Toast.LENGTH_SHORT).show();
        } else {
            // Get Values from EditText and set with key and pass to Database with UserID
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", usernameProfile);
            profileMap.put("status", statusProfile);
            // DatabaseReferences to set Data from HashMap with userId
            mRootRef.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // if user finished UpdateProfile will open MainActivity
                            sendUserToMainActivity();
                            Toast.makeText(this, "Profile has Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            // if not Completed updateProfile will Display ErrorMessage
                            String messageError = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(this, "Error" + messageError, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void retrieveUserInfo() {

        mRootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("name")) && (snapshot.hasChild("image"))) {
                    String retrieveEmail = snapshot.child("name").getValue().toString();
                    String retrieveStatus = snapshot.child("status").getValue().toString();
                    String retrieveImage = snapshot.child("image").getValue().toString();
                    mEtUsernameProfile.setText(retrieveEmail);
                    mEtStatusProfile.setText(retrieveStatus);
                } else if ((snapshot.exists()) && (snapshot.hasChild("name"))) {
                    String retrieveEmail = snapshot.child("name").getValue().toString();
                    String retrieveStatus = snapshot.child("status").getValue().toString();
                    mEtUsernameProfile.setText(retrieveEmail);
                    mEtStatusProfile.setText(retrieveStatus);
                } else {
//                    mEtUsernameProfile.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingActivity.this, "You are Ready to Update Profile Now", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(SettingActivity.this, MainActivity.class);
//        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }


}