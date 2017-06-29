package pl.gregoryiwanek.altimeter.app.utils;

import android.content.Context;
import android.util.TypedValue;

import pl.gregoryiwanek.altimeter.app.R;

/**
 * Consists class responsible for picking values of colors from current styles or pointed styles.
 * Main purpose of it is to give option of dynamic switch of theme during a session.
 */
public class ThemeManager {

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

    public int getThemeAsInteger(String themeName) {
        int themeId;
        switch (themeName) {
            case "Light":
                themeId = R.style.Light;
                break;
            case "Dark":
                themeId = R.style.Dark;
                break;
            case "Candy":
                themeId = R.style.Candy;
                break;
            case "Dracula":
                themeId = R.style.Dracula;
                break;
            case "Ocean":
                themeId = R.style.Ocean;
                break;
            case "Forest":
                themeId = R.style.Forest;
                break;
            default:
                themeId = R.style.Light;
        }
        return themeId;
    }
}
