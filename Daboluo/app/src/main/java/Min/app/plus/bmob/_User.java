package Min.app.plus.bmob;

import android.content.Context;
import android.view.View;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author daboluo
 */
public class _User extends BmobUser {

    String qq;
    String signature,address,birthday,ksh,boluocoin;
    double account;

    Boolean sex,auditor;
    int age;

    public String getBoluocoin() {
        return boluocoin;
    }

    public void setBoluocoin(String boluocoin) {
        this.boluocoin = boluocoin;
    }

    public String getKsh() {
        return ksh;
    }

    public void setKsh(String ksh) {
        this.ksh = ksh;
    }

    public Boolean getAuditor() {
        return auditor;
    }

    public void setAuditor(Boolean auditor) {
        this.auditor = auditor;
    }

    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getQq() {
        return qq;
    }
    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


    public double getAccount() {
        return account;
    }

    public void setAccount(double account) {
        this.account = account;
    }
    

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
