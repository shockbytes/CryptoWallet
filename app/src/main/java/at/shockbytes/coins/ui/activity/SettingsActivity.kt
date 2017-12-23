package at.shockbytes.coins.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.ui.fragment.SettingsFragment
import at.shockbytes.dante.ui.activity.BackNavigableActivity


/**
 * @author Martin Macheiner
 * Date: 27.10.2015.
 */
class SettingsActivity : BackNavigableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment.newInstance())
                .commit()
    }

    override fun injectToGraph(appComponent: AppComponent) {
        // Do nothing here
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

}
