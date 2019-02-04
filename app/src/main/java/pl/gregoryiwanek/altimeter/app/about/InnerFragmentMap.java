package pl.gregoryiwanek.altimeter.app.about;

import android.os.*;
import android.view.*;

import androidx.annotation.*;
import pl.gregoryiwanek.altimeter.app.R;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.*;

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
