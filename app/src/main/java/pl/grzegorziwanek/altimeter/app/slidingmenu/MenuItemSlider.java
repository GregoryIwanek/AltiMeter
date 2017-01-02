package pl.grzegorziwanek.altimeter.app.slidingmenu;

/**
 * Created by Grzegorz Iwanek on 10.12.2016.
 */
public class MenuItemSlider {

    private String mTitle;
    private int mImageId;

    public MenuItemSlider(String title, int imageId) {
        mTitle = title;
        mImageId = imageId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getImageId() {
        return mImageId;
    }

    public void setImageId(int imageId) {
        mImageId = imageId;
    }
}
