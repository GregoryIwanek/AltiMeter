package pl.grzegorziwanek.altimeter.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.slidingmenu.AboutFragment;
import pl.grzegorziwanek.altimeter.app.slidingmenu.MenuItemSlider;
import pl.grzegorziwanek.altimeter.app.slidingmenu.SettingsFragment;
import pl.grzegorziwanek.altimeter.app.slidingmenu.SlidingMenuAdapter;

public class WelcomeScreen extends AppCompatActivity {

    private List<MenuItemSlider> menuItemList;
    private SlidingMenuAdapter slidingMenuAdapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    //private RelativeLayout relativeLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        listViewSliding = (ListView) findViewById(R.id.lv_sliding_menu);
        listViewSliding.setItemsCanFocus(false);
        drawerLayout = (DrawerLayout) findViewById(R.id.screen_welcome_activity);
        //relativeLayout = (RelativeLayout) findViewById(R.id.main_content);
        menuItemList = new ArrayList<>();

        menuItemList.add(new MenuItemSlider("Settings", R.drawable.ic_settings_black_18dp));
        menuItemList.add(new MenuItemSlider("About", R.drawable.ic_eject_light_green));
        menuItemList.add(new MenuItemSlider("Android", R.drawable.ic_android_light_green_a700_18dp));

        slidingMenuAdapter = new SlidingMenuAdapter(this, menuItemList);
        listViewSliding.setAdapter(slidingMenuAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listViewSliding.setItemChecked(0, true);

        drawerLayout.closeDrawer(listViewSliding);

        //handle on item click
        System.out.println("SETTING ONCLICK");
        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                System.out.println(" Setting ONCLICK listener");
                listViewSliding.setItemChecked(position, true);
                replaceFragment(position);
                drawerLayout.closeDrawer(listViewSliding);
                System.out.println("POSITION IS " + position);
                System.out.println("IS THERE ONCLICK LISTENER? " + listViewSliding.getOnItemClickListener());
            }
        });

        System.out.println("IS THERE ONCLICK LISTENER? " + listViewSliding.getOnItemClickListener());

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                listViewSliding.bringToFront();
                listViewSliding.requestLayout();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //check for saved instance of an app, run fragment containing layout if there is none
        if (savedInstanceState == null) {
            //series of task to start fragment with rich menu.
            //get fragmentManager -> transaction -> type of fragment transaction (add) -> commit to run;
            getFragmentManager().beginTransaction().add(R.id.screen_welcome_activity, new MainFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        actionBarDrawerToggle.syncState();
    }

    public void replaceFragment(int pos) {
        Fragment fragment;
        switch (pos) {
            case 0: fragment = new SettingsFragment();
                break;
            case 1: fragment = new AboutFragment();
                break;
            case 2: fragment = new AboutFragment();
                break;
            default: fragment = new SettingsFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_welcome_activity, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
