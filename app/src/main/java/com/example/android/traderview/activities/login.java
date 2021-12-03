package com.example.android.traderview.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.traderview.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();              //creating an instance of the firebaseAuthentication in our app
        EditText email=findViewById(R.id.email);                            //Reference of the email storing Edit_text from the xml file into the java file
        EditText pwd=findViewById(R.id.pwd);                                //Reference of the password storing Edit_text from the xml file into the java file
        Button loginbtn=findViewById(R.id.loginbtn);                        //Reference of the login button from the xml file into the java file
        TextView signuphere=findViewById(R.id.signout);                     //Reference of the Sign-up Button from the xml file into the java file
        ProgressBar progressBar=findViewById(R.id.progressBarLO);           //Reference of the progressBar from the xml file into the java file

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString()==""){
                    email.setError("This is a required field");              //checking if the email field is empty and setting error if it is
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    email.setError("Invalid Email Id");                                 //checking if the email field is invalid and setting error if it is
                    email.requestFocus();
                }
                else if(pwd.getText().toString()==""){
                    pwd.setError("This is a required field");                       //checking if the password field is empty and setting error if it is
                    pwd.requestFocus();
                }
                else if(pwd.getText().toString().length()<6){
                    pwd.setError("Invalid password");                                   //checking if the password field is invalid and setting error if it is
                    pwd.requestFocus();
                }
                else{
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),pwd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {           //attempting to sign in using the email and the password
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent= new Intent(login.this, MainActivity.class);
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);                                     //Redirecting the user to the  MainActivity if login is successful
                            overridePendingTransition(R.anim.right_in,R.anim.left_out);
                            finish();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(login.this, "Login error", Toast.LENGTH_SHORT).show();           //making a toast if there is some error

                        }

                    }
                });
                }
            }
        });


signuphere.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(login.this, signup.class));                //redirecting the user to the Sign up page if the user does not already have an account
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
        finish();
    }
});



    }
}