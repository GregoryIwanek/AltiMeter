package pl.gregoryiwanek.altimeter.app.upgradepro;

import android.support.annotation.NonNull;

import pl.gregoryiwanek.altimeter.app.upgradepro.UpgradeProContract.View;

import static com.google.common.base.Preconditions.checkNotNull;

class UpgradeProPresenter implements UpgradeProContract.Presenter{

    private final View mUpgradeProView;

    UpgradeProPresenter(@NonNull View upgradeProView) {
        mUpgradeProView = checkNotNull(upgradeProView);
        mUpgradeProView.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
