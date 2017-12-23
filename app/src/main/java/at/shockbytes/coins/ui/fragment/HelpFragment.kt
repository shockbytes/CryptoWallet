package at.shockbytes.coins.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.shockbytes.coins.R
import at.shockbytes.coins.dagger.AppComponent

/**
 * @author Martin Macheiner
 * Date: 23.12.2017.
 */
class HelpFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_help, container, false)
    }

    override fun setupViews() {
        // TODO
    }

    override fun injectToGraph(appComponent: AppComponent) {
        // Do nothing
    }

    companion object {

        fun newInstance(): HelpFragment {
            val fragment = HelpFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

    }

}