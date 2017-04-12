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
public class PlanDAO {

    private SQLiteDatabase database;
    private String[] columns = {InSystemsDB.COLUMN_PLAN_ID,
            InSystemsDB.COLUMN_PLAN_NAME};

    private InSystemsDB inSystemsDB;

    public PlanDAO(Context context){
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

    public Plan create (Plan pkg){

        Plan plan = new Plan();

        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_PLAN_ID, pkg.getId());
            values.put(InSystemsDB.COLUMN_PLAN_NAME, pkg.getName());

            long insertedId = database.insert(InSystemsDB.TABLE_PLAN, null, values);

            Log.d("Plan DAO", "Saving Plan... " + insertedId);

            Cursor cursor = database.query(InSystemsDB.TABLE_PLAN, columns, InSystemsDB.COLUMN_PLAN_ID + " = " + insertedId, null, null, null, null);
            cursor.moveToFirst();

            plan.setId(cursor.getLong(0));
            plan.setName(cursor.getString(1));

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan;

    }

    public void delete(Plan pkg){
        database.delete(InSystemsDB.TABLE_PLAN, InSystemsDB.COLUMN_PLAN_ID + " = " + pkg.getId(), null);
    }

    public List<Plan> getAll(){
        List<Plan> planArrayList = new ArrayList<Plan>();

        Cursor cursor = database.query(InSystemsDB.TABLE_PLAN, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            do {
                Plan plan = new Plan();
                plan.setId(cursor.getLong(0));
                plan.setName(cursor.getString(1));
                planArrayList.add(plan);
            } while (cursor.moveToNext());
            cursor.close();
            return planArrayList;
        }else {
            return null;
        }
    }

    public Plan get(Plan plan){

        Cursor cursor = database.query(InSystemsDB.TABLE_PLAN, columns, InSystemsDB.COLUMN_PLAN_ID + " = " + plan.getId(), null, null, null, null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            plan.setId(cursor.getLong(0));
            plan.setName(cursor.getString(1));

            return plan;
        }else {
            return null;
        }

    }

    public Plan update (Plan pkg){
        Plan plan = new Plan();


        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_PLAN_NAME, pkg.getName());

            long updated = database.update(InSystemsDB.TABLE_PLAN, values, InSystemsDB.COLUMN_PLAN_ID + " = " + pkg.getId(), null);

            Cursor cursor = database.query(InSystemsDB.TABLE_PLAN, columns, InSystemsDB.COLUMN_PLAN_ID + " = " + updated, null, null, null, null);
            cursor.moveToFirst();

            plan.setId(cursor.getLong(0));
            plan.setName(cursor.getString(1));
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan;
    }

    public void addItemsToPlan(int id, int planId, int itemId){
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_PACK_ITEM_ID, 	id);
            values.put(InSystemsDB.COLUMN_PACKAGE_ITEM_ID, 	planId);
            values.put(InSystemsDB.COLUMN_ITEM_PACKAGE_ID, 	itemId);

            database.insert(InSystemsDB.TABLE_PACKAGE_ITEM, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PlanItem> getAllByPlanItems(Plan singlePkg, Context context){
        List<PlanItem> planItemList = new ArrayList<PlanItem>();

        final String MY_QUERY = "SELECT " +

                "item.id 		AS item_id, " +
                "item.code 		AS item_code, " +
                "item.name 		AS item_name " +
                "FROM package_item INNER JOIN package ON package_item.package_id = package.id " +
                "INNER JOIN item ON item.id =package_item.item_id " +
                "WHERE package_item.package_id=?";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(singlePkg.getId())});

        CommandDAO commandDAO = new CommandDAO(context);
        commandDAO.open();
        cursor.moveToFirst();

        do {
            PlanItem planItem = new PlanItem();

            planItem.setId(cursor.getInt(0));
            planItem.setCode(cursor.getString(1));
            planItem.setName(cursor.getString(2));
            planItem.setCommandList(commandDAO.getCommandByItemCode(cursor.getString(1)));

            planItemList.add(planItem);
        } while (cursor.moveToNext());
        cursor.close();
        commandDAO.close();
        return planItemList;

    }

    public Plan getLoadedPlan(Plan pln, Context context, boolean configuration) {
        Cursor cursor;
        if (pln.getName() == null) {
            cursor = database.query(InSystemsDB.TABLE_PLAN, columns, InSystemsDB.COLUMN_PLAN_ID + " = " + pln.getId() , null, null, null, null);

        }else{
            cursor = database.query(InSystemsDB.TABLE_PLAN, columns, InSystemsDB.COLUMN_PLAN_NAME + " = " + "'" + pln.getName() + "'", null, null, null, null);

        }

        Plan plan = new Plan();

        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            PlanItemDAO planItemDAO = new PlanItemDAO(context);
            planItemDAO.open();
            plan.setId(cursor.getLong(0));
            plan.setName(cursor.getString(1));
            if (!configuration)  plan.setPlanItemList(planItemDAO.getPlanItemsByPlan(plan, context));
            planItemDAO.close();
        }
        cursor.close();
        return plan;
    }

    public void loadPlans(int id, int planId, int itemId, Context contex){
        PlanItemDAO planItemDAO = new PlanItemDAO(contex);
        planItemDAO.open();
        planItemDAO.addItemToPlan(id, planId, itemId);
        planItemDAO.close();
    }


    public Plan getPlanIdByName(String name) {
        Cursor cursor = database.query(InSystemsDB.TABLE_PLAN, columns, InSystemsDB.COLUMN_PLAN_NAME + " = " + "'" + name + "'", null, null, null, null);

        Plan plan = new Plan();

        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            plan.setId(cursor.getLong(0));
            plan.setName(cursor.getString(1));
        }

        cursor.close();
        return plan;
    }

    public void updatePackageItem(int int1, int int2, int int3,
                                  Context context) {
        // TODO Auto-generated method stub

    }

    public void deletePackageItem(int int1, Context context) {
        // TODO Auto-generated method stub

    }


    public boolean exists(Plan plan) {
        Cursor cursor = database.query(InSystemsDB.TABLE_PLAN, columns, InSystemsDB.COLUMN_PLAN_ID + " = " + plan.getId(), null, null, null, null);
        return (cursor.moveToFirst())? true : false;
    }
}
