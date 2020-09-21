package com.e.whatsapp.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupChatActivity extends AppCompatActivity {

    @BindView(R.id.group_chat_text_display)
    TextView mChatTextDisplay;
    @BindView(R.id.groupChat_scrollView)
    ScrollView mChatScrollView;
    @BindView(R.id.group_chat_bar_layout)
    Toolbar mMainAppBar;
    @BindView(R.id.input_group_message)
    EditText mInputMsg;
    @BindView(R.id.img_send_group_chat)
    ImageButton mImgSendMsg;
    long maxed = 0;
    // instance Variables
    private FirebaseAuth mAuth;
    private String groupName, currentUserID, currentUserName, currentDate, currentTime, getMessage;
    private DatabaseReference mUserRef, mGroupNameRef, mGroupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        ButterKnife.bind(this);
        setSupportActionBar(mMainAppBar);
        groupName = getIntent().getStringExtra("groupName");
        getSupportActionBar().setTitle(groupName + " " + "Group");
        initialize();
        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    displayMessage(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    displayMessage(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        mChatScrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mGroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);
        mImgSendMsg.setOnClickListener(view -> {
            saveMessageInfoInDatabase();
            mInputMsg.setText("");
            mChatScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        });
    }

    private void getUserInfo() {
        mUserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserName = snapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void saveMessageInfoInDatabase() {

        String message = mInputMsg.getText().toString().trim();
        String messageKey = mGroupNameRef.push().getKey();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Message Is Empty!", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            mGroupNameRef.updateChildren(groupMessageKey);
            mGroupMessageKeyRef = mGroupNameRef.child(messageKey);
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            mGroupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    private void displayMessage(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();
        while (iterator.hasNext()) {

            String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot) iterator.next()).getValue();
            mChatTextDisplay.append(chatName + ":\n" + chatMessage + "\n" + chatTime + " : " + chatDate + "\n\n\n");
            mChatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}