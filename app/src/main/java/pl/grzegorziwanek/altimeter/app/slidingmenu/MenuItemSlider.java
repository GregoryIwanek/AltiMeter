package pl.grzegorziwanek.altimeter.app.slidingmenu;

/**
 * Created by Grzegorz Iwanek on 10.12.2016.
 */
public class MenuItemSlider {

    String title;
    int imageId;

    public MenuItemSlider(String title, int imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
