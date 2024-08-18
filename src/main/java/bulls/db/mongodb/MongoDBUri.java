package bulls.db.mongodb;

import bulls.log.DefaultLogger;

public enum MongoDBUri {
    Instance;

    private final String DB_URI_MM_LIVE = "mongodb://siteRootAdmin:mmxla1!@172.30.222.38:4511/?authSource=admin&readPreference=primary&replicaSet=mm_live";
    private final String DB_URI_MM_LIVE_BACKUP = "mongodb://172.28.203.99:4512/?readPreference=primary&replicaSet=mm_live";
    private final String DB_URI_MM_LIVE_HISTORY = "mongodb://172.28.203.99:4521/?readPreference=primary&replicaSet=mm_live_history";
    private final String DB_URI_MM_TEST = "mongodb://172.30.222.38:4611";

    private String uri = "mongodb://172.30.222.38:4611";

    MongoDBUri() {

    }

    public void setUri(String uri) {
        if (uri == null || uri.trim().equals(""))
            return;

        DefaultLogger.logger.info("MongoDBUri setURI {} -> {}", this.uri, uri);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setLiveUri() {
        setUri(DB_URI_MM_LIVE);
    }

    public void setTestUri() {
        setUri(DB_URI_MM_TEST);
    }

    public String getLiveUri() {
        return DB_URI_MM_LIVE;
    }

    public String getTestUri() {
        return DB_URI_MM_TEST;
    }

    public String getLiveHistoryUri() {
        return DB_URI_MM_LIVE_HISTORY;
    }

    public String getBackupUri() {
        return DB_URI_MM_LIVE_BACKUP;
    }
}
