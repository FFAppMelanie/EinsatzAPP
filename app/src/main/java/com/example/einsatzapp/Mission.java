package com.example.einsatzapp;

public class Mission {
    String Name;
    String Key;
    double X;
    double Y;
    public Mission(String name,double x, double y){
        Name=name;
        X=x;
        Y=y;
    }

    public String getAdress(){
        return Name;
    }
    public void setAdress(String vehicle){
        this.Name = vehicle;
    }
    public String getKey(){
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public double getX(){
        return X;
    }
    public void moveXY(double x, double y) {this.X=x; this.Y=y;}
    public double getY(){
        return Y;
    }


}
