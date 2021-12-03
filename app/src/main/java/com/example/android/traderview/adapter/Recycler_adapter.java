package com.example.android.traderview.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.traderview.activities.MainActivity;
import com.example.android.traderview.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Recycler_adapter extends RecyclerView.Adapter<Recycler_adapter.viewholder> {



    private static final String TAG = "Recycler_adapter";
    HashMap<String,String> datalist;                //stores the prices of the stocks
    String stock_names[];                           //stores the combined form of name and symbol of stock as they are the key of hashmaps so cannot be directly accessed
    String name_symbol[]=new String[2];             //stores the name and the symbol of the stock individually
    String price_change[]=new String[2];            //stroes the current price and the percent change of the stock
    Context context;
    ArrayList<String> displaypos;
    HashMap<String,Integer> faveditem=new HashMap<>();
    MainActivity mainActivity=new MainActivity();
//    DatabaseReference dbreference= FirebaseDatabase.getInstance().getReference().child("Users").child(mainActivity.theUserData());
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    HashSet<String> watchlist;
    DecimalFormat decimalFormat= new DecimalFormat("#.##");     //used for showing only two decimal places of the stock prices and change

    public Recycler_adapter(HashMap<String,String> datalist, Context context, ArrayList<String> positions ) {
        this.datalist = datalist;
        this.context = context;
        stock_names =new String[datalist.size()];
        this.displaypos =positions;
        sharedPreferences= this.context.getSharedPreferences(MainActivity.email,this.context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        String list[]=sharedPreferences.getString("stocks","").split(">");
        watchlist= new HashSet<>(Arrays.asList(list));

        datalist.keySet().toArray(stock_names);

    }

    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_card,parent,false);
        return new viewholder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(Recycler_adapter.viewholder holder, int position) {


        String stock_names_and_symbol= displaypos.get(position);


        name_symbol = stock_names_and_symbol.split(">");

        price_change =datalist.get(stock_names_and_symbol).split(">");



        holder.name.setText(name_symbol[0]);
        holder.symbol.setText(name_symbol[1]);
        if(!watchlist.contains(holder.symbol.getText().toString())){
            holder.fav.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_star_border_24));
        }else{
            holder.fav.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_star_24));
        }
        holder.price.setText("$"+decimalFormat.format(Double.parseDouble(price_change[0])));
        holder.per_change.setText(decimalFormat.format(Double.parseDouble(price_change[1]))+"%");
        if(price_change[1].charAt(0)=='-') {
            holder.per_change.setTextColor(context.getColor(R.color.red));
        }else{
            holder.per_change.setTextColor(context.getColor(R.color.green));
        }

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{

        TextView name;
        TextView symbol;
        TextView price;
        TextView per_change;
        ImageView fav;



        public viewholder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.stockName);
            symbol=itemView.findViewById(R.id.symbol);
            price =itemView.findViewById(R.id.stockPrice);
            per_change=itemView.findViewById(R.id.perChange);
            fav=itemView.findViewById(R.id.favorite);

            fav.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {

if(watchlist.contains(symbol.getText().toString())){
    fav.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_star_border_24));
    watchlist.remove(symbol.getText().toString());
    String stocks="";
    for(String stoc:watchlist){
        stocks+=stoc+">";
    }
    editor.putString("stocks",stocks);
    editor.apply();
}
else{
    fav.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_star_24));
    editor.putString("stocks",sharedPreferences.getString("stocks","")+symbol.getText().toString()+">");
    editor.apply();
    watchlist.add(symbol.getText().toString());
}
                   //                    Dbdata dbdata=new Dbdata(name.getText().toString(),email.getText().toString().trim(),symbol.getText().toString());
//                    Toast.makeText(context, dbdata.getName(), Toast.LENGTH_SHORT).show();


//                    dbreference.push().setValue(dbdata);



                }
            });


        }

    }
}
