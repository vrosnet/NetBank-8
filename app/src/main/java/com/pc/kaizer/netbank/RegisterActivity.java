package com.pc.kaizer.netbank;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private EditText mFnameView;
    private EditText mLnameView;
    private EditText mEmailView;
    private EditText mMobileView;
    private EditText mAddrView;
    private EditText mAccnoView;
    private CheckBox mCheckbox;
    private EditText mPassView;
    private EditText mConfirmpass;
    private FirebaseAuth mFirebaseAuth;
    private String otp;
    private DatabaseReference mDatabase;
    private ProgressDialog progress;
    private boolean resp =true;
    private boolean resp2 =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFnameView = (EditText) findViewById(R.id.fname);
        mLnameView = (EditText) findViewById(R.id.lname);
        mEmailView = (EditText) findViewById(R.id.newemail);
        mMobileView = (EditText) findViewById(R.id.mobile);
        mAddrView = (EditText) findViewById(R.id.Address);
        mAccnoView = (EditText) findViewById(R.id.Account);
        mPassView = (EditText) findViewById(R.id.password);
        mConfirmpass = (EditText) findViewById(R.id.confirmpass);
        mCheckbox = (CheckBox) findViewById(R.id.checkBox);
        Button LoginButton = (Button) findViewById(R.id.login);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToNextActivity);
            }
        });

        Button RegisterButton = (Button) findViewById(R.id.reg_button);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Register();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void Register() throws Exception {

        Random r = new Random(System.currentTimeMillis());
        otp = String.valueOf((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        final String new_uid = String.valueOf(100000 + r.nextInt(900000));
        final String Fname = mFnameView.getText().toString();
        final String Lname = mLnameView.getText().toString();
        final String Email = mEmailView.getText().toString();
        final String Mobile = mMobileView.getText().toString();
        final String Address = mAddrView.getText().toString();
        final String Accno = mAccnoView.getText().toString();
        final String pass = mPassView.getText().toString();
        final String confirm = mConfirmpass.getText().toString();
        boolean cancel = false;
        View focusView = null;
        mFnameView.setError(null);
        mLnameView.setError(null);
        mEmailView.setError(null);
        mMobileView.setError(null);
        mAddrView.setError(null);
        if (!name_chk(Fname)) {
            cancel = true;
            mFnameView.setError(getString(R.string.invinp));
            focusView = mFnameView;
        }
        if (!name_chk(Lname)) {
            cancel = true;
            mLnameView.setError(getString(R.string.invinp));
            focusView = mLnameView;
        }

        if (!email_chk(Email)) {
            cancel = true;
            mEmailView.setError(getString(R.string.emlerr));
            focusView = mEmailView;
        }

        if (!mob_chk(Mobile)) {
            cancel = true;
            mMobileView.setError(getString(R.string.mobileinv));
            focusView = mMobileView;
        }
        if (!acchk(Accno)) {
            cancel = true;
            mAccnoView.setError(getString(R.string.accerr));
            focusView = mAccnoView;
        }

        if (!addchk(Address)) {
            cancel = true;
            mAddrView.setError(getString(R.string.adderr));
            focusView = mAddrView;
        }
        if(pass_chk(pass))
        {
            cancel = true;
            mPassView.setError("Input field cannot be left empty");
            focusView = mPassView;
        }
        if(pass_chk(confirm))
        {
            cancel = true;
            mConfirmpass.setError("Input field cannot be left empty");
            focusView = mConfirmpass;
        }
        if(pass.length()<7)
        {
            cancel = true;
            mPassView.setError("Password should be alphanumeric and more than 6 characters");
            focusView = mPassView;
        }
        if(!pass.equals(confirm))
        {
            cancel = true;
            mPassView.setError("The passwords do not match");
            focusView = mPassView;
        }
        if (!mCheckbox.isChecked()) {
            cancel = true;
            mCheckbox.setError("check");
        }
        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            if(!isCreated(Accno))
            {
                if(isVerified(Mobile))
                {
                    reg_user(Fname,Lname,Email,Mobile,Address,Accno,new_uid,pass);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "OTP verification failed", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Internet Banking account aldready created", Toast.LENGTH_LONG).show();
            }

        }

    }

    private boolean name_chk(String name) {
        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(name);
        return !(TextUtils.isEmpty(name) || !m.matches() || name.length() == 0);
    }

    private boolean email_chk(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean mob_chk(String mob) {

        return !Pattern.matches("[a-zA-Z]+", mob) && (mob.length() > 0 || mob.length() < 11) && mob.length() == 10;
    }

    private boolean pass_chk(String pass)
    {
        return TextUtils.isEmpty(pass);
    }

    private boolean addchk(String add) {
        return !TextUtils.isEmpty(add) && add.length() > 15;
    }

    private boolean acchk(String accno) {
        return !Pattern.matches("[a-zA-Z]+", accno) && (accno.length() > 0 || accno.length() < 12) && accno.length() == 11;
    }

    private boolean isCreated(String accno)
    {
        mDatabase.child("accounts").child(accno).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    if(dataSnapshot.getValue().toString().equals("pending"))
                    {
                        resp=false;
                    }
                }
                else
                    Toast.makeText(RegisterActivity.this,"No such account number exists",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this,"Database error",Toast.LENGTH_LONG).show();
            }
        });
        return resp;
    }

    private boolean isVerified(String mob)
    {
        otpRequest req;
        req = new otpRequest(mob, otp, getApplicationContext());
        req.execute((Void) null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialoglayout, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        alertDialog.setView(viewInflated);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String OTP = input.getText().toString();
                if (OTP.equals(otp))
                {
                    resp2=true;
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "OTP verification failed", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.show();
        return resp2;
    }

    private void reg_user(String fname, String lname, final String email, String mob, String addr, final String acc_no, final String uid, String pass)
    {
        mFirebaseAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
                            DB.child("accounts").child(acc_no).child("status").setValue("created");
                            DB.child("users").child(uid).child("account_no").setValue(acc_no);
                            DB.child("users").child(uid).child("email").setValue(email);
                            Toast.makeText(getApplicationContext(), "Creation success", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Creation failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
