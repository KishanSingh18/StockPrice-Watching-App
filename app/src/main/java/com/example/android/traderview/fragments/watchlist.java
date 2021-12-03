package com.example.android.traderview.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.traderview.R;
import com.example.android.traderview.activities.MainActivity;
import com.example.android.traderview.adapter.Recycler_adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class watchlist extends Fragment {

    private static final String TAG = "watchlist";

    @Override
    public void onStop() {
        super.onStop();
        webSocket.close(1000,"the watchlist websocket is closed because user is on other screen");
    }

    List<String> stocknames=new ArrayList<>();
    HashMap<String,String> prices=new HashMap<>();           //stores the price and the percent change of the stock corresponding to its combined name and symbol
    HashMap<String,String> stockandsymbol=new HashMap<>();
    ArrayList<String> positions=new ArrayList<>();

    Recycler_adapter recycler_adapter;
    RecyclerView recycler;
    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    Handler handler;
    WebSocket webSocket;
    OkHttpClient okHttpClient;

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
        view=inflater.inflate(R.layout.fragment_watchlist, container, false);
        okHttpClient=new OkHttpClient();
        stocknames=new ArrayList<>();
        stockandsymbol=new HashMap<>();
        prices=new HashMap<>();
        recycler=view.findViewById(R.id.recycler2);
        SharedPreferences sharedPreferences= getContext().getSharedPreferences(MainActivity.email, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String stoc[]=sharedPreferences.getString("stocks","").split(">");

        stocknames=Arrays.asList(stoc);
        handler= new Handler();
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout=view.findViewById(R.id.swipeWA);
        getstockdata();
        updateRvMap();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                okhttp3.Request request= new okhttp3.Request.Builder().url("wss://ws.finnhub.io?token=c6ba3oiad3ib9s028ht0").build();
                Websocketlistener websocketlistener= new Websocketlistener();
                    webSocket=okHttpClient.newWebSocket(request,websocketlistener);
            }
        },4000);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                view.findViewById(R.id.progressBar2b).setVisibility(View.VISIBLE);
                view.findViewById(R.id.pleaserefreshWA).setVisibility(View.INVISIBLE);

                prices.clear();

                positions.clear();
                getstockdata();
                updateRvMap();
                Log.e(TAG, "onRefresh: "+"updated" );
            }
        });







        return view;
    }

    private void getstockdata() {
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        String url="https://api.twelvedata.com/quote?symbol=";
        for(int i=0;i<stocknames.size();i++){
            url+=stocknames.get(i)+",";
        }url+="&apikey=02b4c702fc82438e97eceefb56a19371";

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse: request received" );
                for(int i=0;i<stocknames.size();i++){
                    try {
                        JSONObject stock= response.getJSONObject(stocknames.get(i));
                        String stock_name= stock.getString("name");
                        String stock_symbol=stock.getString("symbol");
                        String price=stock.getString("close");
                        String previous_close=stock.getString("previous_close");
                        String per_change=stock.getString("percent_change");
                        stockandsymbol.put(stock_symbol,stock_name+">"+previous_close);
                        prices.put(stock_name+">"+stock_symbol,price+">"+per_change);
                        positions.add(stock_name+">"+stock_symbol);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void updateRvMap() {


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.progressBar2b).setVisibility(View.GONE);
                if(prices.size()!=0){
                    view.findViewById(R.id.recycler2).setVisibility(View.VISIBLE);
                    recycler_adapter= new Recycler_adapter(prices,getContext(),positions);
                    recycler.setAdapter(recycler_adapter);

                    Log.e(TAG, "run: watchlist" );}
                else{
                    view.findViewById(R.id.recycler2).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.pleaserefreshWA).setVisibility(View.VISIBLE);

                }

            }
        },4000);

    }

    public class Websocketlistener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {


            for(int i=0;i<stocknames.size();i++) {
                webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"" + stocknames.get(i) + "\"" + "}");
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