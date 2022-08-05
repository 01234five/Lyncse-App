package com.lyncseapp.lyncse;

public class RetrieveMessages {

    public String message;
    public String id;
    public Long createdOn;

    public RetrieveMessages(){

    }

    public RetrieveMessages(String message,String id,Long createdOn){
        this.message = message;
        this.id = id;
        this.createdOn=createdOn;

    }
}
