package at.shockbytes.coins.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.coins.R
import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.ui.fragment.MainFragment
import at.shockbytes.coins.ui.fragment.dialog.LocalCurrencyDialogFragment
import at.shockbytes.coins.util.AppParams
import at.shockbytes.coins.util.ResourceManager
import butterknife.OnClick
import co.mobiwise.materialintro.animation.MaterialIntroListener
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import kotterknife.bindView
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : BaseActivity(),
        NavigationView.OnNavigationItemSelectedListener, MaterialIntroListener {

    private val argViewtype: String = "argViewtype"

    private val fab: FloatingActionButton by bindView(R.id.main_fab)
    private val toolbar: Toolbar by bindView(R.id.main_toolbar)
    private val drawerLayout: DrawerLayout by bindView(R.id.main_drawer_layout)
    private val navigationView: NavigationView by bindView(R.id.main_navigation_view)

    private var drawerToggle: ActionBarDrawerToggle? = null

    private var balanceFragment: MainFragment? = null

    private var viewtype: MainFragment.ViewType = MainFragment.ViewType.BALANCE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        setupNavigationDrawer()

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.prefs_key_fingerprint_as_auth), false)) {
            startActivityForResult(LoginActivity.newIntent(this), REQ_CODE_LOGIN)
        } else {

            viewtype = savedInstanceState?.getSerializable(argViewtype)
                    as? MainFragment.ViewType ?: MainFragment.ViewType.BALANCE
            if (viewtype == MainFragment.ViewType.BALANCE) {
                showBalanceFragment()
            } else {
                showCashoutFragment()
            }
        }
        showShowcaseViews()
    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                showBalanceFragment()
            } else {
                supportFinishAfterTransition()
            }
        } else if (requestCode == REQ_CODE_NEW_BUY && resultCode == Activity.RESULT_OK) {
            balanceFragment?.onNewCurrencyEntryAvailable()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(argViewtype, viewtype)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Do not support click on already selected item
        if (item.isChecked) {
            return false
        }

        when (item.itemId) {

            R.id.menu_navigation_balance -> showBalanceFragment()

            R.id.menu_navigation_cashout -> showCashoutFragment()

            R.id.menu_navigation_help ->
                startActivity(HelpActivity.newIntent(this),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())

            R.id.menu_navigation_settings ->
                startActivity(SettingsActivity.newIntent(this),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }

        drawerLayout.closeDrawer(Gravity.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onUserClicked(s: String) {

        when (s) {

            AppParams.showcaseIdFab -> showShowcaseView(balanceFragment?.balanceHeader,
                    AppParams.showcaseIdHeader, R.string.showcase_header)

            AppParams.showcaseIdHeader -> showShowcaseView(ResourceManager.getNavigationIcon(toolbar),
                    AppParams.showcaseIdToolbar, R.string.showcase_drawer)

            AppParams.showcaseIdToolbar -> askForLocalCurrency()
        }
    }

    @OnClick(R.id.main_fab)
    fun onClickFab() {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        startActivityForResult(AddCurrencyActivity.newIntent(this), REQ_CODE_NEW_BUY,
                options.toBundle())
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.title_balance)
    }

    private fun setupNavigationDrawer() {

        navigationView.setNavigationItemSelectedListener(this)
        navigationView.getHeaderView(0)
                .setOnClickListener { initializePersonalizedDrawer() }

        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.contentdesc_drawer_open, R.string.contentdesc_drawer_close)
        drawerLayout.addDrawerListener(drawerToggle!!)
        drawerToggle?.syncState()

        initializePersonalizedDrawer()
    }

    private fun showBalanceFragment() {
        toolbar.setTitle(R.string.title_balance)

        viewtype = MainFragment.ViewType.BALANCE
        balanceFragment = MainFragment.newInstance(viewtype)
        showFragment(balanceFragment)
    }

    private fun showCashoutFragment() {
        toolbar.setTitle(R.string.title_cashout)

        viewtype = MainFragment.ViewType.CASHOUT
        showFragment(MainFragment.newInstance(viewtype))
    }

    private fun showFragment(fragment: Fragment?) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_content, fragment)
                .commit()
    }

    @AfterPermissionGranted(REQ_CODE_PERM_CONTACTS)
    private fun initializePersonalizedDrawer() {

        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {

            val navigationHeaderText = navigationView.getHeaderView(0)
                    .findViewById<TextView>(R.id.navigation_header_text)
            val navigationHeaderIcon = navigationView.getHeaderView(0)
                    .findViewById<ImageView>(R.id.navigation_header_imgview)

            val name = ResourceManager.getProfileName(this)
            if (!name.isEmpty() && navigationHeaderText != null) {
                navigationHeaderText.text = ResourceManager.getProfileName(this)
            }

            val imageUri = ResourceManager.getProfileImage(this)
            if (imageUri != null) {
                navigationHeaderIcon.setImageDrawable(ResourceManager.createRoundedBitmap(this, imageUri))
            } else if (!name.isEmpty()) {
                navigationHeaderIcon.setImageDrawable(ResourceManager.createRoundedBitmap(this,
                        ResourceManager.createStringBitmap(ResourceManager.convertDpInPixel(96, this),
                                ContextCompat.getColor(this, R.color.colorPrimaryDark), name[0].toString())))
            }

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.perm_contacts_rationale),
                    REQ_CODE_PERM_CONTACTS, Manifest.permission.READ_CONTACTS)
        }
    }

    private fun showShowcaseViews() {
        showShowcaseView(fab, AppParams.showcaseIdFab, R.string.showcase_fab)
    }

    private fun showShowcaseView(view: View?, id: String, @StringRes textId: Int) {
        MaterialIntroView.Builder(this)
                .enableDotAnimation(false)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(200)
                .enableFadeAnimation(true)
                .setInfoText(getString(textId))
                .setShape(ShapeType.CIRCLE)
                .setTarget(view)
                .setListener(this)
                .setTextColor(Color.WHITE)
                .enableDotAnimation(true)
                .setUsageId(id)
                .show()
    }

    private fun askForLocalCurrency() {
        LocalCurrencyDialogFragment.newInstance()
                .setOnCompletionListener {
                    balanceFragment?.onRefresh() // Refresh with possible new currency
                }
                .show(supportFragmentManager, "local-currency-dialog-fragment")
    }

    companion object {

        private const val REQ_CODE_LOGIN = 0x1247
        private const val REQ_CODE_PERM_CONTACTS = 0x2224
        private const val REQ_CODE_NEW_BUY = 0x4890
    }
}
