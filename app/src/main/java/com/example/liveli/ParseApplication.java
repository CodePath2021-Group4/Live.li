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
                .applicationId(BuildConfig.API_KEY)
                .clientKey(BuildConfig.CLIENT_KEY)
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
