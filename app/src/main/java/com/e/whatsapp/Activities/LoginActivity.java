package com.e.whatsapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.e.whatsapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btnLogin)
    Button mBtnLogin;
    @BindView(R.id.tv_forget_password_login)
    TextView mTvForgetPasswordLogin;
    @BindView(R.id.et_Username_login)
    TextInputEditText mEtUsernameLogin;
    @BindView(R.id.et_password_login)
    TextInputEditText mEtPasswordLogin;
    @BindView(R.id.btn_loginByPhoneNum)
    Button mBtnLoginByPhoneNum;
    @BindView(R.id.tv_NewAccount_login)
    TextView mTvNewAccountLogin;
    // Firebase
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // InitializeFields
        initializeFields();
        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // getCurrentUserId by FirebaseUser it's hold getCurrentUser() function throw FirebaseAuth
        currentUser = mAuth.getCurrentUser();

    }

    private void signInUser() {
        String email = mEtUsernameLogin.getText().toString();
        String password = mEtPasswordLogin.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email Required!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password Required!", Toast.LENGTH_SHORT).show();
        } else {
            // ProgressDialog
            loading.setTitle("Login...");
            loading.setMessage("Plz Wait for Login...");
            loading.setCanceledOnTouchOutside(false); // Make user can't touch outside ProgressDialog to Cancel operation
            loading.show();
            // can user sign-in by signInWithEmailAndPassword() function throw FirebaseAuth with email | password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // when user sign-in Successfully, will open MainActivity
                            sendUserToMainActivity();
                            Toast.makeText(this, "SignIn Success!", Toast.LENGTH_LONG).show();
                        } else {
                            // if user can't to sign-in will show ErrorMessage by task.getException and convert toString()
                            String errorMessage = task.getException().toString();
                            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }
                    });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // this function first thing run when RunActivity, check if user already sign-in will open MainActivity automatically
        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }
    private void initializeFields() {
        mTvNewAccountLogin.setOnClickListener(view -> {
            sendUserToRegisterActivity();
        });

        mBtnLogin.setOnClickListener(view -> {
            signInUser();
        });
        loading = new ProgressDialog(this);
        mBtnLoginByPhoneNum.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        });
    }
    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }
    private void sendUserToRegisterActivity() {
        Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerActivity);
    }
}