package at.shockbytes.coins.storage.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * @author Martin Macheiner
 * Date: 25.06.2017.
 */

class CryptoWatcherRealmMigration : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {

    }

    companion object {

        val v1_0_scheme = 0.toLong()
    }

}
