package com.e.whatsapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.e.whatsapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {


    @BindView(R.id.btn_register)
    Button mBtnRegister;
    @BindView(R.id.et_password_register)
    EditText mEtPasswordRegister;
    @BindView(R.id.et_email_register)
    TextInputEditText mEtUsernameRegister;
    @BindView(R.id.tv_haveAccount)
    TextView mTvHaveAccount;

    // FirebaseAuth to Create New Account
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mRootRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        // InitializeFields
        initializeFields();
        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // DatabaseReferences
        mRootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

    }

    private void initializeFields() {

        mTvHaveAccount.setOnClickListener(view -> {
            sendUserToLoginActivity();
        });
        mBtnRegister.setOnClickListener(view -> {
            createNewAccount();
        });
        loadingBar = new ProgressDialog(this);


    }

    private void createNewAccount() {
        String email = mEtUsernameRegister.getText().toString();
        String password = mEtPasswordRegister.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email Required!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password Required!", Toast.LENGTH_SHORT).show();
        } else {
            // show ProgressDialog
            loadingBar.setTitle("Creating...");
            loadingBar.setMessage("Plz Wait for registering...");
            loadingBar.setCanceledOnTouchOutside(false); // TouchOutside ProgressDialog to Cancel is false
            loadingBar.show();
            // create new user by FirebaseAuth class then createUserWithEmailAndPassword() function, with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // TODO: will use currentUserId with setValue(Data other email | password)
                            // getCurrentUserId
                            String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            // setUserID in Database without Value
                            mRootRef.child("Users").child(currentUserID).setValue("");
                            // Open MainActivity when user is created
                            sendUserToMainActivity();
                            // Show Success Message when user is created
                            Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // if user not create show ErrorMessage by Task
                            String messageError = task.getException().toString();
                            Toast.makeText(RegisterActivity.this, messageError, Toast.LENGTH_LONG).show();
                        }
                        // ProgressDialog is Dismiss
                        loadingBar.dismiss();
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }

    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
        // When User click on Back button will not reopen RegisterActivity again
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
        // When User click on Back button will not reopen LoginActivity again
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivity);
        finish();
    }


}