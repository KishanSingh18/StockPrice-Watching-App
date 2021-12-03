package com.example.android.traderview.fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.traderview.R;
import com.example.android.traderview.adapter.Recycler_adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class search extends Fragment {
    private static final String TAG = "search";
    EditText searched_stock;
    ImageView searchbtn;
    RecyclerView recycler;
    Recycler_adapter recycler_adapter;
    ArrayList<String> displayed_stocks=new ArrayList<>();
    HashSet<String> relatedstocks=new HashSet<>();
    ArrayList<String> positions=new ArrayList<>();
    HashMap<String,String> prices=new HashMap<>();           //stores the price and the percent change of the stock corresponding to its combined name and symbol
    HashMap<String,String> stockandsymbol=new HashMap<>();
    View view;
    Handler handler;
    WebSocket webSocket;
    OkHttpClient okHttpClient;

    String stockname;

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

    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        searched_stock = view.findViewById(R.id.searchBAR);
        okHttpClient=new OkHttpClient();

        searchbtn = view.findViewById(R.id.searchbtn);
        recycler = view.findViewById(R.id.recyclerSE);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBarSE);
        handler = new Handler();



        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.kindlyrefreshSE).setVisibility(View.GONE);
                if(relatedstocks.size()!=0){
                    prices.clear();
                    relatedstocks.clear();
                    positions.clear();
                }
//                Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                if (searched_stock.getText().toString().length() == 0) {
                    searched_stock.setError("Good Human!! Don't try to mock me");
                    searched_stock.requestFocus();
                } else {

                    progressBar.setVisibility(View.VISIBLE);
                    if(displayed_stocks.size()>0){
                        displayed_stocks.clear();
                    }
                    Log.e(TAG, "onClick: PRogress bar");
                    stockname = searched_stock.getText().toString().trim();
                    getSymbols(stockname);
                    updateRvMap();
                    Log.e(TAG, "after RVMAP " );
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            okhttp3.Request request= new okhttp3.Request.Builder().url("wss://ws.finnhub.io?token=c6ba3oiad3ib9s028ht0").build();
                            Websocketlisten websocketlistener= new Websocketlisten();
                            webSocket=okHttpClient.newWebSocket(request,websocketlistener);
                        }
                    },7000);

                }

            }
        });


        return view;
    }
//    }https://finnhub.io/api/v1/search?q=apple&token=c6ba3oiad3ib9s028ht0

    public void getSymbols(String name){
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        String url="https://finnhub.io/api/v1/search?q="+name+"&token=c6ba3oiad3ib9s028ht0";
        Log.e("justEnteredgetSymbols", "getSymbols: " );
        Log.e(TAG, "url used in getSymbols: "+url );
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("onResponse", "onResponse: getSymbols" );
                        try {
                            JSONArray dataArray= response.getJSONArray("result");
                            for(int i=0;i<dataArray.length();i++){
                                JSONObject jsonObject= dataArray.getJSONObject(i);
                                String symbol= jsonObject.getString("symbol");
                                relatedstocks.add(symbol);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getStockDetails();
                        Log.e("exiting getSymbols", "onResponse: " );

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: getSymbols "+error.getMessage() );

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    public void getStockDetails(){
        RequestQueue requestQueue1= Volley.newRequestQueue(getContext());
        String[] stocknames=new String[relatedstocks.size()];

        relatedstocks.toArray(stocknames);
        Log.e("justEnteredgetStockd", "getSymbols: " );
        Log.e(TAG, "the stock list getStockDetails: "+stocknames[0]+","+stocknames[1]+","+stocknames[2]+","+stocknames[3] );
        String url="https://api.twelvedata.com/quote?symbol=";
        for(int i=0;i<Math.min(relatedstocks.size(),2);i++){
            url+=stocknames[i]+",";
        }url+="&apikey=02b4c702fc82438e97eceefb56a19371";

        Log.e(TAG, "the url used in getStockDetails: "+url );


        JsonObjectRequest jsonObjectRequest1= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse: getStockDetails response 2" );
                for(int i=0;i<=Math.min(relatedstocks.size(),2);i++){
                    try {
                        JSONObject stock= response.getJSONObject(stocknames[i]);
                        String stock_name= stock.getString("name");
                        String stock_symbol=stock.getString("symbol");
                        String price=stock.getString("close");
                        String previous_close=stock.getString("previous_close");
                        String per_change=stock.getString("percent_change");
                        displayed_stocks.add(stock_symbol);
                        stockandsymbol.put(stock_symbol,stock_name+">"+previous_close);
                        prices.put(stock_name+">"+stock_symbol,price+">"+per_change);
                        positions.add(stock_name+">"+stock_symbol);
//                        Log.e(TAG, "onResponse: "+positions.get(0) );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                Log.e("exiting getStockd", "onResponse: "+prices.get(positions.get(0)) );

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
requestQueue1.add(jsonObjectRequest1);


    }

    private void updateRvMap() {


        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "in handler of updateRvMsp" );
                if(prices.size()!=0){

                    recycler_adapter= new Recycler_adapter(prices,getContext(),positions);
                    recycler.setAdapter(recycler_adapter);

                    Log.e(TAG, "run: watchlist" );}
                else{

                    view.findViewById(R.id.kindlyrefreshSE).setVisibility(View.VISIBLE);

                }

            }
        },7000);

    }

    public class Websocketlisten extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {


            for(int i=0;i<=displayed_stocks.size();i++) {
                Log.e("In websockets", "onOpen: "+displayed_stocks.get(i) );
                webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"" + displayed_stocks.get(i) + "\"" + "}");

                Log.e(TAG, "onOpen: "+ "{\"type\":\"subscribe\",\"symbol\":\"" + displayed_stocks.get(i) + "\"" + "}");
            }



        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.e(TAG, "onMessage: "+text );


            webSocket.send("{\"type\":\"ping\"}");

            JSONObject jsonObject=null;
            try {


                jsonObject = new JSONObject(text);

                JSONArray data_array = jsonObject.getJSONArray("data");
                JSONObject object = data_array.getJSONObject(0);
                String symbol = object.getString("s");
                String price = Double.toString(object.getDouble("p"));
                String nameandlastprice = stockandsymbol.get(symbol);
                String name_price[] = nameandlastprice.split(">");
                Double percent=((object.getDouble("p")-Double.parseDouble(name_price[1]))/Double.parseDouble(name_price[1]))*100;
                String perchange = Double.toString(percent);
                prices.put(name_price[0] + ">" + symbol, price + ">" + perchange);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recycler_adapter.notifyItemChanged(getposition(name_price[0]+">"+symbol));
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Toast.makeText(getContext(),"the websocket connection is closed",Toast.LENGTH_SHORT).show();
        }
    }

    public int getposition(String name_symbol){
        return positions.indexOf(name_symbol);
    }





}