package pl.grzegorziwanek.altimeter.app.slidingmenu;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 10.12.2016.
 */
public class SettingsFragment extends Fragment
{
    public SettingsFragment(){}

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.layout.fragment_settings);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;
    }
}
