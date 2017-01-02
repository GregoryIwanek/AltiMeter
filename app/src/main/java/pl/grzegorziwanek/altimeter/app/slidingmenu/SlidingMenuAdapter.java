package pl.grzegorziwanek.altimeter.app.slidingmenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 10.12.2016.
 */
public class SlidingMenuAdapter extends BaseAdapter {

    private Context context;
    private List<MenuItemSlider> listMenuItemSlider;
    @BindView(R.id.item_image) ImageView mImageView;
    @BindView(R.id.item_title) TextView mTextView;

    public SlidingMenuAdapter(Context context, List<MenuItemSlider> listMenuItemSlider) {
        this.context = context;
        this.listMenuItemSlider = listMenuItemSlider;
    }

    @Override
    public int getCount() {
        return listMenuItemSlider.size();
    }

    @Override
    public Object getItem(int position) {
        return listMenuItemSlider.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = view;
        if (view == null) {
            v = View.inflate(context, R.layout.item_sliding_menu, null);
        }

        ButterKnife.bind(this, v);

        populateMenuSlider(position);

        return v;
    }

    private void populateMenuSlider(int itemPosition) {
        MenuItemSlider menuItemSlider = listMenuItemSlider.get(itemPosition);
        mImageView.setImageResource(menuItemSlider.getImageId());
        mTextView.setText(menuItemSlider.getTitle());
    }
}
