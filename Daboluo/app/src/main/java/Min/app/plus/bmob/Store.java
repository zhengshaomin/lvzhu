package Min.app.plus.bmob;

import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Store extends BmobObject {

    private String name,information,service_scope,notice_content;
    private _User manager;
    private BmobFile icon;
    private boolean state,notice_state,audit_state;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public _User getManager() {
        return manager;
    }

    public void setManager(_User manager) {
        this.manager = manager;
    }

    public BmobFile getIcon() {
        return icon;
    }

    public void setIcon(BmobFile icon) {
        this.icon = icon;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getService_scope() {
        return service_scope;
    }

    public void setService_scope(String service_scope) {
        this.service_scope = service_scope;
    }

    public String getNotice_content() {
        return notice_content;
    }

    public void setNotice_content(String notice_content) {
        this.notice_content = notice_content;
    }

    public boolean isNotice_state() {
        return notice_state;
    }

    public void setNotice_state(boolean notice_state) {
        this.notice_state = notice_state;
    }

    public boolean isAudit_state() {
        return audit_state;
    }

    public void setAudit_state(boolean audit_state) {
        this.audit_state = audit_state;
    }
}
