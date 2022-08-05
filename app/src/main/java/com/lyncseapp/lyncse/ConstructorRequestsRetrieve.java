package com.lyncseapp.lyncse;

public class ConstructorRequestsRetrieve {

    public String sId;
    public String euId;
    public String gigId;
    public String status;
    public Long createdOn;
    public String euTimeZoneUTC;
    public String title;
    public String price;
    public String uriGig;
    public String creatorName;
    public String uriCreator;
    public String euName;
    public String uriEu;
    public String gigType;
    public ConstructorRequestsRetrieve(){

    }

    public ConstructorRequestsRetrieve(String sId,String euId,String gigId,String gigType,String status, Long createdOn,String euTimeZoneUTC,String title,String price,String uriGig,String creatorName,String uriCreator,String euName,String uriEu){
        this.sId = sId;
        this.euId=euId;
        this.gigId=gigId;
        this.gigType=gigType;
        this.status=status;
        this.createdOn=createdOn;
        this.euTimeZoneUTC=euTimeZoneUTC;
        this.title=title;
        this.price=price;
        this.uriGig=uriGig;
        this.creatorName=creatorName;
        this.uriCreator=uriCreator;
        this.euName=euName;
        this.uriEu=uriEu;



    }
}
