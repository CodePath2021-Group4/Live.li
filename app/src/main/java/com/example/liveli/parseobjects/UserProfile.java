package com.example.liveli.parseobjects;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserProfile")
public class UserProfile extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "profile_image";

    public ParseUser getUser(){ return getParseUser(KEY_USER); }
    public void setUser(ParseUser user){ put(KEY_USER, user); }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }
}
