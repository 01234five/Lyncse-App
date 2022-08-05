package com.lyncseapp.lyncse;

public class User {

    public String firstName,lastName, age, email,uriProfile;

    public User(){

    }

    public User(String firstName,String lastName, String age, String email,String uriProfile){
        this.firstName = firstName;
        this.lastName = lastName;
        this.age= age;
        this.email=email;
        this.uriProfile=uriProfile;
    }


}
