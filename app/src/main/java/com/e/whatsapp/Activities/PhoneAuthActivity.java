package com.e.whatsapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.e.whatsapp.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneAuthActivity extends AppCompatActivity {

    @BindView(R.id.ccp_phone_auth)
    CountryCodePicker mCCPPhoneAuth;
    @BindView(R.id.et_phone_num)
    EditText mETPhoneNum;
    @BindView(R.id.btn_send_code)
    Button mBTNSendCode;
    @BindView(R.id.rl_phone_number)
    RelativeLayout mRLPhoneNumber;
    @BindView(R.id.et_code)
    EditText mETCode;


    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String mVerifyID, phoneNum ="", checker = "";
    private PhoneAuthProvider.ForceResendingToken mRecentToke;
    private static String TAG = "PhoneAuthActivity";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mCCPPhoneAuth.registerCarrierNumberEditText(mETPhoneNum);
        initialize();
    }

    private void initialize() {
/*        mCCPPhoneAuth.setOnCountryChangeListener(selectedCountry -> {
            Toast to = Toast.makeText(this, selectedCountry.getName() + " Country Code: +" + selectedCountry.getPhoneCode(), Toast.LENGTH_SHORT);
            to.setGravity(Gravity.CENTER_VERTICAL, 0, 250);
            to.show();
        });*/
        mBTNSendCode.setOnClickListener(view -> {
            if (mBTNSendCode.equals("submit") || checker.equals("sent code")) {
                String codeCheck = mETCode.getText().toString();
                if (codeCheck.equals("")) {
                    Toast.makeText(this, "Is Required", Toast.LENGTH_SHORT).show();
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerifyID, codeCheck);
                    signInWithPhoneAuthCredential(credential);
                }
            } else {
                phoneNum = mCCPPhoneAuth.getFullNumberWithPlus();
                if (!phoneNum.equals("")) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNum,                   // Phone Number to Verify
                            60,                       // Timeout Duration
                            TimeUnit.SECONDS,           // Unit of Timeout
                            PhoneAuthActivity.this,// Activity (for callback binding)
                            mCallback);
                } else {
                    Toast.makeText(PhoneAuthActivity.this, "Phone Number Invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mRLPhoneNumber.setVisibility(View.VISIBLE);
                mETCode.setVisibility(View.GONE);
                mBTNSendCode.setText("Send Code");
                Toast.makeText(PhoneAuthActivity.this, "Verification Failed ", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Logcat Error: " +  e.getLocalizedMessage());
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerifyID = s;
                mRecentToke = forceResendingToken;
                mRLPhoneNumber.setVisibility(View.GONE);
                mBTNSendCode.setText("Submit");
                checker = "sent code";
                mETCode.setVisibility(View.VISIBLE);
                Toast.makeText(PhoneAuthActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                    } else {
                        Toast.makeText(PhoneAuthActivity.this, "Signing Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(PhoneAuthActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }
}