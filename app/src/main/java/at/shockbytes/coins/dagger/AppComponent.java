package at.shockbytes.coins.dagger;

import javax.inject.Singleton;

import at.shockbytes.coins.core.AddCurrencyActivity;
import at.shockbytes.coins.core.MainActivity;
import at.shockbytes.coins.fragment.CashoutDialogFragment;
import at.shockbytes.coins.fragment.MainFragment;
import at.shockbytes.coins.fragment.SettingsFragment;
import dagger.Component;

/**
 * @author Martin Macheiner
 *         Date: 14.06.2017.
 */
@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(AddCurrencyActivity activity);

    void inject(MainFragment fragment);

    void inject(SettingsFragment fragment);

    void inject(CashoutDialogFragment dialogFragment);

}
