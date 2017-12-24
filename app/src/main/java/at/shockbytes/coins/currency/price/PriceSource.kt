package at.shockbytes.coins.currency.price

import io.realm.RealmObject

/**
 * @author Martin Macheiner
 * Date: 24.12.2017.
 */

open class PriceSource(var name: String = "", var icon: Int = 0) : RealmObject()