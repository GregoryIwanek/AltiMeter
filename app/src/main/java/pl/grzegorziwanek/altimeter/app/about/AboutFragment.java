package pl.grzegorziwanek.altimeter.app.about;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 21.01.2017.
 */

public final class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_about, container, false);
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(this.getActivity(), R.color.colorBlack));
        }
        return view;
    }
}

