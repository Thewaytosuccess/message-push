package com.push.enums;

/**
 * 根据业务类型，查询对应的邮件模板名称
 * @author xhzy
 */
public enum BizTypeEnum {
    /**
     * biz type
     */
    USER_REGISTER(0,"user_register_template"),
    ITEM_PROMOTION(1,"item_promotion_template");

    BizTypeEnum(int type,String templateName){
        this.type = type;
        this.templateName = templateName;
    }

    private final int type;
    private final String templateName;

    public int getType(){ return this.type; }

    public String getTemplateName(){ return this.templateName; }

    public static String getTemplateName(int type){
        for(BizTypeEnum e:BizTypeEnum.values()){
            if(e.getType() == type){
                return e.getTemplateName();
            }
        }
        return null;
    }

}
