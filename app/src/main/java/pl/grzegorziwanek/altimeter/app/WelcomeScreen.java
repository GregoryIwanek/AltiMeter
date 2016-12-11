package pl.grzegorziwanek.altimeter.app;

import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import pl.grzegorziwanek.altimeter.app.slidingmenu.MenuItemSlider;
import pl.grzegorziwanek.altimeter.app.slidingmenu.SlidingMenuAdapter;

public class WelcomeScreen extends AppCompatActivity
{
    private List<MenuItemSlider> menuItemList;
    private SlidingMenuAdapter slidingMenuAdapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private RelativeLayout relativeLayoutMain;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        listViewSliding = (ListView) findViewById(R.id.lv_sliding_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.screen_welcome_activity);
        relativeLayoutMain = (RelativeLayout) findViewById(R.id.main_content);
        menuItemList = new ArrayList<>();

        menuItemList.add(new MenuItemSlider("Settings", R.drawable.ic_settings_blue_grey_50_18dp));
        menuItemList.add(new MenuItemSlider("About", R.drawable.ic_eject_light_green));
        menuItemList.add(new MenuItemSlider("Android", R.drawable.ic_android_light_green_a700_18dp));

        slidingMenuAdapter = new SlidingMenuAdapter(this, menuItemList);
        listViewSliding.setAdapter(slidingMenuAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewSliding.setItemChecked(0, true);

        drawerLayout.closeDrawer(listViewSliding);

        //handle on item click
        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listViewSliding.setItemChecked(i, true);
                drawerLayout.closeDrawer(listViewSliding);
            }


        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //check for saved instance of an app, run fragment containing layout if there is none
        if (savedInstanceState == null)
        {
            //series of task to start fragment with rich menu.
            //get fragmentManager -> transaction -> type of fragment transaction (add) -> commit to run;
            getFragmentManager().beginTransaction().add(R.id.screen_welcome_activity, new MainFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        actionBarDrawerToggle.syncState();
    }
}
