package mz.co.insystems.trackingservice.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mz.co.insystems.trackingservice.db.InSystemsDB;
import mz.co.insystems.trackingservice.model.Plan;
import mz.co.insystems.trackingservice.model.PlanItem;

/**
 * Created by voloide on 9/15/16.
 */
public class PlanItemDAO {

    private SQLiteDatabase database;
    private String[] columns = {InSystemsDB.COLUMN_ITEM_ID,
            InSystemsDB.COLUMN_ITEM_CODE,
            InSystemsDB.COLUMN_ITEM_NAME};

    private String[] planItemsColumns = {InSystemsDB.COLUMN_PACK_ITEM_ID,
            InSystemsDB.COLUMN_PACKAGE_ITEM_ID,
            InSystemsDB.COLUMN_ITEM_PACKAGE_ID};


    private InSystemsDB inSystemsDB;
    private static final String TAG                     = "PlanItemDAO";

    public PlanItemDAO(Context context){
        inSystemsDB = new InSystemsDB(context);
    }

    public void open() throws SQLException {
        database = inSystemsDB.getWritableDatabase();
    }


    public boolean isOpened(){
        if (database.isOpen()) {
            return true;
        }else {
            return false;
        }
    }

    public void close(){
        database.close();
    }

    public PlanItem create (PlanItem item){

        PlanItem planItem = new PlanItem();

        long insertedId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_ITEM_NAME, item.getName());
            values.put(InSystemsDB.COLUMN_ITEM_CODE, item.getCode());

            insertedId = database.insert(InSystemsDB.TABLE_ITEM, null, values);
            Log.d(TAG, "Created Item "+ insertedId);


        } catch (Exception e) {
            e.printStackTrace();
        }
        if (insertedId > 0) {
            Cursor cursor = database.query(InSystemsDB.TABLE_ITEM, columns, InSystemsDB.COLUMN_ITEM_ID + " = " + item.getId(), null, null, null, null);
            cursor.moveToFirst();

            planItem.setId(cursor.getInt(0));
            planItem.setName(cursor.getString(1));

            cursor.close();
            return planItem;
        }else {
            return null;
        }

    }

    public boolean delete(PlanItem item){
        long deleted = database.delete(InSystemsDB.TABLE_ITEM, InSystemsDB.COLUMN_ITEM_ID + " = " + item.getId(), null);
        if (deleted > 0) {
            return true;
        }else {
            return false;
        }
    }

    public List<PlanItem> getAll(){
        List<PlanItem> planItemList = new ArrayList<PlanItem>();

        Cursor cursor = database.query(InSystemsDB.TABLE_ITEM, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            do {
                PlanItem planItem = new PlanItem();
                planItem.setId(cursor.getInt(0));
                planItem.setCode(cursor.getString(1));
                planItem.setName(cursor.getString(2));
                planItemList.add(planItem);
            } while (cursor.moveToNext());
            cursor.close();
            return planItemList;
        }else {
            return null;
        }
    }

    public PlanItem get(PlanItem item){
        Cursor cursor = null;
        try {
            cursor = database.query(InSystemsDB.TABLE_ITEM, columns, InSystemsDB.COLUMN_ITEM_ID + " = " + item.getId(), null, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            item.setId(cursor.getInt(0));
            item.setCode(cursor.getString(1));
            item.setName(cursor.getString(2));

            return item;
        }else {
            return null;
        }
    }

    public PlanItem update (PlanItem item){
        PlanItem planItem = new PlanItem();
        long updated = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_ITEM_NAME, item.getName());
            values.put(InSystemsDB.COLUMN_ITEM_CODE, item.getCode());

            updated = database.update(InSystemsDB.TABLE_ITEM, values, InSystemsDB.COLUMN_ITEM_ID + " = " + item.getId(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (updated > 0) {
            Cursor cursor = database.query(InSystemsDB.TABLE_ITEM, columns, InSystemsDB.COLUMN_ITEM_ID + " = " + item.getId(), null, null, null, null);
            cursor.moveToFirst();

            planItem.setId(cursor.getInt(0));
            planItem.setName(cursor.getString(1));
            cursor.close();
            return planItem;
        }else {
            return null;
        }
    }

    public PlanItem getItemByName(PlanItem item) {
        Cursor cursor = database.query(InSystemsDB.TABLE_ITEM, columns, InSystemsDB.COLUMN_ITEM_NAME+ " = " + "'" + item.getName() + "'", null, null, null, null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            PlanItem planItem= new PlanItem();
            planItem.setId(cursor.getInt(0));
            planItem.setName(cursor.getString(1));

            return planItem;
        }else {
            return null;
        }
    }

    public void addItemToPlan(int id, int planId, int itemId){
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_PACK_ITEM_ID, 	id);
            values.put(InSystemsDB.COLUMN_PACKAGE_ITEM_ID, 	planId);
            values.put(InSystemsDB.COLUMN_ITEM_PACKAGE_ID, 	itemId);

            long inserted = database.insert(InSystemsDB.TABLE_PACKAGE_ITEM, null, values);
            Log.d(TAG, "addItemToPlan "+ inserted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PlanItem> getPlanItemsByPlan(Plan plan, Context context){
        List<PlanItem> planItemList = new ArrayList<PlanItem>();

        final String MY_QUERY = "SELECT " +
                "item.id 		AS item_id, " +
                "item.code 		AS item_code, " +
                "item.name 		AS item_name " +
                "FROM package_item INNER JOIN package ON package_item.package_id = package.id " +
                "INNER JOIN item ON item.id =package_item.item_id " +
                "WHERE package_item.package_id=?";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(plan.getId())});

        CommandDAO commandDao = new CommandDAO(context);
        commandDao.open();
        cursor.moveToFirst();

        do {
            PlanItem planItem = new PlanItem();

            planItem.setId(cursor.getInt(0));
            planItem.setCode(cursor.getString(1));
            planItem.setName(cursor.getString(2));
            planItem.setCommandList(commandDao.getCommandByItemCode(cursor.getString(1)));

            planItemList.add(planItem);
        } while (cursor.moveToNext());
        cursor.close();
        commandDao.close();
        return planItemList;
    }
}
