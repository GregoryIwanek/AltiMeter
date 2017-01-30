package pl.grzegorziwanek.altimeter.app.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;

import pl.grzegorziwanek.altimeter.app.BasicActivity;
import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 21.01.2017.
 */

public class MapActivity extends BasicActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        super.initiateUI();
    }
}
