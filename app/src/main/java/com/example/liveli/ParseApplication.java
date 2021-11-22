package com.example.liveli;

import android.app.Application;

import com.example.liveli.parseobjects.UserProfile;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(UserProfile.class);
        //ParseObject.registerSubclass(UserImgs.class);
        //ParseObject.registerSubclass(Likes.class);

        //Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("m39zIghUVC1hIMRqiaBFA7TuOM38edvXfnrQhpT6")
                .clientKey("MiV7hpRgOEu0LNPjJL8KXYtty9Lhxwn1YHYOQcDn")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
