package pl.gregoryiwanek.altimeter.app.about;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.utils.stylecontroller.StyleController;

public final class AboutFragmentMainWindow extends Fragment {

    private StyleController themePicker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themePicker = new StyleController(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        themePicker.applyColorToSingleKnownView(view, R.attr.colorRootBackground);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrameAbout, new InnerFragmentGraphs());
        ft.commit();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setButtonColorsByTheme();
    }

    private void setButtonColorsByTheme() {
        ViewGroup viewGroupContainer =  (ViewGroup) getView();
        List<View> buttonList = new ArrayList<>();
        populateButtonList(viewGroupContainer, buttonList);
        applyThemeColorsToMultipleViews(buttonList, R.attr.colorPrimary);
    }

    private void populateButtonList(ViewGroup viewGroup, List<View> buttonList) {
        for (int i=0; i< viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                populateButtonList((ViewGroup) viewGroup.getChildAt(i), buttonList);
            } else if (viewGroup.getChildAt(i) instanceof Button){
                buttonList.add(viewGroup.getChildAt(i));
            }
        }
    }

    @OnClick(R.id.about_button_graphs)
    public void onButtonGraphsClick() {
        setFragmentToReplace(R.id.about_button_graphs);
    }

    @OnClick(R.id.about_button_new_graph)
    public void onButtonNewGraphClick() {
        setFragmentToReplace(R.id.about_button_new_graph);
    }

    @OnClick(R.id.about_button_details)
    public void onButtonDetailsClick() {
        setFragmentToReplace(R.id.about_button_details);
    }

    @OnClick(R.id.about_button_stats)
    public void onButtonStatsClick() {
        setFragmentToReplace(R.id.about_button_stats);
    }

    @OnClick(R.id.about_button_map)
    public void onButtonMapClick() {
        setFragmentToReplace(R.id.about_button_map);
    }

    @OnClick(R.id.about_button_author)
    public void onButtonAuthorClick() {
        setFragmentToReplace(R.id.about_button_author);
    }

    private void setFragmentToReplace(int id) {
        switch (id) {
            case R.id.about_button_graphs:
                replaceFragment(new InnerFragmentGraphs());
                break;
            case R.id.about_button_new_graph:
                replaceFragment(new InnerFragmentNew());
                break;
            case R.id.about_button_details:
                replaceFragment(new InnerFragmentDetail());
                break;
            case R.id.about_button_stats:
                replaceFragment(new InnerFragmentStats());
                break;
            case R.id.about_button_map:
                replaceFragment(new InnerFragmentMap());
                break;
            case R.id.about_button_author:
                replaceFragment(new InnerFragmentAuthor());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrameAbout, fragment);
        ft.commit();
    }

    // TODO: 29.06.2017 refactor this code and merge one with BasicFragment (app)
    private void applyThemeColorsToMultipleViews(List<View> viewList, int attrId) {
        int colorId = getThemeAttrColor(attrId);
        for (View view : viewList) {
            setViewColor(view, colorId);
        }
    }

    private int getThemeAttrColor(int attrId) {
        return themePicker.getAttrColor(attrId);
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
}
