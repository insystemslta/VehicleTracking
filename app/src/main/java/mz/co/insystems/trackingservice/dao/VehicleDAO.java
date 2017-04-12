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
import mz.co.insystems.trackingservice.model.Owner;
import mz.co.insystems.trackingservice.model.Plan;
import mz.co.insystems.trackingservice.model.PlanItem;
import mz.co.insystems.trackingservice.model.User;
import mz.co.insystems.trackingservice.model.vehicle.Vehicle;
import mz.co.insystems.trackingservice.model.type.VehicleType;

/**
 * Created by voloide on 9/15/16.
 */
public class VehicleDAO {

    private static String OPERATION = "";
    private SQLiteDatabase database;
    private String[] columns = {InSystemsDB.V_ID,
                                InSystemsDB.COLUMN_MAKE,
                                InSystemsDB.COLUMN_MODEL,
                                InSystemsDB.COLUMN_NR_PLATE,
                                InSystemsDB.COLUMN_IMEI,
                                InSystemsDB.COLUMN_REGISTATION_DATE,
                                InSystemsDB.COLUMN_OWNER_ID,
                                InSystemsDB.COLUMN_TYPE,
                                InSystemsDB.COLUMN_CALL_NUMBER,
                                InSystemsDB.COLUMN_IMAGEURI,
                                InSystemsDB.COLUMN_PACKEGE,
                                InSystemsDB.COLUMN_VEHICLE_STATE,
                                InSystemsDB.COLUMN_VEHICLE_PASSWORD};

    private InSystemsDB inSystemsDB;


    public VehicleDAO(Context context) {
        inSystemsDB = new InSystemsDB(context);
    }

    public void close(){
        database.close();
    }

    public void open() throws SQLException {
        database = inSystemsDB.getWritableDatabase();
    }

    private static final String TAG                     = "VehicleDAO";

    public boolean isOpened(){
        return database.isOpen();
    }

    private ContentValues loadContentValues(Vehicle vehicle, String operation) {
        ContentValues values = new ContentValues();
        if (operation.equals("create")) values.put(InSystemsDB.V_ID, vehicle.getId());
        values.put(InSystemsDB.COLUMN_MAKE, 			vehicle.getMake());
        values.put(InSystemsDB.COLUMN_MODEL, 			vehicle.getModel());
        values.put(InSystemsDB.COLUMN_NR_PLATE, 		vehicle.getNrPlate());
        values.put(InSystemsDB.COLUMN_IMEI, 			vehicle.getIMEI());
        values.put(InSystemsDB.COLUMN_REGISTATION_DATE, vehicle.getRegistrationDate());
        values.put(InSystemsDB.COLUMN_OWNER_ID, 		vehicle.getOwner().getId());
        values.put(InSystemsDB.COLUMN_TYPE, 			vehicle.getType().getId());
        values.put(InSystemsDB.COLUMN_CALL_NUMBER, 		vehicle.getCallNumber());
        values.put(InSystemsDB.COLUMN_IMAGEURI, 		vehicle.getImage());
        values.put(InSystemsDB.COLUMN_PACKEGE, 			vehicle.getPlan().getId());
        values.put(InSystemsDB.COLUMN_VEHICLE_STATE, 	(vehicle.isActive()) ? 1 : 2);
        values.put(InSystemsDB.COLUMN_VEHICLE_PASSWORD, vehicle.getPassword());
        return values;
    }

    private Vehicle loadVehicle(Vehicle vehicle, Cursor cursor) {
        vehicle.setId(cursor.getLong(0));
        vehicle.setMake(cursor.getString(1));
        vehicle.setModel(cursor.getString(2));
        vehicle.setNrPlate(cursor.getString(3));
        vehicle.setIMEI(cursor.getString(4));
        vehicle.setRegistrationDate(cursor.getString(5));
        vehicle.setOwner(new Owner(cursor.getLong(6)));
        vehicle.setCallNumber(cursor.getString(8));
        vehicle.setType(new VehicleType(cursor.getInt(7), null));
        vehicle.setImage(cursor.getString(9));
        vehicle.setPlan(new Plan(cursor.getLong(10)));
        vehicle.setActive((cursor.getInt(11) == 1));
        vehicle.setPassword(cursor.getString(12));
        if (hasExtraPlanItems(vehicle.getId())) vehicle.setExtraPlanItems(getVehicleExtraPlanItems(vehicle.getId()));
        return vehicle;
    }

    public boolean hasExtraPlanItems(long vehicleId){
        final String MY_QUERY = "SELECT vehicle_extra_item.* " +
                                "FROM vehicle_extra_item " +
                                "WHERE vehicle_id=?";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(vehicleId)});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }else {
            return false;
        }
    }

    public List<PlanItem> getVehicleExtraPlanItems(long vehicleId){
        final String MY_QUERY = "SELECT vehicle_extra_item.* " +
                                "FROM vehicle_extra_item " +
                                "WHERE vehicle_id=?";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(vehicleId)});
        List<PlanItem> extraItems = new ArrayList<PlanItem>();
        PlanItem item = new PlanItem();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                item.setId(cursor.getInt(2));
                extraItems.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return extraItems;
    }

    public Vehicle create (Vehicle vehicle, User user, Context context){

        Vehicle newVehicle = new Vehicle();

        OPERATION = "create";

        OwnerDAO ownerDAO = new OwnerDAO(context);
        PlanDAO planDAO = new PlanDAO(context);
        long insertedId  = 0;

        try {
            ownerDAO.open();
            planDAO.open();
            boolean ownerExists = ownerDAO.exists(vehicle.getOwner());
            boolean planExists = planDAO.exists(vehicle.getPlan());

            ContentValues values = loadContentValues(vehicle, OPERATION);
            insertedId = database.insert(InSystemsDB.TABLE_VEHICLE, null, values);
            associateVehicleToUser(vehicle.getId(), user.getId());
            if (!ownerExists)
                ownerDAO.create(vehicle.getOwner(), context);
            else
                Log.d(TAG, "Owner not created =============== " + vehicle.getOwner().getId());
            if (!planExists)
                planDAO.create(vehicle.getPlan());
            else
                Log.d(TAG, "Plan not created  ============ == " + vehicle.getPlan().getId());

            //if there are extraimes, insert...
            if (vehicle.getExtraPlanItems() != null){
                for (int i = 0; i < vehicle.getExtraPlanItems().size()-1; i++) {
                    loadExtraItem(i+1, vehicle.getId(), vehicle.getExtraPlanItems().get(i).getId());
                }
            }
            Log.d(TAG, "CREATED VEHICLE " + insertedId);
            ownerDAO.close();
            planDAO.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (insertedId > 0 ) {
            Cursor cursor = database.query(InSystemsDB.TABLE_VEHICLE, columns, InSystemsDB.V_ID + " = " + vehicle.getId(), null, null, null, null);

            if (cursor.moveToFirst()){
                cursor.moveToFirst();
                loadVehicle(vehicle, cursor);
                cursor.close();
            }
            return newVehicle;
        }else {
            return null;
        }

    }

    public boolean confirmContact(Vehicle vehicle) {

        final String MY_QUERY = "SELECT * " +
                                "FROM vehicle " +
                                "WHERE call_number=?";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{vehicle.getCallNumber()});
        return (cursor.moveToFirst());
    }

    public void associateVehicleToUser(long vehicleId, long userId) {
        try {
            ContentValues userVehicleValues = new ContentValues();
            userVehicleValues.put(InSystemsDB.COLUMN_UV_USER_ID, 		userId);
            userVehicleValues.put(InSystemsDB.COLUMN_UV_VEHICLE_ID, 	vehicleId);
            database.insert(InSystemsDB.TABLE_USER_VEHICLE, null, userVehicleValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delete(Vehicle vehicle){
        database.delete(InSystemsDB.TABLE_VEHICLE, InSystemsDB.V_ID + " = " + vehicle.getId(), null);
    }

    public List<Vehicle> getAll(User user){

        List<Vehicle> vehicles = new ArrayList<Vehicle>();

        Cursor cursor = null;

        if (user.getType().isAdmin()) {
            final String MY_QUERY = "SELECT *" +
                                    " FROM vehicle";

            cursor = database.rawQuery(MY_QUERY, null);
        }else {
            final String MY_QUERY = "SELECT vehicle.*" +
                                    "FROM vehicle INNER JOIN user_vehicle 	ON vehicle.id 	= user_vehicle.vehicle_id " +
                                                 "INNER JOIN user 			ON user.id 		= user_vehicle.user_id " +
                                    "WHERE user.id=?";

            cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(user.getId())});
        }
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                Vehicle vehicle = new Vehicle();
                vehicles.add(loadVehicle(vehicle, cursor));
            } while (cursor.moveToNext());

            cursor.close();
            return vehicles;
        }else {
            return null;
        }

    }

    public Vehicle get(Vehicle vehicle){

        Cursor cursor = null;
        if (vehicle.getId() > 0) {
            cursor = database.query(InSystemsDB.TABLE_VEHICLE, columns, InSystemsDB.V_ID + " = " + vehicle.getId(), null, null, null, null);
        }else if (vehicle.getCallNumber() != null) {
            cursor = database.query(InSystemsDB.TABLE_VEHICLE, columns, InSystemsDB.COLUMN_CALL_NUMBER + " = " + vehicle.getCallNumber(), null, null, null, null);
        }
        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            loadVehicle(vehicle, cursor);
            cursor.close();
            return vehicle;
        }else {
            return null;
        }

    }

    public Vehicle update (Vehicle vehicle){
        Vehicle newVehicle = new Vehicle();
        OPERATION = "update";
        long updatedId = 0;
        try {
            ContentValues values = loadContentValues(vehicle, OPERATION);

            updatedId = database.update(InSystemsDB.TABLE_VEHICLE, values, InSystemsDB.V_ID + " = " +vehicle.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (updatedId > 0) {
            Cursor cursor = database.query(InSystemsDB.TABLE_VEHICLE, columns, InSystemsDB.V_ID + " = " + vehicle.getId(), null, null, null, null);
            cursor.moveToFirst();
            loadVehicle(newVehicle, cursor);
            cursor.close();
            return newVehicle;
        }else {
            return null;
        }
    }

    public void updatePassword (Vehicle vehicle){
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.V_ID, vehicle.getId());
            values.put(InSystemsDB.COLUMN_VEHICLE_PASSWORD, vehicle.getPassword());

            database.update(InSystemsDB.TABLE_VEHICLE, values, InSystemsDB.V_ID + " = " +vehicle.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void roolbackPassword (long vehicleId){
        try {
           String sql = "UPDATE "   + InSystemsDB.TABLE_VEHICLE +
                       // " SET "     + InSystemsDB.COLUMN_VEHICLE_PASSWORD   + "=" + InSystemsDB.COLUMN_VEHICLE_OLD_PASSWORD +
                        " WHERE "   + InSystemsDB.V_ID                      + "=" + vehicleId;
            database.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfVehicleExistOnDevice(Vehicle vehicle) {
        Cursor cursor = database.query(InSystemsDB.TABLE_VEHICLE, columns, InSystemsDB.V_ID + " = " + vehicle.getId(), null, null, null, null);
        if (cursor.moveToFirst()){
            cursor.close();
            return true;
        }else {
            return false;
        }
    }

    public void updateExtraItem(int id, int vehicle_id, int item_id) {
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_VEHICLE_EXTRA_ITEM_VEHICLE_ID, 	vehicle_id);
            values.put(InSystemsDB.COLUMN_VEHICLE_EXTRA_ITEM_ITEM_ID, 		item_id);
            database.update(InSystemsDB.TABLE_VEHICLE_EXTRA_ITEM, values, InSystemsDB.COLUMN_VEHICLE_EXTRA_ITEM_ID+ " = " + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteExtraItem(int id) {
        try {
            database.delete(InSystemsDB.TABLE_VEHICLE_EXTRA_ITEM, InSystemsDB.COLUMN_VEHICLE_EXTRA_ITEM_ID + " = " + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasExtraItem(long vehicleId){
        final String MY_QUERY = "SELECT vehicle_extra_item.* " +
                                "FROM vehicle_extra_item " +
                                "WHERE vehicle_id=?";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(vehicleId)});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }else {
            return false;
        }
    }

    public List<PlanItem> getVehicleExtraItems(long vehicleId){
        final String MY_QUERY = "SELECT vehicle_extra_item.* " +
                                "FROM vehicle_extra_item " +
                                "WHERE vehicle_id=?";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(vehicleId)});
        List<PlanItem> extraItems = new ArrayList<PlanItem>();
        PlanItem item = new PlanItem();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                item.setId(cursor.getInt(2));
                extraItems.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return extraItems;
    }

    public void loadExtraItem(int id, long vehicle_id, long item_id) {
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_VEHICLE_EXTRA_ITEM_ID, 			id);
            values.put(InSystemsDB.COLUMN_VEHICLE_EXTRA_ITEM_VEHICLE_ID, 	vehicle_id);
            values.put(InSystemsDB.COLUMN_VEHICLE_EXTRA_ITEM_ITEM_ID, 		item_id);
            long inserted = database.insert(InSystemsDB.TABLE_VEHICLE_EXTRA_ITEM, null, values);
            Log.d(TAG, "Created Extra item "+ inserted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
