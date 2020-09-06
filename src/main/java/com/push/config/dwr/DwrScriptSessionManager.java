package com.push.config.dwr;

import com.push.constant.ConstantPool;
import org.directwebremoting.Container;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;
import org.directwebremoting.extend.ScriptSessionManager;
import org.directwebremoting.servlet.DwrServlet;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author xhzy
 */
public class DwrScriptSessionManager extends DwrServlet {

    public void init(final String value) {
        Container container = ServerContextFactory.get().getContainer();
        ScriptSessionManager manager = container.getBean(ScriptSessionManager.class);
        ScriptSessionListener listener = new ScriptSessionListener() {

            @Override
            public void sessionCreated(ScriptSessionEvent ev) {
                HttpSession httpSession = WebContextFactory.get().getSession();
                String userId = String.valueOf(httpSession.getAttribute(ConstantPool.USER_ID));
                System.out.println("ScriptSession created! userId = "+userId);

                ScriptSession scriptSession = ev.getSession();
                if(Objects.isNull(scriptSession.getAttribute(ConstantPool.USER_ID))){
                    if(StringUtils.isEmpty(userId)){
                        scriptSession.setAttribute(ConstantPool.USER_ID, value);
                        httpSession.setAttribute(ConstantPool.USER_ID,value);
                    }else{
                        scriptSession.setAttribute(ConstantPool.USER_ID,userId);
                    }
                }
            }

            @Override
            public void sessionDestroyed(ScriptSessionEvent ev) {
                ScriptSession session = ev.getSession();
                if(Objects.nonNull(session)){
                    System.out.println("ScriptSession destroyed");
                }
            }
        };
        manager.addScriptSessionListener(listener);

    }

}
