package Min.app.plus.bmob;

import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobObject;

public class Apply extends BmobObject {
    _User user;//用户
    int boluo,rmb;//菠萝，实际到账金额
    String state,alipay,alipayname;//提现状态，支付宝账号，姓名

    public _User getUser() {
        return user;
    }

    public void setUser(_User user) {
        this.user = user;
    }

    public int getBoluo() {
        return boluo;
    }

    public void setBoluo(int boluo) {
        this.boluo = boluo;
    }

    public int getRmb() {
        return rmb;
    }

    public void setRmb(int rmb) {
        this.rmb = rmb;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getAlipayname() {
        return alipayname;
    }

    public void setAlipayname(String alipayname) {
        this.alipayname = alipayname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
