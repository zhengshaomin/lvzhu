package Min.app.plus.bmob;

import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobObject;

public class FreeCarmi extends BmobObject {
    _User user;//使用者
    String carmi;//卡密
    int boluo;//菠萝币
    boolean state;//状态

    public _User getUser() {
        return user;
    }

    public void setUser(_User user) {
        this.user = user;
    }

    public String getCarmi() {
        return carmi;
    }

    public void setCarmi(String carmi) {
        this.carmi = carmi;
    }

    public int getBoluo() {
        return boluo;
    }

    public void setBoluo(int boluo) {
        this.boluo = boluo;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}