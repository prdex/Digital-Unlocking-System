package com.example.digitallock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class User {
    public String name, email, phone;

    public User(){

    }

    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}

