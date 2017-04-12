package mz.co.insystems.trackingservice.db;

/**
 * Created by voloide on 9/15/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class InSystemsDB extends SQLiteOpenHelper{

    /*
     * Table pessoa fields***************************************
     */
    public static final String COLUMN_PESSOA_NAME 	        = "name";
    public static final String COLUMN_PESSOA_SURNAME        = "surname";
    public static final String COLUMN_PESSOA_CONTACT_ID 	= "contact_id";

    /*
     * Table contacto fields***************************************
     */
    public static final String TABLE_CONTACT			        = "contacto";
    public static final String COLUMN_CONTACT_ID 			    = "id";
    public static final String COLUMN_CONTACT_EMAIL 			= "email";
    public static final String COLUMN_CONTACT_PHONE_NEMBER_1 	= "numero_1";
    public static final String COLUMN_CONTACT_PHONE_NEMBER_2 	= "numero_2";

    /*
     * Table owner fields***************************************
     */
    public static final String TABLE_OWNER 			= "owner";
    public static final String COLUMN_ID 			= "id";
    public static final String TYPE 				= "type";
    public static final String OWNER_PESSOA_ID 		= "pessoa_id";

	/*
	 * Table vehicles fields************************************
	 */

    public static final String TABLE_VEHICLE				= "vehicle";
    public static final String V_ID 						= "id";
    public static final String COLUMN_MAKE 					= "make";
    public static final String COLUMN_MODEL 				= "model";
    public static final String COLUMN_NR_PLATE 				= "nr_plate";
    public static final String COLUMN_IMEI                  = "emei";
    public static final String COLUMN_REGISTATION_DATE 		= "registration_date";
    public static final String COLUMN_OWNER_ID 				= "owner_id";
    public static final String COLUMN_CALL_NUMBER 			= "call_number";
    public static final String COLUMN_TYPE					= "type";
    public static final String COLUMN_IMAGEURI 				= "imageUri";
    public static final String COLUMN_PACKEGE 				= "packegeId";
    public static final String COLUMN_VEHICLE_STATE 		= "state";
    public static final String COLUMN_VEHICLE_PASSWORD 		= "password";

    /*
     * TABLE PACKAGE FIELDS****************************************************
     */
    public static final String TABLE_PLAN                   = "package";
    public static final String COLUMN_PLAN_ID               = "id";
    public static final String COLUMN_PLAN_NAME             = "name";



    /*
     * TABLE ITEMS FIELDS****************************************************
     */
    public static final String TABLE_ITEM				= "item";
    public static final String COLUMN_ITEM_ID 			= "id";
    public static final String COLUMN_ITEM_NAME 		= "name";
    public static final String COLUMN_ITEM_CODE 		= "code";


    /*
     * TABLE PACKAGES_ITEMS FIELDS****************************************************
     */
    public static final String TABLE_PACKAGE_ITEM			= "package_item";
    public static final String COLUMN_PACK_ITEM_ID 			= "id";
    public static final String COLUMN_PACKAGE_ITEM_ID 		= "package_id";
    public static final String COLUMN_ITEM_PACKAGE_ID 		= "item_id";

    /*
     * TABLE USER FIELDS****************************************************
     */
    public static final String TABLE_USER					    = "user";
    public static final String COLUMN_USER_ID 				    = "id";
    public static final String COLUMN_USER_LOGIN_NUMBER		    = "login_number";
    public static final String COLUMN_USER_PASSWORD 		    = "password";
    public static final String COLUMN_USER_STATE 			    = "state";
    public static final String COLUMN_USER_TYPE 			    = "type_id";
    public static final String COLUMN_USER_PASSWORD_RESET_CODE 	= "reset_code";
    public static final String COLUMN_USER_OLD_PASSWORD		    = "old_password";
    public static final String COLUMN_USER_LAST_RESET		    = "last_reset";

    /*
     * TABLE COMMAND FIELDS****************************************************
     */
    public static final String TABLE_COMMAND				= "command";
    public static final String COLUMN_COMMAND_ID 			= "id";
    public static final String COLUMN_COMMAND_NAME 			= "name";
    public static final String COLUMN_COMMAND_CODE 			= "code";
    public static final String COLUMN_COMMAND_ITEM_CODE 	= "item_code";
    public static final String COLUMN_COMMAND_ICON 			= "icon";
    public static final String COLUMN_COMMAND_TARGET		= "target";
    /*
     * TABLE COMMAND_PARTS FIELDS****************************************************
     */
    public static final String TABLE_COMMAND_PARTS				= "COMMAND_PARTS";
    public static final String COLUMN_COMMAND_PARTS_ID 			= "id";
    public static final String COLUMN_COMMAND_PARTS_COMMAND_ID 	= "COMMAND_ID";
    public static final String COLUMN_COMMAND_PARTS_PART 		= "PART";
    public static final String COLUMN_COMMAND_PARTS_POSITION 	= "POSITION";
    public static final String COLUMN_COMMAND_PARTS_TYPE	 	= "TYPE";



    /*
     * TABLE USER_VEHICLE FIELDS****************************************************
     */
    public static final String TABLE_USER_VEHICLE		= "user_vehicle";
    public static final String COLUMN_UV_ID 			= "id";
    public static final String COLUMN_UV_USER_ID 		= "user_id";
    public static final String COLUMN_UV_VEHICLE_ID 	= "vehicle_id";

    /*
     * TABLE PACKAGES_ITEMS FIELDS****************************************************
     */
    public static final String TABLE_VEHICLE_EXTRA_ITEM					= "vehicle_extra_item";
    public static final String COLUMN_VEHICLE_EXTRA_ITEM_ID 			= "id";
    public static final String COLUMN_VEHICLE_EXTRA_ITEM_VEHICLE_ID 	= "vehicle_id";
    public static final String COLUMN_VEHICLE_EXTRA_ITEM_ITEM_ID 		= "item_id";

    /*
     * TABLE COMMANDRESPONSE FIELDS****************************************************
     */
    public static final String TABLE_COMMAND_RESPONSE				= "commandresponse";
    public static final String COLUMN_COMMAND_RESPONSE_ID 			= "id";
    public static final String COLUMN_COMMAND_RESPONSE_HEADER 		= "header";
    public static final String COLUMN_COMMAND_RESPONSE_TITLE 		= "title";
    public static final String COLUMN_COMMAND_RESPONSE_DESCRIPTION 	= "description";


    /*
     * TABLE CREDIT FIELDS****************************************************
     */
    public static final String TABLE_CREDIT				= "credit";
    public static final String COLUMN_CREDIT_ID 		= "id";
    public static final String COLUMN_CREDIT_CODE 		= "code";
    public static final String COLUMN_CREDIT_STATUS 	= "status";
    public static final String COLUMN_CREDIT_VEHICLE_ID	= "vehicle_id";




    /*
     * TABLE_CONTACT_CREATE sql statement
     */
    private static final String TABLE_CONTACT_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONTACT +" ("
            + COLUMN_CONTACT_ID+				" integer primary key, "
            + COLUMN_CONTACT_EMAIL+			    " text, "
            + COLUMN_CONTACT_PHONE_NEMBER_1+	" text not null, "
            + COLUMN_CONTACT_PHONE_NEMBER_2 + 	" text);";

    /*
     * TABLE_CREDIT_CREATE sql statement
     */
    private static final String TABLE_CREDIT_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CREDIT +" ("
            + COLUMN_CREDIT_ID+				" integer primary key, "
            + COLUMN_CREDIT_CODE+			" text not null, "
            + COLUMN_CREDIT_STATUS+			" text not null, "
            + COLUMN_CREDIT_VEHICLE_ID + 	" integer);";


    /*
     * TABLE_PACKAGE_CREATE sql statement
     */
    private static final String TABLE_PACKAGE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PLAN +" ("
            + COLUMN_PLAN_ID +			" integer primary key, "
            + COLUMN_PLAN_NAME + 		" text not null);";


    /*
     * TABLE_ITEMS_CREATE sql statement
     */
    private static final String TABLE_ITEMS_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ITEM +" ("
            + COLUMN_ITEM_ID+			" integer primary key, "
            + COLUMN_ITEM_CODE+			" text not null, "
            + COLUMN_ITEM_NAME + 		" text not null);";

    /*
     * TABLE_PACKAGES_ITEMS_CREATE sql statement
     */
    private static final String TABLE_PACKAGES_ITEMS_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PACKAGE_ITEM +" ("
            + COLUMN_PACK_ITEM_ID+			" integer primary key, "
            + COLUMN_PACKAGE_ITEM_ID+		" integer, "
            + COLUMN_ITEM_PACKAGE_ID + 		" integer);";

    /*
     * TABLE_USER_CREATE sql statement
     */

    private static final String TABLE_USER_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USER +" ("
            + COLUMN_USER_ID+					" integer primary key, "
            + COLUMN_PESSOA_NAME + 		        " text not null, "
            + COLUMN_PESSOA_SURNAME + 		    " text not null, "
            + COLUMN_USER_LOGIN_NUMBER + 		" text not null, "
            + COLUMN_USER_PASSWORD + 			" text not null, "
            + COLUMN_USER_STATE + 				" integer, "
            + COLUMN_USER_TYPE + 				" integer, "
            + COLUMN_PESSOA_CONTACT_ID + 		" integer, "
            + COLUMN_USER_PASSWORD_RESET_CODE + " text, "
            + COLUMN_USER_OLD_PASSWORD + 		" text, "
            + COLUMN_USER_LAST_RESET + 			" text);";

    /*
     * TABLE_OWNER_CREATE sql statement
     */
    private static final String TABLE_OWNER_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_OWNER +" ("
            + COLUMN_ID+				        " integer primary key, "
            + COLUMN_PESSOA_NAME + 		        " text not null, "
            + COLUMN_PESSOA_SURNAME + 		    " text not null, "
            + COLUMN_PESSOA_CONTACT_ID + 		" integer, "
            + TYPE + 					        " integer not null);";


    private static final String TABLE_VEHICLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_VEHICLE +" ("
            + V_ID+						" integer primary key, "
            + COLUMN_MAKE + 			" text not null, "
            + COLUMN_MODEL + 			" text not null, "
            + COLUMN_NR_PLATE + 		" text, "
            + COLUMN_IMEI + 			" text, "
            + COLUMN_REGISTATION_DATE + " text, "
            + COLUMN_OWNER_ID + 		" integer, "
            + COLUMN_TYPE + 			" integer, "
            + COLUMN_CALL_NUMBER + 		" integer, "
            + COLUMN_IMAGEURI + 		" text, "
            + COLUMN_PACKEGE + 			" integer, "
            + COLUMN_VEHICLE_STATE + 	" integer, "
            + COLUMN_VEHICLE_PASSWORD + " text);";

	/*
	 * TABLE_PACKAGE_CREATE sql statement
	 */

    private static final String TABLE_VEHICLE_EXTRA_ITEM_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_VEHICLE_EXTRA_ITEM +" ("
            + COLUMN_VEHICLE_EXTRA_ITEM_ID+				" integer primary key, "
            + COLUMN_VEHICLE_EXTRA_ITEM_VEHICLE_ID+		" integer, "
            + COLUMN_VEHICLE_EXTRA_ITEM_ITEM_ID + 		" integer);";

    /*
     * TABLE_COMMAND_CREATE sql statement
     */
    private static final String TABLE_COMMAND_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COMMAND +" ("
            + COLUMN_COMMAND_ID+			" integer primary key, "
            + COLUMN_COMMAND_NAME+			" text not null, "
            + COLUMN_COMMAND_CODE+			" text not null, "
            + COLUMN_COMMAND_ITEM_CODE+		" text not null, "
            + COLUMN_COMMAND_ICON+			" text not null, "
            + COLUMN_COMMAND_TARGET + 		" integer not null);";

    /*
     * TABLE_COMMAND_CREATE sql statement
     */
    private static final String TABLE_COMMAND_PARTS_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COMMAND_PARTS +" ("
            + COLUMN_COMMAND_PARTS_ID+			" integer primary key, "
            + COLUMN_COMMAND_PARTS_COMMAND_ID+	" integer not null, "
            + COLUMN_COMMAND_PARTS_PART+		" text not null, "
            + COLUMN_COMMAND_PARTS_POSITION + 	" integer not null, "
            + COLUMN_COMMAND_PARTS_TYPE + 		" text not null);";


    /*
     * TABLE_PACKAGES_ITEMS_CREATE sql statement
     */
    private static final String TABLE_USER_VEHICLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USER_VEHICLE +" ("
            + COLUMN_UV_ID+				" integer primary key autoincrement, "
            + COLUMN_UV_USER_ID+		" integer, "
            + COLUMN_UV_VEHICLE_ID + 	" integer);";

    /*
     * TABLE_COMMAND_RESPONSE sql statement
     */
    private static final String TABLE_COMMAND_RESPONSE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COMMAND_RESPONSE +" ("
            + COLUMN_COMMAND_RESPONSE_ID+				" integer primary key, "
            + COLUMN_COMMAND_RESPONSE_HEADER+			" text not null, "
            + COLUMN_COMMAND_RESPONSE_TITLE+			" text not null, "
            + COLUMN_COMMAND_RESPONSE_DESCRIPTION + 	" text not null);";


    /*
     * DATABASE NAME AND VERSION FIELDS****************************************************
     */
    public static final String DATABESE_NAME = "insystems.db";
    public static final int DATABESE_VERSION = 14;

    public InSystemsDB(Context context) {
        super(context, DATABESE_NAME, null, DATABESE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            db.execSQL(TABLE_OWNER_CREATE);
            db.execSQL(TABLE_VEHICLE_CREATE);
            db.execSQL(TABLE_USER_CREATE);
            db.execSQL(TABLE_PACKAGES_ITEMS_CREATE);
            db.execSQL(TABLE_ITEMS_CREATE);
            db.execSQL(TABLE_PACKAGE_CREATE);
            db.execSQL(TABLE_COMMAND_CREATE);
            db.execSQL(TABLE_USER_VEHICLE_CREATE);
            db.execSQL(TABLE_COMMAND_PARTS_CREATE);
            db.execSQL(TABLE_VEHICLE_EXTRA_ITEM_CREATE);
            db.execSQL(TABLE_COMMAND_RESPONSE_CREATE);
            db.execSQL(TABLE_CREDIT_CREATE);
            db.execSQL(TABLE_CONTACT_CREATE);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

            db.execSQL("DROP TABLE IF EXISTS "+TABLE_OWNER);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_VEHICLE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ITEM);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PLAN);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_PACKAGE_ITEM);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_COMMAND);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER_VEHICLE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_COMMAND_PARTS);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_VEHICLE_EXTRA_ITEM);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_COMMAND_RESPONSE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CREDIT);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACT);


            onCreate(db);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

            db.execSQL("DROP TABLE IF EXISTS "+TABLE_OWNER);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_VEHICLE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ITEM);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PLAN);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_PACKAGE_ITEM);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_COMMAND);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER_VEHICLE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_COMMAND_PARTS);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_VEHICLE_EXTRA_ITEM);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_COMMAND_RESPONSE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CREDIT);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CONTACT);

            onCreate(db);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

}
