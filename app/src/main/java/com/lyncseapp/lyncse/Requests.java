package com.lyncseapp.lyncse;

import java.util.Map;

public class Requests {


    public String sId;
    public String euId;
    public String gigId;
    public String uStatus;
    public Map createdOn;
    public String euTimeZoneUTC;
    public String title;
    public String price;
    public String duration;
    public String gigTime;
    public String uriGig;
    public String creatorName;
    public String uriCreator;
    public String euName;
    public String uriEu;
    public String gigType;




    public Requests(){

    }

    public Requests(String sId,String euId,String gigId,String gigType,String uStatus, Map createdOn,String euTimeZoneUTC,String title,String price,String duration,String gigTime,String uriGig,String creatorName,String uriCreator, String euName,String uriEu){
        this.sId = sId;
        this.euId=euId;
        this.gigId=gigId;
        this.gigType=gigType;
        this.uStatus=uStatus;
        this.createdOn=createdOn;
        this.euTimeZoneUTC=euTimeZoneUTC;
        this.title=title;
        this.price=price;
        this.gigTime=gigTime;
        this.duration=duration;
        this.uriGig=uriGig;
        this.creatorName=creatorName;
        this.uriCreator=uriCreator;
        this.euName=euName;
        this.uriEu=uriEu;




    }
}
