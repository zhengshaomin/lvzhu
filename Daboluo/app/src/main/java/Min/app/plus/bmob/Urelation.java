package Min.app.plus.bmob;

import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobObject;

/**
 * @author daboluo
 */
public class Urelation extends BmobObject {

    _User author,object;
    boolean author_new,object_new;

    public _User getAuthor() {
        return author;
    }

    public void setAuthor(_User author) {
        this.author = author;
    }

    public _User getObject() {
        return object;
    }

    public void setObject(_User object) {
        this.object = object;
    }

    public boolean isAuthor_new() {
        return author_new;
    }

    public void setAuthor_new(boolean author_new) {
        this.author_new = author_new;
    }

    public boolean isObject_new() {
        return object_new;
    }

    public void setObject_new(boolean object_new) {
        this.object_new = object_new;
    }
}
