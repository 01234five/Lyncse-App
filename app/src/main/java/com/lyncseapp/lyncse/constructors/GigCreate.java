package com.lyncseapp.lyncse.constructors;


import java.util.Map;

public class GigCreate {

    public String title;
    public String esearch;
    public String info;
    public String userID;
    public String uriBanner;
    public Map createdOn;

    public GigCreate(){

    }

    public GigCreate(String title,String esearch, String info, String userID, String uriBanner, Map createdOn){
        this.title = title;
        this.info = info;
        this.userID = userID;
        this.uriBanner = uriBanner;
        this.createdOn = createdOn;


    }

}
