package com.example.android.traderview.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.traderview.R;
import com.example.android.traderview.fragments.home_page;
import com.example.android.traderview.fragments.profile;
import com.example.android.traderview.fragments.search;
import com.example.android.traderview.fragments.watchlist;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

//    private static final String TAG = "MainActivity";
//
//    HashMap<String,String> oldprices;           //stores the so as to say the stale price and the percent change of the stock corresponding to its combined name and symbol
//    HashMap<String,String> stockandsymbol;      //stores the name and the latest closing price of the stock corresponding to its symbol this is used during websockets messages
//    HashMap<String,String> latestprices;        //stores the latest price and the percent change of the stock corresponding to its combined name and symbol
//    ArrayList<String> positions=new ArrayList<>();         //used to store the position of each stock in the recyclerview
//    List<String> stocknames;                    //list of all the stock symbols whose data has to be queried
//
//    Boolean flag_websockets=false;
//
//    Handler handler;
//    SwipeRefreshLayout swipeRefreshLayout;
//    Recycler_adapter recycler_adapter;
//    RecyclerView recycler;
//
//    OkHttpClient okHttpClient;

    FirebaseUser firebaseUser;
    public static String email="";




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, signup.class));             //this is the launcher activity and it checks if the user is logged in already
                                                                                                //and if the user is not logged in they are redirected to the signup page
            finish();
        }
        else{
            email=FirebaseAuth.getInstance().getCurrentUser().getEmail();                       //getting the email of  the logged in user
        }



        BottomNavigationView bottomNavigationView=findViewById(R.id.nav_bar);                   //reference of  the bottom navigation bar from the xml file into the java file
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new home_page()).commit();        //this will show the home_page as the default open fragment when the user starts the mainActivity

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override                                                                               //setting a selector listener on the bottom navigation bar and opening the respective fragment
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selected=null;
                switch (item.getItemId()){
                    case R.id.home:
                        selected= new home_page();
                        break;
                    case R.id.profile:
                        selected= new profile();
                        break;
                    case R.id.watchlist:
                        selected= new watchlist();
                        break;
                    case R.id.search:
                        selected=new search();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container,selected).commit();
                overridePendingTransition(R.anim.left_out,R.anim.right_in);

                return true;
            }
        });
    }




}