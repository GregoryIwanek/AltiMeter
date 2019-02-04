package pl.gregoryiwanek.altimeter.app.upgradepro;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import pl.gregoryiwanek.altimeter.app.BasicActivity;
import pl.gregoryiwanek.altimeter.app.R;

/**
 * Consists of main activity of UpgradePro section.
 * Section responsible for app upgrade from FREE version to PRO version.
 */
public class UpgradeProActivity extends BasicActivity {

    private UpgradeProFragment mUpgradeProFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_graph);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_graph);

        super.initiateUI();
        setUpgradeProFragment();
        setPresenter();
    }

    private void setUpgradeProFragment() {
        mUpgradeProFragment =
                (UpgradeProFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mUpgradeProFragment == null) {
            mUpgradeProFragment = UpgradeProFragment.newInstance();
            addFragmentToActivityOnStart(
                    getSupportFragmentManager(), mUpgradeProFragment, R.id.contentFrame);
        }
    }

    private void setPresenter() {
        UpgradeProPresenter upgradeProPresenter = new UpgradeProPresenter(mUpgradeProFragment);
    }
}
