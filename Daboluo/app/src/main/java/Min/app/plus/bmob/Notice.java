package Min.app.plus.bmob;

import cn.bmob.v3.BmobObject;

/**
 * @author daboluo
 */
public class Notice extends BmobObject {

    String title, content, promise, cancel, url;
    Boolean visiable,type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPromise() {
        return promise;
    }

    public void setPromise(String promise) {
        this.promise = promise;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getVisiable() {
        return visiable;
    }

    public void setVisiable(Boolean visiable) {
        this.visiable = visiable;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }
}