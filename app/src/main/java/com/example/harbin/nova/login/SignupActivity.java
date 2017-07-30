package com.example.harbin.nova.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.harbin.nova.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private EditText et_firstname, et_lastname, et_doctorEmail;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    final Map<String, String> doctorName_map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        et_doctorEmail = (EditText) findViewById(R.id.doctorEmail);
        et_firstname = (EditText) findViewById(R.id.firstname);
        et_lastname = (EditText) findViewById(R.id.lastname);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                mDatabase = FirebaseDatabase.getInstance().getReference();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }


//                try {
                    final String doctorEmail = et_doctorEmail.getText().toString().trim();
                    Query doctorInfoQuery = mDatabase.child("doctors").orderByChild("email").equalTo(doctorEmail);
                    doctorInfoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot == null) {
                                throw new IllegalArgumentException("The Doctor ID is not correct.");
                                //                            return;
                            }
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Doctor doctor = ds.getValue(Doctor.class);
                                doctorName_map.put("key", ds.getKey());
                                doctorName_map.put("firstname", doctor.firstname);
                                doctorName_map.put("lastname", doctor.lastname);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//                }catch (Exception e){
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    return;
//
//                }


                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {


                                    mFirebaseUser = auth.getCurrentUser();
                                    mUserId = mFirebaseUser.getUid();

                                    try{
                                        User user = new User(et_firstname.getText().toString(),
                                                et_lastname.getText().toString(), email);
                                        mDatabase.child("doctors").child(doctorName_map.get("key")).child("userList").child(mUserId).setValue(user);
                                        mDatabase.child("users").child(mUserId).child("doctorEmail").setValue(doctorEmail);
                                        mDatabase.child("users").child(mUserId).child("doctorID").setValue(doctorName_map.get("key"));
                                        mDatabase.child("users").child(mUserId).child("doctorFirstname").setValue(doctorName_map.get("firstname"));
                                        mDatabase.child("users").child(mUserId).child("doctorLastname").setValue(doctorName_map.get("lastname"));
                                        mDatabase.child("users").child(mUserId).child("email").setValue(email);

                                    }catch (Exception e){
                                        Toast.makeText(SignupActivity.this, e.getMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    }




                                    mDatabase.child("users").child(mUserId).child("curtID").setValue(0);
                                    List<String> remindTimes = new ArrayList<String>();
                                    remindTimes.add("7:00");
                                    remindTimes.add("8:00");
                                    remindTimes.add("12:00");
                                    remindTimes.add("13:00");
                                    remindTimes.add("18:00");
                                    remindTimes.add("19:00");
                                    mDatabase.child("users").child(mUserId).child("remindTime").setValue(remindTimes);

                                    mDatabase.child("users").child(mUserId).child("firstname").setValue(et_firstname.getText().toString().trim());
                                    mDatabase.child("users").child(mUserId).child("lastname").setValue(et_lastname.getText().toString().trim());


                                    SharedPreferences prefs = getSharedPreferences("NOVA_data", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("userID", mUserId);
                                    editor.putString("doctorID", doctorName_map.get("key"));
                                    editor.commit();



//                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
