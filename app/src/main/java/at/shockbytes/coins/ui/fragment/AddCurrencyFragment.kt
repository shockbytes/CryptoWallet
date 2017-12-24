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
import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.util.CoinUtils
import butterknife.BindView
import butterknife.OnClick
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 23.12.2017.
 */
class AddCurrencyFragment : BaseFragment() {

    @Inject
    lateinit var currencyManager: CurrencyManager

    @BindView(R.id.fragment_dialog_add_currency_edit_cryptocurrency)
    lateinit var editCryptoCurrency: TextInputEditText

    @BindView(R.id.fragment_dialog_add_currency_edit_currency)
    lateinit var editCurrency: TextInputEditText

    @BindView(R.id.fragment_dialog_add_currency_til_cryptocurrency)
    lateinit var tilCryptoCurrency: TextInputLayout

    @BindView(R.id.fragment_dialog_add_currency_til_currency)
    lateinit var tilCurrency: TextInputLayout

    @BindView(R.id.fragment_dialog_add_currency_spinner_cryptocurrency)
    lateinit var spinnerCryptoCurrency: Spinner

    @BindView(R.id.fragment_dialog_add_currency_spinner_currency)
    lateinit var spinnerCurrency: Spinner


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

        spinnerCryptoCurrency.adapter = CurrencySpinnerAdapter(context, getCryptoCurrencyItems())
        spinnerCryptoCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val item = spinnerCryptoCurrency.selectedItem as CurrencySpinnerAdapter.CurrencySpinnerAdapterItem
                tilCryptoCurrency.hint = item.text
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        spinnerCurrency.adapter = CurrencySpinnerAdapter(context, getCurrencyItems())
        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val item = spinnerCurrency.selectedItem as CurrencySpinnerAdapter.CurrencySpinnerAdapterItem
                tilCurrency.hint = item.text
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

        val boughtPrice = java.lang.Double.parseDouble(strbp)
        val boughtCurrency = RealCurrency.values()[spinnerCurrency.selectedItemPosition]
        val amount = java.lang.Double.parseDouble(strcc)
        val currency = CryptoCurrency.values()[spinnerCryptoCurrency.selectedItemPosition]

        val c = Currency(_cryptoCurrency = currency, cryptoAmount = amount,
                _realCurrency = boughtCurrency, realAmount = boughtPrice)

        currencyManager.addCurrency(c)
        activity.setResult(Activity.RESULT_OK, Intent())
        activity.supportFinishAfterTransition()
    }

    private fun getCurrencyItems(): List<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> {
        return RealCurrency.values().map {
            CurrencySpinnerAdapter.CurrencySpinnerAdapterItem(it.name,
                    CoinUtils.getResourceForCurrency(it))
        }
    }

    private fun getCryptoCurrencyItems(): List<CurrencySpinnerAdapter.CurrencySpinnerAdapterItem> {
        return CryptoCurrency.values().map {
            CurrencySpinnerAdapter.CurrencySpinnerAdapterItem(it.name,
                    CoinUtils.getResourceForCryptoCurrency(it))
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