package at.shockbytes.coins.ui.fragment


import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.coins.R
import at.shockbytes.coins.adapter.OwnedCurrencyAdapter
import at.shockbytes.coins.currency.Balance
import at.shockbytes.coins.currency.CurrencyManager
import at.shockbytes.coins.currency.OwnedCurrency
import at.shockbytes.coins.dagger.AppComponent
import at.shockbytes.coins.ui.fragment.dialog.CashoutDialogFragment
import at.shockbytes.coins.ui.fragment.dialog.RemoveConfirmationDialogFragment
import at.shockbytes.coins.util.AppParams
import butterknife.BindView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        OwnedCurrencyAdapter.OnEntryPopupItemSelectedListener {

    enum class ViewType {
        BALANCE, CASHOUT
    }

    @Inject
    lateinit var currencyManager: CurrencyManager

    @Inject
    lateinit var preferences: SharedPreferences

    @BindView(R.id.main_fragment_rv)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.fragment_main_swipe_container)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @BindView(R.id.balance_header_txt_current)
    lateinit var txtCurrent: TextView

    @BindView(R.id.balance_header_txt_invested)
    lateinit var txtInvested: TextView

    @BindView(R.id.balance_header_txt_percentage)
    lateinit var txtDiffPercentage: TextView

    // Can't be assigned with just the ButterKnife assignment,
    // will crash if it is not handled that way
    @JvmField
    @BindView(R.id.balance_header_imgview_trend)
    var imgViewTrend: ImageView? = null

    @BindView(R.id.fragment_main_empty_view)
    lateinit var emptyView: View

    @BindView(R.id.fragment_main_empty_view_text)
    lateinit var emptyTextView: TextView

    private lateinit var viewType: ViewType

    private var adapter: OwnedCurrencyAdapter? = null

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

        if (timerDisposable?.isDisposed == false) {
            timerDisposable?.dispose()
        }

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

        emptyTextView.setText(if (viewType == ViewType.CASHOUT)
            R.string.empty_indicator_cashout
        else
            R.string.empty_indicator_balance)

        // RecyclerView
        adapter = OwnedCurrencyAdapter(context, ArrayList(),
                viewType, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        isViewSetup = true
    }

    override fun onCashout(ownedCurrency: OwnedCurrency) {

        val fragment = CashoutDialogFragment.newInstance(ownedCurrency.id)
        fragment.setOnCashoutCompletedListener { loadData() }
        fragment.show(fragmentManager, "cashout-fragment")
    }

    override fun onDelete(ownedCurrency: OwnedCurrency) {
        val amount = "${ownedCurrency.amount} ${ownedCurrency.cryptoCurrency.name}"
        val dialog = RemoveConfirmationDialogFragment.newInstance(amount)
                .setConfirmationListener {
                    currencyManager.removeCurrency(ownedCurrency)
                    loadData()
                }
        dialog.show(fragmentManager, "remove_confirmation_dialog_fragment")
    }

    private fun setupHeader(balance: Balance) {

        txtCurrent.text = balance.current.toString() + " " + currencyManager.localCurrency
        txtInvested.text = balance.invested.toString() + " " + currencyManager.localCurrency

        val diff = balance.percentageDiff
        val diffColor = if (diff >= 0) R.color.percentage_win else R.color.percentage_loose
        txtDiffPercentage.setTextColor(ContextCompat.getColor(context, diffColor))
        txtDiffPercentage.text = diff.toString() + "%"

        animateTrendArrow(balance.current)
    }

    private fun animateTrendArrow(balance: Double) {

        // Do not animate anything if it has the same value
        if (lastBalance == balance) {
            return
        }

        val rotation = (if (lastBalance > balance) 90 else -90).toFloat()
        imgViewTrend?.rotation = rotation

        imgViewTrend?.animate()?.alpha(1f)?.setDuration(AppParams.trendAnimDuration)
                ?.withEndAction {
                    imgViewTrend?.animate()?.alpha(0f)?.duration = AppParams.trendAnimDuration
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
            subscribeToSingleDataSource(currencyManager.cashedoutCurrencies)
        }

    }

    private fun subscribeToSingleDataSource(dataSource: Observable<List<OwnedCurrency>>) {

        dataSource.subscribe({ ownedCurrencies ->
            swipeRefreshLayout.isRefreshing = false

            adapter?.setLocalCurrency(currencyManager.localCurrency,
                    currencyManager.currencyConversionRates)
            adapter?.data = ownedCurrencies

            val visibility = if (ownedCurrencies.isEmpty()) View.VISIBLE else View.GONE
            emptyView.visibility = visibility

            // Call in here makes sure, that the balance object is loaded at this point in time
            setupHeader(currencyManager.balance)
        }) { throwable -> throwable.printStackTrace() }
    }

    private fun subscribeToPeriodicDataSource() {

        timerDisposable = Observable.interval(0, AppParams.autoUpdateTime,
                TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe { subscribeToSingleDataSource(currencyManager.ownedCurrencies) }
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
