package pl.gregoryiwanek.altimeter.app.mainview;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Grzegorz Iwanek on 19.01.2017.
 */
public class SwipeRefreshLayoutChild extends SwipeRefreshLayout {

    private View mScrollUpChild;

    public SwipeRefreshLayoutChild(Context context) {
        super(context);
    }

    public SwipeRefreshLayoutChild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpChild != null) {
            return ViewCompat.canScrollVertically(mScrollUpChild, -1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }
}
