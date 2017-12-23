package at.shockbytes.coins.dagger;

import javax.inject.Singleton;

import at.shockbytes.coins.ui.activity.MainActivity;
import at.shockbytes.coins.ui.fragment.AddCurrencyFragment;
import at.shockbytes.coins.ui.fragment.MainFragment;
import at.shockbytes.coins.ui.fragment.SettingsFragment;
import at.shockbytes.coins.ui.fragment.dialog.CashoutDialogFragment;
import dagger.Component;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */
@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(MainFragment fragment);

    void inject(AddCurrencyFragment fragment);

    void inject(SettingsFragment fragment);

    void inject(CashoutDialogFragment dialogFragment);

}
