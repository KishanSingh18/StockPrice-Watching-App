package com.example.android.traderview.fragments;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
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
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class home_page extends Fragment {
    private static final String TAG = "MainActivity";

        View view;


        HashMap<String,String> prices;           //stores the combined price and the percent change of the stock corresponding to its combined name and symbol
        HashMap<String,String> stockandsymbol;      //stores the name and the latest closing price of the stock corresponding to its symbol this is used during websockets messages
        ArrayList<String> positions=new ArrayList<>();         //used to store the position of each stock in the recyclerview
        List<String> stocknames;                    //list of all the stock symbols whose data has to be queried



        Handler handler;                                //reference of the handler of the current UI thread
        SwipeRefreshLayout swipeRefreshLayout;          //reference of the swipe refresh layout from xml
        Recycler_adapter recycler_adapter;              //refernce of the recycler adapter
        RecyclerView recycler;                          //reference  fo the recycler view
        OkHttpClient okHttpClient;
        WebSocket webSocket;

    @Override
    public void onStop() {
        super.onStop();
        if(webSocket!=null){
            Log.e(TAG, "onStop: "+"webSocketclosed" );
        webSocket.close(1000,"the fragment has been stopped");}
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {               //handling the orientation change just by showing the toast message(didn't have to time to appropriately handle this
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            Toast.makeText(getContext(), "configchanged", Toast.LENGTH_SHORT).show();
            }
        else{
            LayoutInflater inflater= (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            inflater.inflate(R.layout.fragment_home_page,null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_home_page, container, false);
            //initialising all the components and the datatypes
            prices =new HashMap<>();
            stockandsymbol=new HashMap<>();
            stocknames=new ArrayList<>();


            handler= new Handler();
            swipeRefreshLayout=view.findViewById(R.id.swipeWA);
            recycler=view.findViewById(R.id.recycler2);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));

            okHttpClient= new OkHttpClient();
            //calling the methods to update the ui with the data and update the recycler view
            updateUI();
            updateRvMap();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Request request= new Request.Builder().url("wss://ws.finnhub.io?token=c6ba3oiad3ib9s028ht0").build();
                    Websocketlistener websocketlistener= new Websocketlistener();
                    webSocket=okHttpClient.newWebSocket(request,websocketlistener);             //creating a connection with the finnhub websocket

                }
            },4000);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {           //handling the swipe to refresh functionality  of the app
                    view.findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.pleaserefreshWA).setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    prices.clear();                     //clearing the prices when swipped
                    stocknames.clear();                 //clearing the stock names when swipped
                    //calling the methods to update the ui with the data and update the recycler view
                    updateUI();
                    updateRvMap();
                }
            });
            return view;
        }

        private void updateUI() {                       //adding the symbols of all the stocks whose data has to be queried
            stocknames.add("GOOG");
            stocknames.add("AAPL");
            stocknames.add("FB");
            stocknames.add("MSFT");
            stocknames.add("AMZN");

            getstockdata();             //makes an api call to get a brief data of each stock
        }

        private void getstockdata() {
            RequestQueue queue= Volley.newRequestQueue(getContext());
            String url="https://api.twelvedata.com/quote?symbol=GOOG,AAPL," +
                    "FB,MSFT,AMZN&apikey=02b4c702fc82438e97eceefb56a19371";             //creating a url with the stock names
            JsonObjectRequest request= new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {                   //extracting data if the connection is successful
                            for(int i=0;i<stocknames.size();i++){
                                try {
                                    JSONObject stock= response.getJSONObject(stocknames.get(i));        //this part is self explainatory
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
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "some error has occured", Toast.LENGTH_SHORT).show();      //creating a toast when there is an error
                    Log.e(TAG, "onErrorResponse: this is the problem",error);

                }
            });
            queue.add(request);                 //querying the data from the api
        }


        private void updateRvMap() {

            handler.postDelayed(new Runnable() {     //trying to populate the recycler view with the data after a delay of 4 secs
                @Override
                public void run() {
                    view.findViewById(R.id.progressBar2).setVisibility(View.GONE);
                    if(prices.size()!=0){                                                   //populating the recycler view with the data if available
                        view.findViewById(R.id.recycler2).setVisibility(View.VISIBLE);
                        recycler_adapter= new Recycler_adapter(prices,getContext(),positions);
                        recycler.setAdapter(recycler_adapter);

                        Log.e(TAG, "run: klfhdsjdsnclkjnjdnvojdnvjdbirekjhujbvidjsjfeidkjmokcd" );}
                    else{
                        view.findViewById(R.id.recycler2).setVisibility(View.INVISIBLE);
                        view.findViewById(R.id.pleaserefreshWA).setVisibility(View.VISIBLE);        //setting a request to refresh the data if the data
                                                                                                    // isn't available right now(most prolly due to api restriction

                    }

                }
            },4000);

        }



        public class Websocketlistener extends WebSocketListener {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {


           for(int i=0;i<stocknames.size();i++) {
               webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"" + stocknames.get(i) + "\"" + "}");
           }                        //sending messages to the websocket once the connection is opened



            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {

                //receiving the data from the websocket and parsing the json to get the appropriate data\
                //this function is mostly self explainatory
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


                    handler.post(new Runnable() {       //notifying the recycler view that new data  is available
                                                        // from the UI thread coz the websockets were running on the UI thread
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
        } //function to get the position  where the corresponding stock is shown in the recycler view

    }
