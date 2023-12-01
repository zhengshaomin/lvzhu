package Min.app.plus.bmob;

import cn.bmob.v3.BmobObject;

/**
 * @author daboluo
 */
public class Reply extends BmobObject {
    private String content;
    private Posts post;
    private Boolean type,news,visible;//type类型为true则为点赞，反之为提醒（评论，回复）
    //评论的用户
    private _User author, recipient;
    private Urelation relationship;

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

    // 所评论的帖子

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public _User getRecipient() {
        return recipient;
    }

    public void setRecipient(_User recipient) {
        this.recipient = recipient;
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

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }

    public Urelation getRelationship() {
        return relationship;
    }

    public void setRelationship(Urelation relationship) {
        this.relationship = relationship;
    }

}


