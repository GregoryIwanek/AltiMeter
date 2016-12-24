package pl.grzegorziwanek.altimeter.app.slidingmenu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 10.12.2016.
 */
public class SettingsFragment extends PreferenceFragment
{
    public SettingsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
