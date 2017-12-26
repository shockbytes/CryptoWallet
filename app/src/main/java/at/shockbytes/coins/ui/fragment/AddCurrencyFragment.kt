package at.shockbytes.coins.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
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
import butterknife.BindView
import butterknife.OnClick
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

    @BindView(R.id.fragment_dialog_add_currency_edit_cryptocurrency)
    protected lateinit var editCryptoCurrency: TextInputEditText

    @BindView(R.id.fragment_dialog_add_currency_edit_currency)
    protected lateinit var editCurrency: TextInputEditText

    @BindView(R.id.fragment_dialog_add_currency_til_cryptocurrency)
    protected lateinit var tilCryptoCurrency: TextInputLayout

    @BindView(R.id.fragment_dialog_add_currency_til_currency)
    protected lateinit var tilCurrency: TextInputLayout

    @BindView(R.id.fragment_dialog_add_currency_spinner_cryptocurrency)
    protected lateinit var spinnerCryptoCurrency: Spinner

    @BindView(R.id.fragment_dialog_add_currency_spinner_currency)
    protected lateinit var spinnerCurrency: Spinner


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
                tilCurrency.hint = item.text
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        // Select the local currency as default value
        spinnerCurrency.setSelection(currencyManager.localCurrency.ordinal, true)

        spinnerCryptoCurrency.adapter = CurrencySpinnerAdapter(context, getCryptoCurrencyItems())
        spinnerCryptoCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val item = spinnerCryptoCurrency.selectedItem as CurrencySpinnerAdapter.CurrencySpinnerAdapterItem
                tilCryptoCurrency.hint = item.text
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

    }

    @OnClick(R.id.fragment_dialog_add_currency_btn_save)
    fun onClickSave() {

        val strbp = editCurrency.text.toString()
        val strcc = editCryptoCurrency.text.toString()

        if (strbp.isEmpty() or strcc.isEmpty()) {
            showSnackbar(getString(R.string.error_add_currency))
            return
        }

        val realAmount = strbp.toDouble()
        val realCurrency = RealCurrency.values()[spinnerCurrency.selectedItemPosition]
        val cryptoAmount = strcc.toDouble()
        val cryptoCurrency = CryptoCurrency.values()[spinnerCryptoCurrency.selectedItemPosition]

        if (realAmount <= 0.0 || cryptoAmount <= 0.0) {
            showSnackbar(getString(R.string.error_add_currency_bigger_than_zero))
            return
        }

        val c = Currency(_cryptoCurrency = cryptoCurrency, cryptoAmount = cryptoAmount,
                _realCurrency = realCurrency, realAmount = realAmount)

        currencyManager.addCurrency(c)
        activity.setResult(Activity.RESULT_OK, Intent())
        activity.supportFinishAfterTransition()
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