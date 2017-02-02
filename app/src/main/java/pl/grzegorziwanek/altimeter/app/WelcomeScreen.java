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

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.slidingmenu.AboutFragment;
import pl.grzegorziwanek.altimeter.app.slidingmenu.MenuItemSlider;
import pl.grzegorziwanek.altimeter.app.slidingmenu.SettingsFragment;
import pl.grzegorziwanek.altimeter.app.slidingmenu.SlidingMenuAdapter;

public class WelcomeScreen extends AppCompatActivity {

    @BindView(R.id.lv_sliding_menu) ListView mNavigationMenuSlidingList;
    @BindView(R.id.screen_welcome_activity) DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        ButterKnife.bind(this);

        setNavigationSlider();

//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.screen_welcome_activity, new MainFragment()).commit();
//        }
    }

    private void setNavigationSlider() {
        setNavigationMenuSlidingList();
        setDisplayHome();
        setActionBarDrawerToggle();
        setListenerToDrawerLayout();
    }

    private void setNavigationMenuSlidingList() {
        mNavigationMenuSlidingList.setItemsCanFocus(false);
        mNavigationMenuSlidingList.setAdapter(getSlidingMenuAdapter());
        mNavigationMenuSlidingList.setOnItemClickListener(setMenuListOnClickListener());
    }

    private SlidingMenuAdapter getSlidingMenuAdapter() {
        return new SlidingMenuAdapter(this, getMenuItemList());
    }

    private List<MenuItemSlider> getMenuItemList() {
        List<MenuItemSlider> menuItemList = new ArrayList<>();
        populateMenuList(menuItemList);
        return menuItemList;
    }

    private void populateMenuList(List<MenuItemSlider> menuItemList) {
        menuItemList.add(new MenuItemSlider("Settings", R.drawable.ic_settings_black_18dp));
        menuItemList.add(new MenuItemSlider("About", R.drawable.ic_eject_light_green));
        menuItemList.add(new MenuItemSlider("Android", R.drawable.ic_android_light_green_a700_18dp));
    }

    private AdapterView.OnItemClickListener setMenuListOnClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mNavigationMenuSlidingList.setItemChecked(position, true);
                replaceCurrentFragment(position);
                drawerLayout.closeDrawer(mNavigationMenuSlidingList);
            }
        };
    }

    private void replaceCurrentFragment(int pos) {
        Fragment fragment;
        fragment = setFragment(pos);
        replaceFragment(fragment);
    }

    //TODO-> populate with new kind of layouts and fragments (settings, about and share >> (more?))
    private Fragment setFragment(int posClicked) {
        switch (posClicked) {
            case 0: return new SettingsFragment();
            case 1: return new AboutFragment();
            case 2: return new AboutFragment();
            default: return new SettingsFragment();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.screen_welcome_activity, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setDisplayHome() {
        if (getSupportActionBar() != null) {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setActionBarDrawerToggle() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_opened, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                mNavigationMenuSlidingList.bringToFront();
                mNavigationMenuSlidingList.requestLayout();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
    }

    private void setListenerToDrawerLayout() {
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
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
}
