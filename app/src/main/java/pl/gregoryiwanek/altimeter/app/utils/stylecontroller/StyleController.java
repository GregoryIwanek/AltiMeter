package pl.gregoryiwanek.altimeter.app.utils.stylecontroller;

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
public class StyleController {

    private Context mContext;

    public StyleController(Context context) {
        mContext = context;
    }

    public void applyColorAsBackground(View view, int attrId) {
        int colorId = getAttrColor(attrId);
        view.setBackgroundColor(colorId);
    }

    public void applyColorToSingleKnownView(View view, int attrId) {
        int colorId = getAttrColor(attrId);
        setColor(view, colorId);
    }

    public void applyColorToSingleUnknownNestedView(ViewGroup rootView, int attrId, Class<?> lookedType) {
        View view = pickSingleNestedView(rootView, lookedType);
        if (view != null) {
            applyColorToSingleKnownView(view, attrId);
        }
    }

    public void applyColorToMultipleViews(ViewGroup rootView, int attrId, Class<?> lookedType) {
        List<View> viewList = new ArrayList<>();
        pickMultipleNestedViews(rootView, viewList, lookedType);
        applyColorToView(viewList, attrId);
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

    private View pickSingleNestedView(ViewGroup viewGroup, Class<?> lookedType) {
        for (int i=0; i< viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                pickSingleNestedView((ViewGroup) viewGroup.getChildAt(i), lookedType);
            } else if (lookedType.isInstance(viewGroup.getChildAt(i))){
                return viewGroup.getChildAt(i);
            }
        }
        return null;
    }

    private void applyColorToView(List<View> viewList, int attrId) {
        int colorId = getAttrColor(attrId);
        for (View view : viewList) {
            setColor(view, colorId);
        }
    }

    public int getAttrColor(int attrId) {
        TypedValue typedValue = new TypedValue();
        // !!! set boolean resolveRes as true to make it work!!!
        mContext.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.data;
    }

    private void setColor(View view, int colorId) {
        if (isBackgroundInvisible(view)) {
            setBackgroundColor(view, colorId);
        } else {
            setColorFilter(view, colorId);
        }
    }

    private boolean isBackgroundInvisible(View view) {
        return view.getBackground() == null || !view.getBackground().isVisible();
    }

    private void setBackgroundColor(View view, int colorId) {
        view.setBackgroundColor(colorId);
    }

    private void setColorFilter(View view, int colorId) {
        view.getBackground().setColorFilter(colorId, PorterDuff.Mode.MULTIPLY);
    }

    public int getStyleAsInteger(String themeName) {
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
