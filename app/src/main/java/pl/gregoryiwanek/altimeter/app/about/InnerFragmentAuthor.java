package pl.gregoryiwanek.altimeter.app.about;

import android.os.*;
import android.view.*;

import androidx.annotation.*;
import androidx.fragment.app.*;
import pl.gregoryiwanek.altimeter.app.R;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.*;

public class InnerFragmentAuthor extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_author, container, false);

        ButterKnife.bind(this, view);
        return view;
    }
}
