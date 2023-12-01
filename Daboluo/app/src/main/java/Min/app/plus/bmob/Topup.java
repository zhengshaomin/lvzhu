package Min.app.plus.bmob;

import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobObject;

/**
 * @author daboluo
 */
public class Topup extends BmobObject {
    _User author;
    double value;
    String State,type;//状态

    public _User getAuthor() {
        return author;
    }

    public void setAuthor(_User author) {
        this.author = author;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
