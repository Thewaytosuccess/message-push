package com.push.enums;

/**
 * @author xhzy
 */

public enum EmailTypeEnum {
    /**
     * email type
     */
    ATTACHMENT("attachment"),
    IMAGE("image");

    EmailTypeEnum(String type){
        this.type = type;
    }

    private final String type;

    public String getType(){
        return type;
    }
}
