package pl.gregoryiwanek.altimeter.app.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Consists class responsible for picking values of colors from current styles or pointed styles.
 * Main purpose of it is to give option of dynamic switch of theme during a session.
 */
public class ThemeAttributesPicker {

    /**
     * Get color under given R.attr in the current theme.
     * @param context context of the application;
     * @param attrId name of the color (R.attr.name) to return in {@int} format;
     * @return current's theme value of the color under given R.attr;
     */
    public int getColor(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        // !!! set boolean resolveRes as true to make it work!!!
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }
}
