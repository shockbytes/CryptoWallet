package at.shockbytes.coins.dagger

import at.shockbytes.coins.ui.activity.MainActivity
import at.shockbytes.coins.ui.fragment.AddCurrencyFragment
import at.shockbytes.coins.ui.fragment.MainFragment
import at.shockbytes.coins.ui.fragment.SettingsFragment
import at.shockbytes.coins.ui.fragment.dialog.CashoutDialogFragment
import at.shockbytes.coins.ui.fragment.dialog.LocalCurrencyDialogFragment
import dagger.Component
import javax.inject.Singleton

/**
 * @author Martin Macheiner
 * Date: 14.06.2017.
 */
@Singleton
@Component(modules = [(AppModule::class), (NetworkModule::class)])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: MainFragment)

    fun inject(fragment: AddCurrencyFragment)

    fun inject(fragment: SettingsFragment)

    fun inject(dialogFragment: CashoutDialogFragment)

    fun inject(dialogFragment: LocalCurrencyDialogFragment)

}
