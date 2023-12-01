package Min.app.plus.utils;

import org.json.JSONObject;

/**
 * 作者：daboluo on 2023/8/23 22:37
 * Email:daboluo719@gmail.com
 */
public class MyEvent {
    private String type;
    private String message;

    public MyEvent(String type,String message) {
        this.type=type;
        this.message = message;
    }
    public String getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }
}
