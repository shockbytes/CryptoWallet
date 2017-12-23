package at.shockbytes.coins.ui.activity

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import at.shockbytes.coins.ui.fragment.AddCurrencyFragment

class AddCurrencyActivity : ContainerBackNavigableActivity() {

    override val displayFragment: Fragment
        get() = AddCurrencyFragment.newInstance()

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, AddCurrencyActivity::class.java)
        }
    }

}
