package bts.sio.compterendu.helper;

import android.provider.BaseColumns;

/**
 * Created by TI-tygangsta on 18/04/2017.
 */

public final class CRReaderContract {
    private CRReaderContract() {
    }
    /* Inner class that defines the table contents */
    public static class CREntry implements BaseColumns {
        public static final String TABLE_NAME = "User";
        public static final String COLUMN_USER_USERNAME = "username";
        public static final String COLUMN_USER_SALT = "salt";
        public static final String COLUMN_USER_FONCTION = "fonction";
        public static final String COLUMN_USER_CLEARPASS = "clearpass";
        /**
        public static final String COLUMN_USER_FONCTION = "fonction";
         **/

    }
}