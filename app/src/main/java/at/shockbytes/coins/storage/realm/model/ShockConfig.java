package at.shockbytes.coins.storage.realm.model;

import io.realm.RealmObject;


public class ShockConfig extends RealmObject {

    private long lastPrimaryKey;

    public ShockConfig() {
        this(0);
    }

    public ShockConfig(long lastPrimaryKey) {
        this.lastPrimaryKey = lastPrimaryKey;
    }

    public long getLastPrimaryKey() {
        long key = lastPrimaryKey;
        lastPrimaryKey ++;
        return key;
    }

}
