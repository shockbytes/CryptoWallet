package at.shockbytes.coins.ui.fragment


import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.coins.R
import at.shockbytes.coins.adapter.BaseAdapter
import at.shockbytes.coins.adapter.CurrencyAdapter
import at.shockbytes.coins.currency.Balance
import at.shockbytes.coins.currency.Currency
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.currency.conversion.CurrencyConversionRates
import at.shockbytes.coins.currency.price.NoPriceProviderSelectedException
import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.ui.activity.DetailActivity
import at.shockbytes.coins.ui.activity.SettingsActivity
import at.shockbytes.coins.ui.fragment.dialog.CashoutDialogFragment
import at.shockbytes.coins.ui.fragment.dialog.RemoveConfirmationDialogFragment
import at.shockbytes.coins.util.AppParams
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        CurrencyAdapter.OnEntryPopupItemSelectedListener, BaseAdapter.OnItemClickListener<Currency> {

    enum class ViewType {
        BALANCE, CASHOUT
    }

    @Inject
    protected lateinit var currencyManager: CurrencyManager

    @Inject
    protected lateinit var preferences: SharedPreferences

    val balanceHeader: View by bindView(R.id.fragment_main_header) // Public so it can be accessed by the MainActivity for showcase view
    private val recyclerView: RecyclerView by bindView(R.id.main_fragment_rv)
    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.fragment_main_swipe_container)
    private val txtCurrent: TextView by bindView(R.id.balance_header_txt_current)
    private val txtInvested: TextView by bindView(R.id.balance_header_txt_invested)
    private val txtDiffPercentage: TextView by bindView(R.id.balance_header_txt_percentage)
    private val imgViewTrend: ImageView by bindView(R.id.balance_header_imgview_trend)
    private val emptyView: View by bindView(R.id.fragment_main_empty_view)
    private val emptyTextView: TextView by bindView(R.id.fragment_main_empty_view_text)
    private val emptyImgView: ImageView by bindView(R.id.fragment_main_empty_view_img)

    private lateinit var viewType: ViewType

    private var adapter: CurrencyAdapter? = null

    private var isViewSetup: Boolean = false

    private var lastBalance: Double = 0.toDouble()

    private var timerDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewType = arguments.getSerializable(ARG_VIEWTYPE) as ViewType
        isViewSetup = false
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()

        if (!isViewSetup) {
            setupViews()
        }

        lastBalance = currencyManager.latestBalance
        loadData()
    }

    override fun onPause() {
        super.onPause()

        timerDisposable?.dispose()
        currencyManager.storeLatestBalance()
    }

    override fun onRefresh() {
        loadData()
    }

    public override fun setupViews() {

        // SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorAccent),
                ContextCompat.getColor(context, R.color.colorPrimaryDark))
        swipeRefreshLayout.isEnabled = viewType != ViewType.CASHOUT

        // Setup empty views
        emptyTextView.setText(if (viewType == ViewType.CASHOUT)
            R.string.empty_indicator_cashout
        else
            R.string.empty_indicator_balance)
        emptyImgView.setImageResource(if (viewType == ViewType.CASHOUT)
            R.drawable.ic_navigation_cashout
        else
            R.drawable.ic_navigation_balance
        )

        // RecyclerView
        adapter = CurrencyAdapter(context, ArrayList(),
                viewType, this)
        adapter?.onItemClickListener = this
        recyclerView.layoutManager = getLayoutManagerForOrientation()
        recyclerView.adapter = adapter

        isViewSetup = true
    }

    override fun onCashout(c: Currency) {
        val fragment = CashoutDialogFragment.newInstance(c.id)
        fragment.setOnCashoutCompletedListener { loadData() }
        fragment.show(fragmentManager, "cashout-fragment")
    }

    override fun onDelete(c: Currency) {
        val amount = "${c.cryptoAmount} ${c.getCryptoCurrency().name}"
        RemoveConfirmationDialogFragment.newInstance(amount)
                .setConfirmationListener {
                    currencyManager.removeCurrency(c)
                    loadData()
                }
                .show(fragmentManager, "remove-confirmation-dialog-fragment")
    }

    override fun onItemClick(t: Currency, v: View) {
        recyclerView.scrollToPosition(adapter?.getLocation(t) ?: 0)
        showDetailFragment(t, v)
    }

    // --------------------------------------------------------------------------------

    private fun showDetailFragment(t: Currency, v: View) {

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                android.support.v4.util.Pair(v.findViewById(R.id.item_currency_cardview),
                        getString(R.string.transition_item_currency_cardview)),
                android.support.v4.util.Pair(v.findViewById(R.id.item_currency_icon),
                        getString(R.string.transition_item_currency_icon)),
                android.support.v4.util.Pair(v.findViewById(R.id.item_currency_txt_amount),
                        getString(R.string.transition_item_currency_crypto_amount)),
                android.support.v4.util.Pair(v.findViewById(R.id.item_currency_divider),
                        getString(R.string.transition_item_currency_divider)),
                android.support.v4.util.Pair(v.findViewById(R.id.item_currency_txt_bought_price),
                        getString(R.string.transition_item_currency_txt_bought_price)),
                android.support.v4.util.Pair(v.findViewById(R.id.item_currency_imgview_arrow),
                        getString(R.string.transition_item_currency_imgview_arrow)),
                android.support.v4.util.Pair(v.findViewById(R.id.item_currency_txt_current_price),
                        getString(R.string.transition_item_currency_txt_current_price)),
                android.support.v4.util.Pair(v.findViewById(R.id.item_currency_txt_diff),
                        getString(R.string.transition_item_currency_txt_diff)))
        startActivity(DetailActivity.newIntent(context, t.id), options.toBundle())
    }

    private fun setupHeader(balance: Balance?) {

        txtCurrent.text = balance?.current?.toString() + " " + currencyManager.localCurrency
        txtInvested.text = balance?.invested?.toString() + " " + currencyManager.localCurrency

        val diff = balance?.percentageDiff ?: 0.0
        val diffColor = if (diff >= 0) R.color.percentage_win else R.color.percentage_loose
        if (context != null) { // Some exceptions occur and I don't know why context is null
            txtDiffPercentage.setTextColor(ContextCompat.getColor(context, diffColor))
        }
        txtDiffPercentage.text = diff.toString() + "%"

        animateTrendArrow(balance?.current ?: lastBalance)
    }

    private fun animateTrendArrow(balance: Double) {

        // Do not animate anything if it has the same value
        if (lastBalance == balance) {
            return
        }

        val rotation = (if (lastBalance > balance) 90 else -90).toFloat()
        imgViewTrend.rotation = rotation

        imgViewTrend.animate().alpha(1f).setDuration(AppParams.trendAnimDuration)
                .withEndAction {
                    imgViewTrend.animate().alpha(0f).duration = AppParams.trendAnimDuration
                }
        lastBalance = balance
    }

    private fun loadData() {

        swipeRefreshLayout.isRefreshing = true

        if (viewType == ViewType.BALANCE) {

            if (preferences.getBoolean(getString(R.string.prefs_key_auto_update), false)) {
                subscribeToPeriodicDataSource()
            } else {
                subscribeToSingleDataSource(currencyManager.ownedCurrencies)
            }

        } else if (viewType == ViewType.CASHOUT) {
            subscribeToSingleDataSource(currencyManager.cashedOutCurrencies)
        }

    }

    private fun subscribeToSingleDataSource(dataSrc: Observable<List<Currency>>) {

        Observable.zip(dataSrc, currencyManager.currencyConversionProvider.getCurrencyConversionRates(),
                BiFunction<List<Currency>, CurrencyConversionRates, Pair<List<Currency>, CurrencyConversionRates>> { c, r -> Pair(c, r) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ pair ->

                    swipeRefreshLayout.isRefreshing = false

                    adapter?.setLocalCurrency(currencyManager.localCurrency, pair.second)
                    adapter?.data = pair.first.toMutableList()

                    val visibility = if (pair.first.isEmpty()) View.VISIBLE else View.GONE
                    emptyView.visibility = visibility

                    // Call in here makes sure, that the balance object is loaded at this point in time
                    setupHeader(currencyManager.balance)

                }) { throwable ->
                    throwable.printStackTrace()
                    handleDataLoadingError(throwable)
                }
    }

    private fun subscribeToPeriodicDataSource() {
        timerDisposable = Observable.interval(0, AppParams.autoUpdateTime,
                TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe { subscribeToSingleDataSource(currencyManager.ownedCurrencies) }
    }

    private fun getLayoutManagerForOrientation(): RecyclerView.LayoutManager {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager(context)
        } else {
            GridLayoutManager(context, 2)
        }
    }

    private fun handleDataLoadingError(throwable: Throwable) {

        timerDisposable?.dispose() // Kill timer if this is a periodic task
        swipeRefreshLayout.isRefreshing = false

        when (throwable) {

            is NoPriceProviderSelectedException -> {
                showSnackbar(getString(R.string.error_no_price_provider),
                        getString(R.string.enable), true,
                        {
                            startActivity(SettingsActivity.newIntent(context),
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity)
                                            .toBundle())
                        })
                // TODO v1.2 -Show something in EmptyView
            }
        // else -> showToast(getString(R.string.error_load_data), showLong = true)
        }

    }

    fun onNewCurrencyEntryAvailable() {
        if (isVisible) {
            loadData()
        }
    }

    companion object {

        private val ARG_VIEWTYPE = "arg_viewtype"

        fun newInstance(viewType: ViewType): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            args.putSerializable(ARG_VIEWTYPE, viewType)
            fragment.arguments = args
            return fragment
        }

    }


}
