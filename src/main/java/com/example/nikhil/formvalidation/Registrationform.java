package com.example.nikhil.formvalidation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Registrationform extends ProgressDialoge {

    private AutoCompleteTextView city;
    private EditText email, password, ph, university;
    private RadioButton male, female;
    private RadioGroup radioGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static String TAG, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationform);
        initViews();
        TAG = getString(R.string.tag);
        getGender();
    }

    public void initViews() {
//        dbref
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        auth
        mAuth = FirebaseAuth.getInstance();

        // university roll no
        university = (EditText) findViewById(R.id.university);

//        email
        email = (EditText) findViewById(R.id.email);

//        password
        password = (EditText) findViewById(R.id.password);

        // phone

        ph = (EditText) findViewById(R.id.ph);

//        radio buttons
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        male = (RadioButton) findViewById(R.id.male_radio_btn);
        female = (RadioButton) findViewById(R.id.female_radio_btn);

//        city
        city = (AutoCompleteTextView) findViewById(R.id.ac_city);
        city.setThreshold(1);
        String[] cities = getResources().getStringArray(R.array.india_cities);

        ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, cities);
        city.setAdapter(city_adapter);


        if (validateForm()) {
            showProgressDialog();
            mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideProgressDialog();
                            // [END_EXCLUDE]
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    Toast.makeText(Registrationform.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(Registrationform.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "createUserWithEmail:success");
                                dbRegister(task.getResult().getUser());
                                sendVerificationEmail();
                            }
                        }
                    });

        } else {
            Toast.makeText(Registrationform.this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
        }
    }
    public void btnRegister(View v){
        if(validateForm()){
            showProgressDialog();
            mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideProgressDialog();
                            // [END_EXCLUDE]
                            if (!task.isSuccessful()){
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    Toast.makeText(Registrationform.this, "Check your internet connection",Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(Registrationform.this, ""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "createUserWithEmail:success");
                                dbRegister(task.getResult().getUser());
                                sendVerificationEmail();
                            }
                        }
                    });

        }else{
            Toast.makeText(Registrationform.this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
        }
    }
    public void sendVerificationEmail(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    checkEmailDialog();
                }
            }
        });
    }
    private void checkEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Check your Email for verification");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        builder.create().show();
    }

    public void dbRegister(FirebaseUser fuser){
        User user = new User(
                university.getText().toString().trim(),
                usernameFromEmail(email.getText().toString().trim()),
                email.getText().toString().trim(),
                password.getText().toString().trim(),
                ph.getText().toString().trim(),
                gender,
                city.getText().toString().trim(),
                getRegToken()
        );
        Log.d(TAG, ""+user);
        mDatabase.child(getString(R.string.DB_Users))
                .child(fuser.getUid())
                .setValue(user);

    }
    public String getRegToken(){
        Log.d(TAG, "getRegToken: before token");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "getRegToken: "+token);
        return token;
    }

    public String getGender(){

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.male_radio_btn:
                        gender = "male";
                        break;
                    case R.id.female_radio_btn:
                        gender = "female";
                        break;
                }

            }
        });
        return gender;
    }
    public boolean validateForm(){
        if(TextUtils.isEmpty(email.getText().toString().trim()) || !email.getText().toString().trim().contains("@") ){
            Log.d(TAG, "validateForm: email");
            return false;
        }

        else if(TextUtils.isEmpty(password.getText().toString().trim())){
            Log.d(TAG, "validateForm: password");
            return false;
        }


        else if(TextUtils.isEmpty(city.getText().toString())){
            Log.d(TAG, "validateForm: city");
            return false;
        }
        else if(!male.isChecked() && !female.isChecked()){
            Log.d(TAG, "validateForm: radiobtn");
            return false;
        }
        else{
            return true;
        }
    }

    public String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }


}



