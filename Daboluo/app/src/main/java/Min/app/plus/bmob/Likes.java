package Min.app.plus.bmob;

import cn.bmob.v3.BmobObject;

/**
 * 作者：daboluo on 2023/9/17 19:13
 * Email:daboluo719@gmail.com
 */
public class Likes extends BmobObject {

    _User user;
    Posts post;

    public _User getUser() {
        return user;
    }

    public void setUser(_User user) {
        this.user = user;
    }

    public Posts getPost() {
        return post;
    }

    public void setPost(Posts post) {
        this.post = post;
    }
}
