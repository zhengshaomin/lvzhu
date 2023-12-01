package Min.app.plus;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @author daboluo
 */
public class Banners extends BmobObject {

    String imageurl,idorurl,title;
    Boolean type;
    BmobFile image;

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public String getIdorurl() {
        return idorurl;
    }

    public void setIdorurl(String idorurl) {
        this.idorurl = idorurl;
    }


    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
