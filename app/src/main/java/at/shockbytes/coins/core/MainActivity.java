package at.shockbytes.coins.core;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import at.shockbytes.coins.R;
import at.shockbytes.coins.currency.OwnedCurrency;
import at.shockbytes.coins.fragment.CashoutFragment;
import at.shockbytes.coins.fragment.MainFragment;
import at.shockbytes.coins.fragment.dialog.AddCurrencyDialogFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements AddCurrencyDialogFragment.OnCurrencyAddedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final int REQ_CODE_LOGIN = 0x1247;

    private MainFragment mainFragment;

    @Bind(R.id.main_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.main_drawer_layout)
    protected DrawerLayout drawerLayout;

    @Bind(R.id.main_navigation_view)
    protected NavigationView navigationView;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupActionBar();
        setupNavigationDrawer();

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.prefs_key_fingerprint_as_auth), false)) {
            startActivityForResult(LoginActivity.newIntent(this), REQ_CODE_LOGIN);
        } else {
            showMainFragment();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_main_settings) {
            startActivity(SettingsActivity.newIntent(this),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            showMainFragment();
        }

    }

    @Override
    public void onCurrencyAdded(OwnedCurrency ownedCurrency) {
        mainFragment.onNewCurrencyEntryAvailable(ownedCurrency);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Do not support click on already selected item
        if (item.isChecked()) {
            return false;
        }

        switch (item.getItemId()) {

            case R.id.menu_navigation_balance:

                toolbar.setTitle("Balance");
                showMainFragment();
                break;

            case R.id.menu_navigation_cashout:

                toolbar.setTitle("Cashout");
                showFragment(CashoutFragment.newInstance());
                break;

            case R.id.menu_navigation_settings:

                startActivity(SettingsActivity.newIntent(this),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                break;
        }

        drawerLayout.closeDrawer(Gravity.START);

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @OnClick(R.id.main_fab)
    protected void onClickFab() {
        AddCurrencyDialogFragment dialogFragment = AddCurrencyDialogFragment.newInstance();
        dialogFragment.setOnCurrencyAddedListener(this);
        dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
    }

    private void setupActionBar() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("Balance");
        }
    }

    private void setupNavigationDrawer() {

        navigationView.setNavigationItemSelectedListener(this);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.contentdesc_drawer_open, R.string.contentdesc_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    private void showMainFragment() {
        mainFragment = MainFragment.newInstance();
        showFragment(mainFragment);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_content, fragment)
                .commit();
    }

}
