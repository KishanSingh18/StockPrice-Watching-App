package com.example.android.traderview.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();           //creating an instance of the firebaseAuthentication in our app
        EditText nametxt=findViewById(R.id.nameSE);                     //Reference of the name storing Edit_text from the xml file into the java file
        EditText email=findViewById(R.id.emailid);                      //Reference of the email storing Edit_text from the xml file into the java file
        EditText password=findViewById(R.id.pasword);                   //Reference of the password storing Edit_text from the xml file into the java file
        EditText repass=findViewById(R.id.repasword);                   //Reference of the Re-entered password storing Edit_text from the xml file into the java file
        Button Signup=findViewById(R.id.Signupbutton);                  //Reference of the Sign-up Button from the xml file into the java file
        TextView logihere=findViewById(R.id.loginhere);                 //Reference of the login button from the xml file into the java file
        ProgressBar progressBar=findViewById(R.id.progressBarSU);       //Reference of the progressBar from the xml file into the java file

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_id=email.getText().toString().trim();      //stores the email_id entered by the user
                String pwd= password.getText().toString().trim();       //stores the password entered by the user
                String repwd=repass.getText().toString().trim();        //stores the re_entered password entered by the user
                String name=nametxt.getText().toString().trim();        //stores the name entered by the user
                if(name==""){
                    nametxt.setError("This is a required field");       //checking if the name field is empty and setting error if it is
                    nametxt.requestFocus();
                }
                else if(email_id==""){
                    email.setError("This is a required field");         //checking if the email field is empty and setting error if it is
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email_id).matches()){
                    email.setError("Enter a valid Email Id");               //checking if the email field is invalid and setting error if it is
                    email.requestFocus();
                }
                else if (pwd == "") {
                    password.setError("This is a required field");          //checking if the password field is empty and setting error if it is
                    password.requestFocus();
                }
                else if(pwd.length()<6){
                    password.setError("The password must be atleast 6 characters long");
                    password.setText("");                                   //checking if the password field is invalid and setting error if it is
                    repass.setText("");
                    password.requestFocus();
                }
                else if(!pwd.equals(repwd)){
                    password.setError("The entered passwords do not match");
                    password.setText("");                                   //checking if the password and the re_entered password field are equal and setting error if it is
                    repass.setText("");
                    password.requestFocus();
                }


                else{
                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())     //attempting to create a new user using the email and the password
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            SharedPreferences sharedPreferences= getSharedPreferences(email_id,MODE_PRIVATE);
                            SharedPreferences.Editor editor= sharedPreferences.edit();                             //successfully creating the user and intialising the sharedpreferences with the details of the user
                            editor.putString("email",email_id.trim());
                            editor.putString("name",name);
                            editor.putString("stocks","");
                            editor.apply();

//                            DataUtil dataUtil=new DataUtil();
//                            dataUtil.insert(name.getText().toString().trim(),email.getText().toString().trim(),"");
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));             //Redirecting the user to the  MainActivity
                            overridePendingTransition(R.anim.right_in,R.anim.left_out);                         //transition animation
                            finish();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(signup.this, "Some error has occured"+task.getException().getMessage(), Toast.LENGTH_LONG).show();           //making a toast if some error is occured while making a new user
                        }
                    }
                });
                }
            }
        });



        logihere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this, login.class));        //redirecting the user to the login page if the user already has an account
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                finish();
            }
        });


    }
    public String update_email(String email){
        return email.replace('.',',');
    }
}