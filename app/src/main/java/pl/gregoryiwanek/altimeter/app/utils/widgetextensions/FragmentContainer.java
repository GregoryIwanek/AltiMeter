package pl.gregoryiwanek.altimeter.app.utils.widgetextensions;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FragmentContainer extends FrameLayout {

    public FragmentContainer(@NonNull Context context) {
        super(context);
    }

    public FragmentContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FragmentContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FragmentContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
