package com.push.controller;

import com.push.constant.ConstantPool;
import com.push.entity.User;
import com.push.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author xhzy
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @PostMapping("/login")
    private Long login(User user, HttpServletResponse response, HttpSession session){
        String pony = "pony",jackMa = "jackMa";
        if(pony.equals(user.getUsername())){
            user.setUserId(1);
        }else if(jackMa.equals(user.getUsername())){
            user.setUserId(2);
        }else{
            throw new IllegalArgumentException("param error");
        }
        UserContext.setUser(user);

        //save userId to session
        session.setAttribute(ConstantPool.USER_ID,user.getUserId());

        Cookie cookie = new Cookie(ConstantPool.USER_ID, String.valueOf(user.getUserId()));
        response.addCookie(cookie);
        return user.getUserId();
    }

    @GetMapping("/logout")
    public Boolean logout(HttpSession session){
        session.removeAttribute(ConstantPool.USER_ID);
        UserContext.remove();
        return true;
    }


}
