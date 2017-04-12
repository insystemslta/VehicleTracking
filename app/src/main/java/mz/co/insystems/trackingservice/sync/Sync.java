package mz.co.insystems.trackingservice.sync;

/**
 * Created by voloide on 9/18/16.
 */
public class Sync {

    public static final String SYNC_OWNER				= "owner";
    public static final String SYNC_VEHICLE				= "vehicle";
    public static final String SYNC_COMMAND				= "command";
    public static final String SYNC_COMMAND_PARTS 		= "command_parts";
    public static final String SYNC_ITEM				= "item";
    public static final String SYNC_PLAN                = "package";
    public static final String SYNC_PLAN_ITEM           = "package_item";
    public static final String SYNC_USER 				= "user";
    public static final String SYNC_USER_VEHICLE 		= "user_vehicle";
    public static final String SYNC_VEHICLE_EXTRA_ITEM 	= "vehicle_extra_item";
    public static final String SYNC_COMMAND_RESPONSE 	= "commandResponses";

    public static final int SYNCED 		 	= -1;
    public static final int NOT_SYNCED 		= 0;

    public static final int FIRST_SYNC 	 	= 1000;
    public static final int UPDATE_SYNC  	= 1001;
    public static final int DELETED_SYNC 	= 1003;
    public static final int VALIDATE_SYNC 	= 1004;
    public static final int FIRST_USER_SYNC = 1005;
    public static final int USER_CREATION 	= 1006;
    public static final int USER_PASS_RESET	= 1007;
    public static final int UPDATE_CHECK 	= 2000;
    public static final int PASSWORD_RESET  = 1008;

    /*
     * Variaveis que especificam o destinatario sa sincronizacao
     */
    public static final int SYNC_TARGET_USER 	= 1;
    public static final int SYNC_TARGET_ADMIN 	= 2;
    public static final int SYNC_TARGET_ALL 	= 3;


}

