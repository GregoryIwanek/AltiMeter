package pl.grzegorziwanek.altimeter.app.details;

import android.os.Bundle;
import android.support.annotation.NonNull;

import pl.grzegorziwanek.altimeter.app.model.database.source.SessionDataSource;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 09.02.2017.
 */

public class DetailsPresenter implements DetailsContract.Presenter {

    private SessionDataSource.DetailsSessionCallback callbackDetails;
    private DetailsFragment mDetailsView;
    private final SessionRepository mSessionRepository;
    private String sessionId;

    public DetailsPresenter(String id,
                            @NonNull SessionRepository sessionRepository,
                            @NonNull DetailsFragment detailsFragment) {
        sessionId = id;
        mSessionRepository = sessionRepository;
        mDetailsView = checkNotNull(detailsFragment);
        mDetailsView.setPresenter(this);
        setCallbacks();
    }

    private void setCallbacks() {
        callbackDetails = new SessionDataSource.DetailsSessionCallback() {
            @Override
            public void onDetailsLoaded(Bundle args) {
                mDetailsView.setTitleTextView(args.getString("title"));
                mDetailsView.setDescriptionTextView(args.getString("description"));
                mDetailsView.setIdTextView(args.getString("id"));
                mDetailsView.setNumPointsTextView(args.getString("numOfPoints"));
                mDetailsView.setTimeStartTextView(args.getString("timeStart"));
                mDetailsView.setTimeEndTextView(args.getString("timeEnd"));
                mDetailsView.setDistanceTextView(args.getString("distance"));
            }
        };
    }

    @Override
    public void start() {
        mSessionRepository.getDetails(sessionId, callbackDetails);
    }
}
