package com.example.android.traderview;

public class Dbdata {
    public String name;
    public String email;
    public String stocks;
    public Dbdata(){}
    public Dbdata(String name,String email, String stocks) {
        this.name = name;
        this.email = email;
        this.stocks=stocks;
    }

   public Dbdata(String stocks){
        this.stocks=stocks;
   }

    public String getStocks() {
        return stocks;
    }

    public void setStocks(String stocks) {
        this.stocks = stocks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
