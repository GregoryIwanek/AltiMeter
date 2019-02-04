package pl.gregoryiwanek.altimeter.app.settings;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.PreferenceFragmentCompat;

import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.utils.stylecontroller.StyleController;

/**
 * Class consists preference settings fragment and preference view.
 */
public final class SettingsFragment extends PreferenceFragmentCompat {

    StyleController styleController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        styleController = new StyleController(getActivity());
    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setSettingsBackground(view);
        return view;
    }

    private void setSettingsBackground(View view) {
        styleController.applyColorAsBackground(view, R.attr.colorRootBackground);
    }
}

