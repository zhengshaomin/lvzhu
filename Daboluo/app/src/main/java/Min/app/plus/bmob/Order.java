package Min.app.plus.bmob;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author daboluo
 */
public class Order extends BmobObject {
    _User author,recipient;//作者，接单者
    String state,title,content,type,address;//接单状态，内容
    boolean audit_state,user_visible;//审核状态
    _User consumer;
    Store store;
    Goods goods;
    String price;
    BmobFile photo;//图片
    String remarks;//备注
    String merchantsremarks;//商家备注
    String after_sales_type,after_sales_reasons;

    public String getAfter_sales_type() {
        return after_sales_type;
    }

    public void setAfter_sales_type(String after_sales_type) {
        this.after_sales_type = after_sales_type;
    }

    public boolean isUser_visible() {
        return user_visible;
    }

    public void setUser_visible(boolean user_visible) {
        this.user_visible = user_visible;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public _User getAuthor() {
        return author;
    }

    public void setAuthor(_User author) {
        this.author = author;
    }

    public _User getRecipient() {
        return recipient;
    }

    public void setRecipient(_User recipient) {
        this.recipient = recipient;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isAudit_state() {
        return audit_state;
    }

    public void setAudit_state(boolean audit_state) {
        this.audit_state = audit_state;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }

    public _User getConsumer() {
        return consumer;
    }

    public void setConsumer(_User consumer) {
        this.consumer = consumer;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getAfter_sales_reasons() {
        return after_sales_reasons;
    }

    public void setAfter_sales_reasons(String after_sales_reasons) {
        this.after_sales_reasons = after_sales_reasons;
    }

    public String getMerchantsremarks() {
        return merchantsremarks;
    }

    public void setMerchantsremarks(String merchantsremarks) {
        this.merchantsremarks = merchantsremarks;
    }
}
