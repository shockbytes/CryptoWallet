package at.shockbytes.coins.storage;

import android.support.annotation.NonNull;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * @author Martin Macheiner
 *         Date: 25.06.2017.
 */

public class CoinsRealmMigration implements RealmMigration {

    public static final int ORIGINAL_SCHEME = 0;

    public static final int CURRENT_SCHEME = 1;

    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion == ORIGINAL_SCHEME) {
            migrateCashout(schema);
            oldVersion++;
        }
    }

    private void migrateCashout(RealmSchema schema) {
        schema.get("OwnedCurrency")
                .addField("isCashedOut", boolean.class);
    }


}
