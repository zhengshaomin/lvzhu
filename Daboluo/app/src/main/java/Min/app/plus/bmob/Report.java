package Min.app.plus.bmob;

import Min.app.plus.bmob.Posts;
import Min.app.plus.bmob._User;
import cn.bmob.v3.BmobObject;

/**
 * @author daboluo
 */
public class Report extends BmobObject {

    private _User author;
    // 所评论的帖子
    private Posts report_post;
    private _User report_user;

    public _User getReport_user() {
        return report_user;
    }

    public void setReport_user(_User report_user) {
        this.report_user = report_user;
    }

    private String report_content;

    public _User getAuthor() {
        return author;
    }

    public void setAuthor(_User author) {
        this.author = author;
    }

    public Posts getReport_post() {
        return report_post;
    }

    public void setReport_post(Posts report_post) {
        this.report_post = report_post;
    }

    public String getReport_content() {
        return report_content;
    }

    public void setReport_content(String report_content) {
        this.report_content = report_content;
    }
}

