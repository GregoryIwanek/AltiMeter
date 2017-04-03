package pl.gregoryiwanek.altimeter.app.about;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import pl.gregoryiwanek.altimeter.app.R;

public class InnerFragmentAuthor extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_author, container, false);

        ButterKnife.bind(this, view);
        return view;
    }
}
