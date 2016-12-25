package pl.grzegorziwanek.altimeter.app.slidingmenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 10.12.2016.
 */
public class SlidingMenuAdapter extends BaseAdapter {

    private Context context;
    private List<MenuItemSlider> listMenuItemSlider;

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
        View v = View.inflate(context, R.layout.item_sliding_menu, null);

        ImageView imageView = (ImageView) v.findViewById(R.id.item_image);
        TextView textView = (TextView) v.findViewById(R.id.item_title);

        MenuItemSlider menuItemSlider = listMenuItemSlider.get(position);
        imageView.setImageResource(menuItemSlider.getImageId());
        textView.setText(menuItemSlider.getTitle());

        return v;
    }
}
