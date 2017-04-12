package mz.co.insystems.trackingservice.sync;

/**
 * Created by voloide on 9/16/16.
 */
public class Url {



    /*
     * API_VERSION
     * syncServiceUrl
     * BASE_URL
     */
    private static String API_VERSION = "v1.0";
    private static final String syncServiceUrl = "http://tracking.insystems.co.mz/"+API_VERSION+"/index.php/";
    private static final String baseUrl = "http://www.insystems.co.mz/";


    /*
     * GENERAL SYNC URL's
     */
    public static final String SYNC_ALL 					= syncServiceUrl +"synchronize/syncall";
    public static final String CHECK_FOR_UPDATES 			= syncServiceUrl +"synchronize/checkForUpdates";
    public static final String GET_UPDATE_HEADERS 			= syncServiceUrl +"synchronize/getupdates";
    public static final String SET_UPDATE_AS_APLLYED 		= syncServiceUrl +"synchronize/setAsUpdated";
    public static final String SYNC_SINGLE_USER_DATA		= syncServiceUrl +"synchronize/getSigleUserData";

    /*
     * GET_IMAGE URL
     */
    public static final String VEHICLE_IMAGE_GET 			= baseUrl +"public/vehicles/";


    /*
     * GET's URL's
     */
    public static final String OWNER_GET 					= syncServiceUrl +"owner/get";
    public static final String PACKAGE_GET 					= syncServiceUrl +"package/get";
    public static final String ITEM_GET 					= syncServiceUrl +"item/get";
    public static final String COMMAND_GET 					= syncServiceUrl +"command/get";
    public static final String USER_GET 					= syncServiceUrl +"user/get";
    public static final String VEHICLE_GET					= syncServiceUrl +"vehicle/get";
    public static final String COMMAND_PARTS_GET 			= syncServiceUrl +"command/getCommandPart";
    public static final String PACKAGE_ITEM_GET 			= syncServiceUrl +"plan/getPackageItems";
    public static final String VEHICLE_EXTRA_ITEM_GET 		= syncServiceUrl +"vehicle/getExtraItem";
    public static final String COMMAND_RESPONSE 			= syncServiceUrl +"command/getCommandResponse";;
    /*
     * GET_ALL's URL's
     */
    public static final String PACKAGE_GETALL_URL 			= syncServiceUrl +"package/getall";
    public static final String ITEM_GETALL_URL 				= syncServiceUrl +"item/getall";
    public static final String COMMAND_GETALL_URL 			= syncServiceUrl +"command/getall";
    public static final String USER_GETALL_URL 				= syncServiceUrl +"user/getall";
    public static final String COMMAND_GETALLPARTS_URL 		= syncServiceUrl +"command/getallparts";


    /*
     * CREATE URL's
     */
    public static final String OWNER_CREATE_URL 			= syncServiceUrl +"index.php/owner/create";
    public static final String PACKAGE_CREATE_URL 			= syncServiceUrl +"plan/create";
    public static final String USER_CREATE_URL 				= syncServiceUrl +"user/create";


    /*
     * GET_BY URL's
     */
    public static final String VEHICLE_GETBYUSER 			= syncServiceUrl +"vehicle/getbyuser";
    public static final String USER_GET_BY_CREDENTIALS 		= syncServiceUrl +"user/getByCredentials";
    public static final String USER_GET_BY_CONTACT			= syncServiceUrl +"user/getByLoginNumber";
    public static final String USER_GET_BY_ID			    = syncServiceUrl +"user/get";

    /*
     * USER ACCOUNTS MANAGEMENT URL's
     */
    public static final String USER_GETNEW_ID 				= syncServiceUrl +"user/generatenewid";
    public static final String USER_GETAUTHORIZATION_URL 	= syncServiceUrl +"user/getauthorization";
    public static final String USER_VALIDATE 				= syncServiceUrl +"user/validate";
    public static final String USER_PASSWORD_RESET_URL 		= syncServiceUrl +"user/resetPassword";
    public static final String VALIDADE_USER_CONTACT		= syncServiceUrl +"user/validateLoginNumber";



}
