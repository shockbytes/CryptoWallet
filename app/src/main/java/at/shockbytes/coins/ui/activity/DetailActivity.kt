package at.shockbytes.coins.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.coins.R
import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.ui.fragment.dialog.CashoutDialogFragment
import at.shockbytes.coins.ui.fragment.dialog.RemoveConfirmationDialogFragment
import at.shockbytes.coins.util.ResourceManager
import kotterknife.bindView
import javax.inject.Inject

/**
 * @author Martin Macheiner
 * Date: 27.12.2017.
 */

class DetailActivity : BackNavigableActivity() {

    @Inject
    protected lateinit var currencyManager: CurrencyManager

    private val imgView: ImageView by bindView(R.id.activity_detail_imgview)
    private val txtCryptoAmount: TextView by bindView(R.id.activity_detail_txt_crypto_amount)
    private val txtBoughtPrice: TextView by bindView(R.id.activity_detail_txt_bought)
    private val txtCurrentPrice: TextView by bindView(R.id.activity_detail_txt_current)
    private val txtDiff: TextView by bindView(R.id.activity_detail_txt_diff)
    private val txtBoughtDate: TextView by bindView(R.id.activity_detail_txt_bought_date)
    private val txtSoldDate: TextView by bindView(R.id.activity_detail_txt_sold_date)
    private val imgViewArrow: ImageView by bindView(R.id.activity_detail_imgview_conversion_arrow)
    private val txtPriceSource: TextView by bindView(R.id.activity_detail_txt_price_source)

    private lateinit var c: Currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        c = currencyManager.getCurrencyById(intent.extras.getLong(argId))
        setupViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        menu?.findItem(R.id.menu_detail_cashout)?.isVisible = !c.instanceCashedOut
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menu_detail_cashout -> {
                val fragment = CashoutDialogFragment.newInstance(c.id)
                fragment.setOnCashoutCompletedListener { supportFinishAfterTransition() }
                fragment.show(supportFragmentManager, "cashout-fragment")
            }
            R.id.menu_detail_remove -> {
                val amount = "${c.cryptoAmount} ${c.getCryptoCurrency().name}"
                val dialog = RemoveConfirmationDialogFragment.newInstance(amount)
                        .setConfirmationListener {
                            currencyManager.removeCurrency(c)
                            supportFinishAfterTransition()
                        }
                dialog.show(supportFragmentManager, "remove-confirmation-dialog-fragment")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun backwardAnimation() {
        txtPriceSource.animate().alpha(0f).start()
        imgViewArrow.animate().setStartDelay(0).setDuration(50).rotation(0f).start()
    }

    private fun setupViews() {

        title = c.getCryptoCurrency().fullName

        imgView.setImageResource(c.getCryptoCurrency().icon)
        txtCryptoAmount.text = "${ResourceManager.roundDouble(c.cryptoAmount, 8)} ${c.getCryptoCurrency().name}"

        val boughtPrice = if (c.getRealCurrency() !== currencyManager.localCurrency) {
            // Fall back to realAmount, if conversion not possible
            currencyManager.currencyConversionRates
                    ?.convert(c.realAmount, c.getRealCurrency(), currencyManager.localCurrency)
                    ?: c.realAmount
        } else {
            c.realAmount
        }
        txtBoughtPrice.text = "${ResourceManager.roundDouble(boughtPrice, 2)} ${currencyManager.localCurrency.symbol}"
        txtCurrentPrice.text = "${c.currentPrice} ${currencyManager.localCurrency.symbol}"

        val diff = c.getPricePercentageDiff(boughtPrice)
        val diffColor = if (diff >= 0) R.color.percentage_win else R.color.percentage_loose
        txtDiff.setTextColor(ContextCompat.getColor(this, diffColor))
        txtDiff.text = diff.toString().plus("%")

        txtBoughtDate.text = getString(R.string.detail_bought_date,
                ResourceManager.formatDateOfYear(c.boughtDate))

        txtSoldDate.text = if (c.cashoutDate > 0) getString(R.string.detail_sold_date,
                ResourceManager.formatDateOfYear(c.cashoutDate)) else getString(R.string.detail_sold_date, "---")

        c.priceSource?.name?.let { txtPriceSource.text = getString(R.string.detail_price_provider, it) }
        c.priceSource?.icon?.let {
            txtPriceSource.setCompoundDrawablesWithIntrinsicBounds(0, 0, it, 0)
        }

        if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            imgViewArrow.animate().setStartDelay(200).setDuration(200).rotation(90f).start()
        }
    }

    companion object {

        private val argId = "arg_id"

        fun newIntent(context: Context, id: Long): Intent {
            return Intent(context, DetailActivity::class.java)
                    .putExtra(argId, id)
        }

    }

}