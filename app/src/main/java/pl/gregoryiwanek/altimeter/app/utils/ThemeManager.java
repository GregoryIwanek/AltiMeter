package pl.gregoryiwanek.altimeter.app.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.gregoryiwanek.altimeter.app.R;

/**
 * Consists class responsible for picking values of colors from current styles or pointed styles.
 * Main purpose of it is to give option of dynamic switch of theme during a session.
 */
public class ThemeManager {

    public void applyColorToMultipleViews(ViewGroup rootView, int attrId, Class<?> lookedType, Context context) {
        List<View> viewList = new ArrayList<>();
        pickMultipleNestedViews(rootView, viewList, lookedType);
        applyColor(viewList, attrId, context);
    }

    public void applyColorToSingleView(View view, int attrId, Context context) {
        int colorId = getAttrColor(context, attrId);
        setViewColor(view, colorId);
    }

    public void applyColorToSingleNestedChildView(ViewGroup rootView, int attrId, Class<?> lookedType, Context context) {
        View view = pickSingleNestedView(rootView, lookedType, context);
        if (view != null) {
            applyColorToSingleView(view, attrId, context);
        }
    }

    public void applyColorToNonTransparentBackground(View view, int attrId, Context context) {
        int colorId = getAttrColor(context, attrId);
        view.setBackgroundColor(colorId);
    }

    private void pickMultipleNestedViews(ViewGroup viewGroup, List<View> viewList, Class<?> lookedType) {
        for (int i=0; i< viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                pickMultipleNestedViews((ViewGroup) viewGroup.getChildAt(i), viewList, lookedType);
            } else if (lookedType.isInstance(viewGroup.getChildAt(i))){
                viewList.add(viewGroup.getChildAt(i));
            }
        }
    }

    private View pickSingleNestedView(ViewGroup rootView, Class<?> lookedType, Context context) {
        for (int i=0; i< rootView.getChildCount(); i++) {
            if (rootView.getChildAt(i) instanceof ViewGroup) {
                pickSingleNestedView((ViewGroup) rootView.getChildAt(i), lookedType, context);
            } else if (lookedType.isInstance(rootView.getChildAt(i))){
                return rootView.getChildAt(i);
            }
        }
        return null;
    }

    private void applyColor(List<View> viewList, int attrId, Context context) {
        int colorId = getAttrColor(context, attrId);
        for (View view : viewList) {
            setViewColor(view, colorId);
        }
    }

    public int getAttrColor(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        // !!! set boolean resolveRes as true to make it work!!!
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }


    private void setViewColor(View view, int colorId) {
        setViewColor(view, colorId, PorterDuff.Mode.MULTIPLY);
    }

    private void setViewColor(View view, int colorId, PorterDuff.Mode mode) {
        if (isBackgroundInvisible(view)) {
            setViewBackgroundColor(view, colorId);
        } else {
            setViewColorFilter(view, colorId, mode);
        }
    }

    private void setViewBackgroundColor(View view, int colorId) {
        view.setBackgroundColor(colorId);
    }

    private void setViewColorFilter(View view, int colorId, PorterDuff.Mode mode) {
        view.getBackground().setColorFilter(colorId, mode);
    }

    private boolean isBackgroundInvisible(View view) {
        return view.getBackground() == null || !view.getBackground().isVisible();
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
