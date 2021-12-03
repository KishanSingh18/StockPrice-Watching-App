package com.example.android.traderview.adapter;

public class Data {
    public String stock_name;
    String stock_symbol;
    String price;
    String per_change;

    public Data(String stock_name, String stock_symbol, String price, String per_change) {
        this.stock_name = stock_name;
        this.stock_symbol = stock_symbol;
        this.price = price;
        this.per_change = per_change;
    }
}
