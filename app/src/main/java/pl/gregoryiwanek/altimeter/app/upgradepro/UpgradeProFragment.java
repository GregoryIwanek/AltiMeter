package pl.gregoryiwanek.altimeter.app.upgradepro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import pl.gregoryiwanek.altimeter.app.R;

public class UpgradeProFragment extends Fragment implements UpgradeProContract.View {

    private UpgradeProContract.Presenter mPresenter;

    public static UpgradeProFragment newInstance() {
        return new UpgradeProFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upgrade_pro, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        return view;
    }

    @Override
    public void setPresenter(UpgradeProContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }
}