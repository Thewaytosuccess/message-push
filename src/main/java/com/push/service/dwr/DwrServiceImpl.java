package com.push.service.dwr;

import com.push.constant.ConstantPool;
import com.push.entity.Message;
import com.push.config.dwr.DwrScriptSessionManager;
import com.push.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.directwebremoting.*;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Objects;

/**
 * 消息推送：kafka + dwr
 * expose service
 * @author xhzy
 */
@Service
@RemoteProxy
@Slf4j
public class DwrServiceImpl implements MessageService {

    @RemoteMethod
    public void onRefresh() {
        try {
            //获取当前的ScriptSession
            WebContext webContext = WebContextFactory.get();
            ScriptSession scriptSession = webContext.getScriptSession();
            HttpSession httpSession = webContext.getSession();

            if (scriptSession != null) {
                if(Objects.nonNull(httpSession) && Objects.nonNull(httpSession.getAttribute(ConstantPool.USER_ID))){
                    log.info("register from httpSession");
                    //刷新页面过程中，需要将userKey从httpSession中复制到scriptSession中
                    scriptSession.setAttribute(ConstantPool.USER_ID, httpSession.getAttribute(ConstantPool.USER_ID));
                }
            }
            new DwrScriptSessionManager().init(String.valueOf(httpSession.getAttribute(ConstantPool.USER_ID)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void push(Message msg){
        final String text = msg.getBody();
        final long receiverId = msg.getReceiverId();

        //message push
        try{
            Browser.withAllSessionsFiltered(session -> Objects.nonNull(session.getAttribute(ConstantPool.USER_ID)),  () -> {
                final ScriptBuffer script = new ScriptBuffer();
                //设定前台接收消息的方法和参数  在前台js里定义getMessage (data) 的方法，就会自动被调用
                script.appendCall("getMessage", text);
                Collection<ScriptSession> sessions = Browser.getTargetSessions();
                for (ScriptSession scriptSession : sessions) {
                    Object userId = scriptSession.getAttribute(ConstantPool.USER_ID);
                    if(Objects.nonNull(userId) && receiverId == Long.parseLong(String.valueOf(userId))){
                        scriptSession.addScript(script);
                        log.info("向客户端推送消息："+ text);
                    }
                }
            });
        }catch (Exception e){
            log.error("没有客户端建立连接。。。error={}",e.getMessage());
        }
    }


}
