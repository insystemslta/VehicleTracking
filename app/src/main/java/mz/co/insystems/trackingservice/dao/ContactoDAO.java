package mz.co.insystems.trackingservice.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import mz.co.insystems.trackingservice.db.InSystemsDB;
import mz.co.insystems.trackingservice.model.Contacto;

/**
 * Created by voloide on 9/15/16.
 */
public class ContactoDAO {

    private SQLiteDatabase database;
    private String[] columns = {InSystemsDB.COLUMN_CONTACT_ID,
            InSystemsDB.COLUMN_CONTACT_PHONE_NEMBER_1,
            InSystemsDB.COLUMN_CONTACT_PHONE_NEMBER_2,
            InSystemsDB.COLUMN_CONTACT_EMAIL};

    private InSystemsDB inSystemsDB;
    private static final String TAG                     = "OwnerDAO";
    public ContactoDAO(Context context) {
        inSystemsDB = new InSystemsDB(context);
    }

    public void open() throws SQLException {
        database = inSystemsDB.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public boolean isOpened() {
        if (database.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    public Contacto create(Contacto contacto){

        ContentValues values = new ContentValues();
        values.put(InSystemsDB.COLUMN_CONTACT_ID, contacto.getId());
        values.put(InSystemsDB.COLUMN_CONTACT_PHONE_NEMBER_1, contacto.getTelefone_1());
        values.put(InSystemsDB.COLUMN_CONTACT_PHONE_NEMBER_2, contacto.getTelefone_2());
        values.put(InSystemsDB.COLUMN_CONTACT_EMAIL, contacto.getEmail());

        try {

            long insertedId = database.insert(InSystemsDB.TABLE_CONTACT, null, values);
            Log.d(TAG, "Saving Contacto... " + insertedId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Contacto contacto){

        long updated = 0;

        ContentValues values = new ContentValues();
        values.put(InSystemsDB.COLUMN_CONTACT_PHONE_NEMBER_1, contacto.getTelefone_1());
        values.put(InSystemsDB.COLUMN_CONTACT_PHONE_NEMBER_2, contacto.getTelefone_2());
        values.put(InSystemsDB.COLUMN_CONTACT_EMAIL, contacto.getEmail());

        try {
            updated = database.update(InSystemsDB.TABLE_CONTACT, values, InSystemsDB.COLUMN_CONTACT_ID + " = " + contacto.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (updated > 0) ? true : false;
    }

    public Contacto get(int contactoId){
        Cursor cursor = database.query(InSystemsDB.TABLE_CONTACT, columns, InSystemsDB.COLUMN_CONTACT_ID + " = " + contactoId, null, null, null, null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();
            Contacto contact = new Contacto();
            loadContact(contact, cursor);
            return contact;
        }else {
            return null;
        }
    }

    public boolean delete(Contacto contacto){
        long deleted = 0;
        try {
            deleted = database.delete(InSystemsDB.TABLE_CONTACT, InSystemsDB.COLUMN_CONTACT_ID + " = " + contacto.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (deleted > 0) ? true : false;
    }

    private void loadContact(Contacto contact, Cursor cursor) {
        cursor.moveToFirst();

        contact.setId(cursor.getLong(0));
        contact.setTelefone_1(cursor.getString(1));
        contact.setTelefone_2(cursor.getString(2));
        contact.setEmail(cursor.getString(3));
        cursor.close();
    }

    public boolean contactExistsOnDb(Contacto contacto) {
        Cursor cursor = database.query(InSystemsDB.TABLE_CONTACT, columns, InSystemsDB.COLUMN_CONTACT_ID + " = " + contacto.getId(), null, null, null, null);
        if (cursor.moveToFirst()) return true;
        return false;
    }

}
