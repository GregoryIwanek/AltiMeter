package pl.grzegorziwanek.altimeter.app.details;

import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 09.02.2017.
 */

public class DetailsPresenter implements DetailsContract.Presenter {

    DetailsFragment mDetailsView;

    public DetailsPresenter(@NonNull DetailsFragment detailsFragment) {
        mDetailsView = checkNotNull(detailsFragment);
    }

    @Override
    public void start() {

    }
}
