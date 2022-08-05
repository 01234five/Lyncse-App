package com.lyncseapp.lyncse;

public class Gigs {

    public String title;
    public String info;
    public String userID;
    public String uriBanner;
    public Long createdOn;

    public Gigs(){

    }

    public Gigs(String title,String info,String userID,String uriBanner,Long createdOn){
        this.title = title;
        this.info = info;
        this.userID = userID;
        this.uriBanner = uriBanner;
        this.createdOn = createdOn;


    }

}
