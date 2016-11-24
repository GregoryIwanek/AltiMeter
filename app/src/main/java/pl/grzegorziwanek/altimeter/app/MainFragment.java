package pl.grzegorziwanek.altimeter.app;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Grzegorz Iwanek on 23.11.2016.
 */
public class MainFragment extends Fragment
{
    public MainFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    //consist actions to perform upon re/start of app ( update current location and information)
    @Override
    public void onStart()
    {
        super.onStart();

        //refresh info on screen on app start/restart
        updateAppInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    //called onStart and restart-> update information to show on app start
    private void updateAppInfo()
    {

    }

    //inner class responsible for background update
    //ASyncTask <params, progress, result> -> params: given entry data to work on; progress: data to show progress; result: result of background execution
    public class FetchDataInfoTask extends AsyncTask<String, Void, String[]>
    {
        @Override
        protected String[] doInBackground(String... strings) {
            return new String[0];
        }
    }
}
