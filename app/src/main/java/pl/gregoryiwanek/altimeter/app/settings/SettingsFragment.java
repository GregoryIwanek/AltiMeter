package pl.gregoryiwanek.altimeter.app.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.gregoryiwanek.altimeter.app.R;

/**
 * Class consists preference settings fragment and preference view.
 */
public final class SettingsFragment extends PreferenceFragment {

    public SettingsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(this.getActivity(), R.color.colorNavyBlue));
        }
        return view;
    }
}