package com.example.android.traderview.data;

import com.example.android.traderview.Dbdata;
import com.example.android.traderview.activities.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class DataUtil {
    DatabaseReference dbref=FirebaseDatabase.getInstance().getReference().child("Users");
    MainActivity mainActivity=new MainActivity();
//    DatabaseReference currentuser= dbref.child(mainActivity.theUserData());
    String user_name;
    String email;
    public ArrayList<String> profildata=new ArrayList<>();

    public void insert(String name,String email,String Stocks){

        Dbdata dbdata=new Dbdata(name,email,Stocks);
//        currentuser.setValue(dbdata);
    }
    public void update(String watchedstocks){
        String watchliststocks=new String(watchedstocks);
        HashMap updatedstocklist=new HashMap();
        updatedstocklist.put("stocks",watchliststocks);
//        currentuser.updateChildren(updatedstocklist);

    }

}
