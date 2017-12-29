package at.shockbytes.coins.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import at.shockbytes.coins.R
import at.shockbytes.coins.adapter.CurrencySpinnerAdapter
import at.shockbytes.coins.currency.CryptoCurrency
import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.currency.RealCurrency
import at.shockbytes.coins.currency.price.PriceProxy
import at.shockbytes.coins.dagger.AppComponent
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotterknife.bindView
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 23.12.2017.
 */
class AddCurrencyFragment : BaseFragment() {

    @Inject
    protected lateinit var currencyManager: CurrencyManager

    @Inject
    protected lateinit var priceProxy: PriceProxy

    private val editCryptoCurrency: TextInputEditText by bindView(R.id.activity_add_currency_edit_cryptocurrency)
    private val editCurrency: TextInputEditText by bindView(R.id.activity_add_currency_edit_currency)
    private val tilCryptoCurrency: TextInputLayout by bindView(R.id.activity_add_currency_til_cryptocurrency)
    private val tilCurrency: TextInputLayout by bindView(R.id.activity_add_currency_til_currency)
    private val spinnerCryptoCurrency: Spinner by bindView(R.id.activity_add_currency_spinner_cryptocurrency)
    private val spinnerCurrency: Spinner by bindView(R.id.activity_add_currency_spinner_currency)
    private val btnAdd: AppCompatButton by bindView(R.id.activity_add_currency_btn_save)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set it default to canceled and set to OK when 'save' is clicked
        activity.setResult(Activity.RESULT_CANCELED, Intent())
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_add_currency, container, false)
    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun setupViews() {

        spinnerCurrency.adapter = CurrencySpinnerAdapter(context, getCurrencyItems())
        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val item = spinnerCurrency.selectedItem as CurrencySpinnerAdapter.CurrencySpinnerAdapterItem
                tilCurrency.hint = item.name
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        // Select the local currency as default value
        spinnerCurrency.setSelection(currencyManager.localCurrency.ordinal, true)

        spinnerCryptoCurrency.adapter = CurrencySpinnerAdapter(context, getCryptoCurrencyItems())
        spinnerCryptoCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val item = spinnerCryptoCurrency.selectedItem as CurrencySpinnerAdapter.CurrencySpinnerAdapterItem
                tilCryptoCurrency.hint = item.name
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        // This has to be called after all the other stuff is initialized!!!
        setupReactiveViews()
    }

    private fun setupReactiveViews() {

        val cd = RxTextView.textChanges(editCurrency)
                .map { !it.isEmpty() && it.toString().toDouble() > 0.0 }
                .distinctUntilChanged()
        val ccd = RxTextView.textChanges(editCryptoCurrency)
                .map { !it.isEmpty() && it.toString().toDouble() > 0.0 }
                .distinctUntilChanged()
        // This is only the case if no price provider is selected!
        val scd = Observable.defer {
            val cryptoItem = spinnerCryptoCurrency.selectedItem
                    as? CurrencySpinnerAdapter.CurrencySpinnerAdapterItem
            Observable.just(cryptoItem != null)
        }.distinctUntilChanged()

        Observable.combineLatest(arrayOf(cd, ccd, scd)) { it.all { t -> t == true } }
                .subscribe {
                    val bgTint = ContextCompat.getColor(context,
                            (if (it) R.color.colorAccent else R.color.disabled))
                    btnAdd.supportBackgroundTintList = ColorStateList.valueOf(bgTint)
                    RxView.enabled(btnAdd).accept(it)
                }

        RxView.clicks(btnAdd).subscribe {

            val realAmount = editCurrency.text.toString().toDouble()
            val realCurrency = RealCurrency.values()[spinnerCurrency.selectedItemPosition]
            val cryptoAmount = editCryptoCurrency.text.toString().toDouble()
            val cryptoItem = spinnerCryptoCurrency.selectedItem as CurrencySpinnerAdapter.CurrencySpinnerAdapterItem
            val cryptoCurrency = CryptoCurrency.valueOf(cryptoItem.name)

            val c = Currency(_cryptoCurrency = cryptoCurrency, cryptoAmount = cryptoAmount,
                    _realCurrency = realCurrency, realAmount = realAmount,
                    boughtDate = System.currentTimeMillis())

            currencyManager.addCurrency(c)
            activity.setResult(Activity.RESULT_OK, Intent())
            activity.supportFinishAfterTransition()
        }
    }

    private fun getCurrencyItems(): List<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> {
        return RealCurrency.values().map {
            CurrencySpinnerAdapter.CurrencySpinnerAdapterItem(it.name, it.icon)
        }
    }

    private fun getCryptoCurrencyItems(): List<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> {
        return priceProxy.getSupportedCryptoCurrencies().map {
            CurrencySpinnerAdapter.CurrencySpinnerAdapterItem(it.name, it.icon)
        }
    }

    companion object {

        fun newInstance(): AddCurrencyFragment {
            val fragment = AddCurrencyFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}