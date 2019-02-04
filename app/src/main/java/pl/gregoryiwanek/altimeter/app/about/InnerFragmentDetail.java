package pl.gregoryiwanek.altimeter.app.about;

import android.os.*;
import android.view.*;

import androidx.annotation.*;
import pl.gregoryiwanek.altimeter.app.R;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.*;

public class InnerFragmentDetail extends BasicInnerFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_details, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onFieldActionsClick() {}

    @OnClick(R.id.about_labels_details)
    public void onFieldLabelsClick() {
        showUpDialog(getString(R.string.about_labels_details));
    }

    @OnClick(R.id.about_edit_details)
    public void onFieldEditClick() {
        showUpDialog(getString(R.string.about_edit_details));
    }

    @OnClick(R.id.about_save_details)
    public void onFieldButtonSaveClick() {
        showUpDialog(getString(R.string.about_button_save_details));
    }

    @OnClick(R.id.about_info_details)
    public void onFieldInfoClick() {
        showUpDialog(getString(R.string.about_info_details));
    }
}
