package com.example.im.googlesign;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Im on 04-11-2017.
 */

public class contact  {
    String email,name,date,app;
    public  void setName(String name)
    {
        this.name=name;
    }
    public  void setEmail(String email)
    {
        this.email=email;
    }
    public  void setDate(String date)
    {
        this.date=date;
    }
    public  void setApp(String app)
    {
        this.app=app;
    }

public String getName()
{
    return this.name;
}
public String getEmail() {
    return this.email;
}
public String getDate()
    {
        return this.date;
    }
    public String getApp()
    {
        return this.app;
    }

}
