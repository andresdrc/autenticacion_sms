package com.secuoyasoft.autenticacioncelular;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;



public class MainActivity extends AppCompatActivity {

    EditText phone, otp;
    Button btngenOTP, btnverify;
    FirebaseAuth mAuth;

    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        btngenOTP = findViewById(R.id.btngenerateOTP);
        btnverify = findViewById(R.id.btngverifyOTP);
        mAuth = FirebaseAuth.getInstance();

        btngenOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(phone.getText().toString())){
                    Toast.makeText(MainActivity.this, "Ingrese un numero valido", Toast.LENGTH_SHORT).show();
                }
                else {
                    String number = phone.getText().toString();
                    sendverificationcode(number);
                }

            }

        });

        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(otp.getText().toString())){
                    Toast.makeText(MainActivity.this, "Codigo erroneo ingresado", Toast.LENGTH_SHORT).show();
                }
                verifycode(otp.getText().toString());
            }
        });

    }

   private void verifycode(String code){
    PhoneAuthCredential credential =PhoneAuthProvider.getCredential(verificationId, code);
    signbyCredentials(credential);
    }

    private void signbyCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Ingreso satisfactorio", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void sendverificationcode(String phoneNumber){

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+591" + phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }



    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()

    {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
           final String code = credential.getSmsCode();
           if (code != null){
               verifycode(code);
           }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(MainActivity.this, "Verificacion fallida", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(@NonNull String s,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {

            super.onCodeSent(s, token);

            verificationId = s;

        }
    };


}