package com.example.android.traderview.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.traderview.R;
import com.example.android.traderview.activities.MainActivity;
import com.example.android.traderview.activities.login;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class profile extends Fragment {
    private static final String TAG = "profile";
    List<String> profile=new ArrayList<>();
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            Toast.makeText(getContext(), "ORIENTATION CHANGE", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "ORIENTATION CHANGE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity=new MainActivity();

        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        TextView name= view.findViewById(R.id.nametext);
        TextView email= view.findViewById(R.id.emailtext);
        SharedPreferences sharedPreferences= getContext().getSharedPreferences(MainActivity.email, Context.MODE_PRIVATE);
        name.setText(sharedPreferences.getString("name","user_name"));
        email.setText(sharedPreferences.getString("email","email"));



        Button signout=view.findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), login.class));
            }
        });

        return view;
    }
}