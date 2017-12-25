package at.shockbytes.coins.storage.realm.model

import io.realm.RealmObject


open class ShockConfig(private var lastPrimaryKey: Long = 0) : RealmObject() {

    fun getLastPrimaryKey(): Long {
        val key = lastPrimaryKey
        lastPrimaryKey++
        return key
    }

}
