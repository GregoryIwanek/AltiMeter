package pl.grzegorziwanek.altimeter.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeScreen extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        //check for saved instance of an app, run fragment containing layout if there is none
        if (savedInstanceState == null)
        {
            //series of task to start fragment with rich menu.
            //get fragmentManager -> transaction -> type of fragment transaction (add) -> commit to run;
            getFragmentManager().beginTransaction().add(R.id.screen_welcome_activity, new MainFragment()).commit();
        }
    }
}
