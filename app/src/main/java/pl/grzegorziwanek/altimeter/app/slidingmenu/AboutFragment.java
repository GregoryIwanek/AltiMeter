package pl.grzegorziwanek.altimeter.app.slidingmenu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 18.12.2016.
 */
public class AboutFragment extends Fragment {

    public AboutFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_about, container, false);
        return rootView;
    }
}
