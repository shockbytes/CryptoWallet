package at.shockbytes.coins.currency

import at.shockbytes.coins.util.ResourceManager

/**
 * @author Martin Macheiner
 * Date: 24.12.2017.
 */
class Balance {

    var invested: Double = 0.0
        get() = ResourceManager.roundDouble(field, 2)

    var current: Double = 0.0
        get() = ResourceManager.roundDouble(field, 2)

    var percentageDiff = 0.0
        private set
        get() {
            return if (invested == 0.0) {
                0.0
            } else {
                val diff = current / (invested / 100) - 100
                ResourceManager.roundDouble(diff, 2)
            }
        }

    fun addInvested(price: Double) {
        invested += price
    }

    fun addCurrent(price: Double) {
        current += price
    }

    fun clear() {
        invested = 0.0
        current = 0.0
        percentageDiff = 0.0
    }

}