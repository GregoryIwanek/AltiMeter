package pl.grzegorziwanek.altimeter.app.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 09.02.2017.
 */

public class DetailsFragment extends Fragment implements DetailsContract.View {

    private DetailsContract.Presenter mPresenter;

    public DetailsFragment() {}

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        return view;
    }

    @Override
    public void setPresenter(DetailsContract.Presenter presenter) {

    }
}