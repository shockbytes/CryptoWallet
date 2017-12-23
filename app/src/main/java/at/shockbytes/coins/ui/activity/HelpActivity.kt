package at.shockbytes.coins.ui.activity

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import at.shockbytes.coins.ui.fragment.HelpFragment

/**
 * @author Martin Macheiner
 * Date: 23.12.2017.
 */

class HelpActivity : ContainerBackNavigableActivity() {

    override val displayFragment: Fragment
        get() = HelpFragment.newInstance()


    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, HelpActivity::class.java)
        }
    }
}