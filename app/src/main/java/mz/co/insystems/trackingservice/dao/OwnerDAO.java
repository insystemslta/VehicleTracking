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
import mz.co.insystems.trackingservice.model.type.OwnerType;

/**
 * Created by voloide on 9/15/16.
 */
public class OwnerDAO {

    private SQLiteDatabase database;


    private String[] columns = {InSystemsDB.COLUMN_ID,
            InSystemsDB.COLUMN_PESSOA_NAME,
            InSystemsDB.COLUMN_PESSOA_SURNAME,
            InSystemsDB.COLUMN_PESSOA_CONTACT_ID,
            InSystemsDB.TYPE};

    private InSystemsDB inSystemsDB;
    private static final String TAG                     = "OwnerDAO";

    public OwnerDAO(Context context) {
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

    public void create(Owner owner, Context context) {
        Owner newOwner = new Owner();
        long insertedId = 0;
        int insertedContactId = 0;
        ContactoDAO contactoDao = new ContactoDAO(context);

        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_ID, owner.getId());
            values.put(InSystemsDB.COLUMN_PESSOA_NAME, owner.getName());
            values.put(InSystemsDB.COLUMN_PESSOA_SURNAME, owner.getSurname());
            values.put(InSystemsDB.COLUMN_PESSOA_CONTACT_ID, owner.getContacto().getId());
            values.put(InSystemsDB.TYPE, owner.getType().getId());





            try {
                contactoDao.open();
                boolean contactExists = contactoDao.contactExistsOnDb(owner.getContacto());
                insertedId = database.insert(InSystemsDB.TABLE_OWNER, null, values);
                Log.d(TAG, "Saving Owner... " + insertedId);
                if (!contactExists && insertedId > 0) contactoDao.create(owner.getContacto());
                contactoDao.close();
            }catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if (insertedId > 0) {

            Cursor cursor = database.query(InSystemsDB.TABLE_OWNER, columns, InSystemsDB.COLUMN_ID + " = " + owner.getId(), null, null, null, null);
            cursor.moveToFirst();
            fillOwnerFromDb(cursor,context);
            cursor.close();
            return newOwner;
        } else {
            return null;
        }*/
    }

    public boolean delete(Owner owner, Context context){
        ContactoDAO contactoDao = new ContactoDAO(context);
        try {
            database.beginTransaction();
            database.delete(InSystemsDB.TABLE_OWNER, InSystemsDB.COLUMN_ID + " = " + owner.getId(), null);
            contactoDao.open();
            contactoDao.delete(owner.getContacto());
            contactoDao.close();
            database.setTransactionSuccessful();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            database.endTransaction();
        }finally {
            database.endTransaction();
        }
        return false;
    }


    private Owner fillOwnerFromDb(Cursor cursor, Context context) {
        ContactoDAO contactoDao = new ContactoDAO(context);
        contactoDao.open();
        Owner owner = new Owner();
        owner.setId(cursor.getLong(0));
        owner.setName(cursor.getString(1));
        owner.setSurname(cursor.getString(2));
        owner.setContacto(contactoDao.get(cursor.getInt(3)));
        owner.setType(new OwnerType(cursor.getShort(4)));
        contactoDao.close();
        return owner;
    }

    public Owner get(long ownerId, Context context){

        //Owner owner = new Owner();

        Cursor cursor = database.query(InSystemsDB.TABLE_OWNER, columns, InSystemsDB.COLUMN_ID + " = " + ownerId, null, null, null, null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            Owner owner = fillOwnerFromDb(cursor, context);
            cursor.close();
            return owner;
        }else {
            return null;
        }

    }


    public Owner update (Owner owner, Context context){
        Owner newOwner = new Owner();
        long updated = 0;
        ContactoDAO contactoDao = new ContactoDAO(context);

        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_PESSOA_NAME, owner.getName());
            values.put(InSystemsDB.COLUMN_PESSOA_SURNAME, owner.getSurname());
            values.put(InSystemsDB.COLUMN_PESSOA_CONTACT_ID, owner.getContacto().getId());
            values.put(InSystemsDB.TYPE, owner.getType().getId());


            try {
                contactoDao.open();
                database.beginTransaction();
                updated = database.update(InSystemsDB.TABLE_OWNER, values, InSystemsDB.COLUMN_ID + " = " + owner.getId(), null);

                contactoDao.update(owner.getContacto());

                database.setTransactionSuccessful();
                contactoDao.close();
            } catch (Exception e) {
                e.printStackTrace();
                database.endTransaction();
            }finally {
                database.endTransaction();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (updated > 0) {
            Cursor cursor = database.query(InSystemsDB.TABLE_OWNER, columns, InSystemsDB.COLUMN_ID + " = " + owner.getId(), null, null, null, null);
            cursor.moveToFirst();
            newOwner = fillOwnerFromDb(cursor, context);
            cursor.close();

            return newOwner;
        }else {
            return null;
        }

    }

    public Owner getOwnerByName(Owner owner, Context context) {
        Cursor cursor = database.query(InSystemsDB.TABLE_OWNER, columns, InSystemsDB.COLUMN_PESSOA_NAME+ " = " + "'" + owner.getName() + "'", null, null, null, null);
        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            Owner newOwner = fillOwnerFromDb(cursor, context);

            return newOwner;
        }else {
            return null;
        }
    }

    public boolean exists(Owner owner) {
        Cursor cursor = database.query(InSystemsDB.TABLE_OWNER, columns, InSystemsDB.COLUMN_ID + " = " + owner.getId(), null, null, null, null);
        return (cursor.moveToFirst())? true : false;
    }

    public List<Owner> getAll(Context context) {
        List<Owner> owners = new ArrayList<Owner>();

        Cursor cursor = database.query(InSystemsDB.TABLE_OWNER, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            do {
                Owner owner = new Owner();

                ContactoDAO contactoDao = new ContactoDAO(context);
                contactoDao.open();

                owner.setId(cursor.getInt(0));
                owner.setName(cursor.getString(1));
                owner.setSurname(cursor.getString(2));
                owner.setType(new OwnerType(cursor.getInt(4)));
                owner.setContacto(contactoDao.get(cursor.getInt(3)));

                owners.add(owner);
            } while (cursor.moveToNext());
            cursor.close();
            return owners;
        }else {
            return null;
        }
    }
}