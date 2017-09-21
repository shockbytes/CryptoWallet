package at.shockbytes.coins.core;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import at.shockbytes.coins.R;
import at.shockbytes.coins.fragment.MainFragment;
import at.shockbytes.coins.util.ResourceManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQ_CODE_LOGIN = 0x1247;
    private static final int REQ_CODE_PERM_CONTACTS = 0x2224;
    private static final int REQ_CODE_NEW_BUY = 0x4890;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            showMainFragment();
        } else if (requestCode == REQ_CODE_NEW_BUY && resultCode == RESULT_OK) {
            mainFragment.onNewCurrencyEntryAvailable();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Do not support click on already selected item
        if (item.isChecked()) {
            return false;
        }

        switch (item.getItemId()) {

            case R.id.menu_navigation_balance:

                toolbar.setTitle(R.string.title_balance);
                showMainFragment();
                break;

            case R.id.menu_navigation_cashout:

                toolbar.setTitle(R.string.title_cashout);
                showFragment(MainFragment.newInstance(MainFragment.ViewType.CASHOUT));
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
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        startActivityForResult(AddCurrencyActivity.newIntent(this), REQ_CODE_NEW_BUY, options.toBundle());
    }

    private void setupActionBar() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(R.string.title_balance);
        }
    }

    private void setupNavigationDrawer() {

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getHeaderView(0)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initializePersonalizedDrawer();
                    }
                });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.contentdesc_drawer_open, R.string.contentdesc_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        initializePersonalizedDrawer();
    }

    private void showMainFragment() {
        mainFragment = MainFragment.newInstance(MainFragment.ViewType.BALANCE);
        showFragment(mainFragment);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_content, fragment)
                .commit();
    }

    @AfterPermissionGranted(REQ_CODE_PERM_CONTACTS)
    private void initializePersonalizedDrawer() {

        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {

            TextView navigationHeaderText = navigationView.getHeaderView(0)
                    .findViewById(R.id.navigation_header_text);
            ImageView navigationHeaderIcon = navigationView.getHeaderView(0)
                    .findViewById(R.id.navigation_header_imgview);

            String name = ResourceManager.getProfileName(this);
            if (!name.isEmpty() && navigationHeaderText != null) {
                navigationHeaderText.setText(ResourceManager.getProfileName(this));
            }

            Uri imageUri = ResourceManager.getProfileImage(this);
            if (imageUri != null) {
                navigationHeaderIcon.setImageDrawable(ResourceManager.createRoundedBitmap(this, imageUri));
            } else if (!name.isEmpty()) {
                navigationHeaderIcon.setImageDrawable(ResourceManager.createRoundedBitmap(this,
                        ResourceManager.createStringBitmap(ResourceManager.convertDpInPixel(96, this),
                                ContextCompat.getColor(this, R.color.colorPrimaryDark), String.valueOf(name.charAt(0)))));
            }

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.perm_contacts_rationale),
                    REQ_CODE_PERM_CONTACTS, Manifest.permission.READ_CONTACTS);
        }

    }

}
