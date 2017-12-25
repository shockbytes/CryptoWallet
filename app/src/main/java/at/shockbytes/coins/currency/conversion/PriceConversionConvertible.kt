package at.shockbytes.coins.currency.conversion

import at.shockbytes.coins.currency.RealCurrency

/**
 * @author Martin Macheiner
 * Date: 25.12.2017.
 *
 * Different provider data classes have to implement this interface to be compatible with
 * the PriceManager and PriceProxy classes
 *
 */

interface PriceConversionConvertible {

    fun asPriceConversion(to: RealCurrency): PriceConversion
}