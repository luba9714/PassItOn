package com.example.passiton.Data;

import android.text.Editable;

import com.example.passiton.Data.Book;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String city;
    private String phoneNum;
    private String id;

    public User(String name, String email, String city, String phoneNum, ArrayList<Book> listOfBooks) {
        this.name = name;
        this.email = email;
        this.city = city;
        this.phoneNum = phoneNum;
    }

    public User(String id) {
        this.id=id;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }



    public void setAllDetails(Editable name, Editable email, Editable city, Editable phone) {
        this.setName(name.toString());
        this.setEmail(email.toString());
        this.setCity(city.toString());
        this.setPhoneNum(phone.toString());
    }

    public boolean notEmpty() {
        if(!this.getEmail().equals("") && !this.getCity().equals("") && !this.getName().equals("") && !this.getPhoneNum().equals("")){
            return true;
        }else {
            return false;
        }
    }
}
