package pl.gregoryiwanek.altimeter.app.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gregoryiwanek.altimeter.app.R;

public class InnerFragmentMap extends BasicInnerFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_map, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onFieldActionsClick() {
        showUpDialog(getString(R.string.about_actions_share));
    }

    @OnClick(R.id.about_map_content)
    public void onFieldMapContent() {
        showUpDialog(getString(R.string.about_map_content));
    }
}
