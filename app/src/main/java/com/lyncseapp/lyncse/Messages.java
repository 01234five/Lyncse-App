package com.lyncseapp.lyncse;

import java.util.Map;

public class Messages {

    public String message;
    public String id;
    public String idTo;
    public Map createdOn;

    public Messages(){

    }

    public Messages(String message,String id,String idTo,Map createdOn){
        this.message = message;
        this.id = id;
        this.idTo = idTo;
        this.createdOn=createdOn;

    }
}
