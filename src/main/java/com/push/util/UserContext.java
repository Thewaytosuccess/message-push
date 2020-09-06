package com.push.util;

import com.push.entity.User;

/**
 * @author xhzy
 */
public class UserContext {

    private static final ThreadLocal<User> LOCAL = new ThreadLocal<>();

    public static void setUser(User user){
        LOCAL.set(user);
    }

    public static User getUser(){
        return LOCAL.get();
    }

    public static void remove(){
        LOCAL.remove();
    }
}
