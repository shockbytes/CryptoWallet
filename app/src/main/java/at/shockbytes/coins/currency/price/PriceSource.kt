package at.shockbytes.coins.currency.price

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Martin Macheiner
 * Date: 24.12.2017.
 */

open class PriceSource(@PrimaryKey var name: String = "", var icon: Int = 0) : RealmObject()