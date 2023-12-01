package Min.app.plus.bmob;

import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobObject;

/**
 * @author daboluo
 */
public class Feed extends BmobObject {
    String content;
    _User author;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public _User getAuthor() {
        return author;
    }

    public void setAuthor(_User author) {
        this.author = author;
    }
}
