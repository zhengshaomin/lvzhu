package Min.app.plus.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Chat extends BmobObject {
    Urelation relationship;
    Order order;
    _User msg_author,msg_recipient;
    String msg_content;
    Boolean news,visible,type;//type为true为图片
    BmobFile photo;


    public Urelation getRelationship() {
        return relationship;
    }

    public void setRelationship(Urelation relationship) {
        this.relationship = relationship;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public _User getMsg_author() {
        return msg_author;
    }

    public void setMsg_author(_User msg_author) {
        this.msg_author = msg_author;
    }

    public _User getMsg_recipient() {
        return msg_recipient;
    }

    public void setMsg_recipient(_User msg_recipient) {
        this.msg_recipient = msg_recipient;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public Boolean getNews() {
        return news;
    }

    public void setNews(Boolean news) {
        this.news = news;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }
}
