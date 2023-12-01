package Min.app.plus.bmob;

import cn.bmob.v3.BmobObject;

/**
 * 作者：daboluo on 2023/8/27 16:46
 * Email:daboluo719@gmail.com
 */
public class ai_log extends BmobObject {
    String messages,role,session;//内容，角色，用户标识

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public ai_log(String messages,String role,String session) {
        this.messages = messages;
        this.role = role;
        this.session=session;
    }
}
