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
import mz.co.insystems.trackingservice.model.User;
import mz.co.insystems.trackingservice.model.type.UserType;

/**
 * Created by voloide on 9/16/16.
 */
public class UserDAO {


    private SQLiteDatabase database;
    private String[] columns = {InSystemsDB.COLUMN_USER_ID,
            InSystemsDB.COLUMN_PESSOA_NAME,
            InSystemsDB.COLUMN_PESSOA_SURNAME,
            InSystemsDB.COLUMN_USER_LOGIN_NUMBER,
            InSystemsDB.COLUMN_USER_PASSWORD,
            InSystemsDB.COLUMN_USER_STATE,
            InSystemsDB.COLUMN_USER_TYPE,
            InSystemsDB.COLUMN_PESSOA_CONTACT_ID,
            InSystemsDB.COLUMN_USER_PASSWORD_RESET_CODE,
            InSystemsDB.COLUMN_USER_OLD_PASSWORD,
            InSystemsDB.COLUMN_USER_LAST_RESET};


    private InSystemsDB inSystemsDB;
    public UserDAO(Context context){
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

    public User create (User user, Context context){
        User newUser = new User();
        long insertedId = 0;
        ContactoDAO contactoDao = new ContactoDAO(context);

        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_USER_ID, user.getId());
            values.put(InSystemsDB.COLUMN_PESSOA_NAME, user.getName());
            values.put(InSystemsDB.COLUMN_PESSOA_SURNAME, user.getSurname());
            values.put(InSystemsDB.COLUMN_USER_LOGIN_NUMBER, user.getLoginNumber());
            values.put(InSystemsDB.COLUMN_USER_PASSWORD, user.getPassword());
            values.put(InSystemsDB.COLUMN_USER_STATE, (user.isActive()) ? 1 : 0);
            values.put(InSystemsDB.COLUMN_USER_TYPE, user.getType().getId());
            values.put(InSystemsDB.COLUMN_PESSOA_CONTACT_ID, user.getContacto().getId());
            values.put(InSystemsDB.COLUMN_USER_PASSWORD_RESET_CODE, user.getPasswordRestoreCode());
            values.put(InSystemsDB.COLUMN_USER_OLD_PASSWORD, user.getOldPassword());
            values.put(InSystemsDB.COLUMN_USER_LAST_RESET, user.getLastResetDate());

            try {
                contactoDao.open();
                boolean contactExists = contactoDao.contactExistsOnDb(user.getContacto());

                insertedId = database.insert(InSystemsDB.TABLE_USER, null, values);
                Log.d("User DAO", "Saving User... " + insertedId);
                if (!contactExists && insertedId > 0) contactoDao.create(user.getContacto());
                contactoDao.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (insertedId > 0) {
                Cursor cursor = database.query(InSystemsDB.TABLE_USER, columns, InSystemsDB.COLUMN_USER_ID + " = " + insertedId, null, null, null, null);

                if (cursor.moveToFirst()) {
                    cursor.moveToFirst();

                    buildUser(newUser, context, cursor);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return newUser;

    }

    public boolean delete(User user, Context context){
        ContactoDAO contactoDao = new ContactoDAO(context);
        try {
            database.beginTransaction();
            database.delete(InSystemsDB.TABLE_USER, InSystemsDB.COLUMN_USER_ID + " = " + user.getId(), null);
            contactoDao.open();
            contactoDao.delete(user.getContacto());
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

    public User get(User user, Context context){

        Cursor cursor = null;

        if (user.getPasswordRestoreCode() != null) {

            final String MY_QUERY = "SELECT * " +
                    "FROM user " +
                    "WHERE reset_code=?";
            cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(user.getPasswordRestoreCode())});
        }else if (user.getId() > 0) {
            cursor = database.query(InSystemsDB.TABLE_USER, columns, InSystemsDB.COLUMN_USER_ID + " = " + user.getId(), null, null, null, null);
        }else {
            cursor = null;
        }

        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            buildUser(user, context, cursor);
            return user;
        }else {
            return null;
        }

    }

    private void buildUser(User user, Context context, Cursor cursor) {
        ContactoDAO contactoDao = new ContactoDAO(context);
        contactoDao.open();

        user.setId(cursor.getInt(0));
        user.setName(cursor.getString(1));
        user.setSurname(cursor.getString(2));
        user.setLoginNumber(cursor.getString(3));
        user.setPassword(cursor.getString(4), false);
        user.setActive((cursor.getInt(5) > 0));
        user.setType(new UserType(cursor.getInt(6), (cursor.getInt(6) > 1)));
        user.setContacto(contactoDao.get(cursor.getInt(7)));
        user.setPasswordRestoreCode(cursor.getString(8));
        user.setOldPassword(cursor.getString(9));
        user.setLastResetDate(cursor.getString(10));
        contactoDao.close();
        cursor.close();
    }

    public boolean validateOnDB(User user){
        Cursor cursor = null;
        boolean isFound = false;
        final String MY_QUERY = "SELECT *" +
                "FROM user " +
                "WHERE login_number=? AND password=?";

        try {
            cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(user.getLoginNumber()), String.valueOf(user.getPassword())});
            if (cursor.moveToFirst()) {
                cursor.close();
                isFound = true;

            }else {
                isFound = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isFound;
    }

    public User getFromLogin(User user, Context context){
        Cursor cursor = null;
        final String MY_QUERY = "SELECT *" +
                "FROM user " +
                "WHERE login_number=? AND password=?";
        User retrivedUser = new User();

        try {
            cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(user.getLoginNumber()), String.valueOf(user.getPassword())});
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                buildUser(retrivedUser, context, cursor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retrivedUser;
    }

    public boolean update(User user, Context context) {
        long updated = 0;
        ContactoDAO contactoDao = new ContactoDAO(context);
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_PESSOA_NAME, user.getName());
            values.put(InSystemsDB.COLUMN_PESSOA_SURNAME, user.getSurname());
            values.put(InSystemsDB.COLUMN_USER_LOGIN_NUMBER, user.getLoginNumber());
            values.put(InSystemsDB.COLUMN_USER_PASSWORD, user.getPassword());
            values.put(InSystemsDB.COLUMN_USER_STATE, (user.isActive()) ? 1 : 0);
            values.put(InSystemsDB.COLUMN_USER_TYPE, user.getType().getId());
            values.put(InSystemsDB.COLUMN_PESSOA_CONTACT_ID, user.getContacto().getId());
            values.put(InSystemsDB.COLUMN_USER_PASSWORD_RESET_CODE, user.getPasswordRestoreCode());
            values.put(InSystemsDB.COLUMN_USER_OLD_PASSWORD, user.getOldPassword());
            values.put(InSystemsDB.COLUMN_USER_LAST_RESET, user.getLastResetDate());

            updated = database.update(InSystemsDB.TABLE_USER, values, InSystemsDB.COLUMN_USER_ID+ " = " + user.getId(), null);

            try {

                contactoDao.open();
                database.beginTransaction();

                updated = database.update(InSystemsDB.TABLE_USER, values, InSystemsDB.COLUMN_USER_ID+ " = " + user.getId(), null);

                contactoDao.update(user.getContacto());

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
        return (updated > 0) ? true : false;
    }

    public boolean resetPassword(User user){

        long updated = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_USER_PASSWORD, user.getPassword());
            values.put(InSystemsDB.COLUMN_USER_PASSWORD_RESET_CODE, user.getPasswordRestoreCode());
            values.put(InSystemsDB.COLUMN_USER_OLD_PASSWORD, user.getOldPassword());
            values.put(InSystemsDB.COLUMN_USER_LAST_RESET, user.getLastResetDate());

            updated = database.update(InSystemsDB.TABLE_USER, values, InSystemsDB.COLUMN_USER_PASSWORD_RESET_CODE+ " = " + user.getPasswordRestoreCode(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (updated > 0) ? true : false;
    }

    public void registPasswordResetCode(User user){
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_USER_ID, user.getId());
            values.put(InSystemsDB.COLUMN_USER_PASSWORD_RESET_CODE, user.getPasswordRestoreCode());
            values.put(InSystemsDB.COLUMN_USER_OLD_PASSWORD, user.getPassword());

            database.update(InSystemsDB.TABLE_USER, values, InSystemsDB.COLUMN_USER_ID+ " = " + user.getId(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<User> getAll(Context context){
        List<User> userList = new ArrayList<User>();

        Cursor cursor = database.query(InSystemsDB.TABLE_USER, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            do {
                User user = new User();

                ContactoDAO contactoDao = new ContactoDAO(context);
                contactoDao.open();

                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setSurname(cursor.getString(2));
                user.setLoginNumber(cursor.getString(3));
                user.setPassword(cursor.getString(4), false);
                user.setActive((cursor.getInt(5) > 0) ? true : false);
                user.setType(new UserType(cursor.getInt(6)));
                user.setContacto(contactoDao.get(cursor.getInt(7)));
                user.setPasswordRestoreCode(cursor.getString(8));
                user.setOldPassword(cursor.getString(9));
                user.setLastResetDate(cursor.getString(10));

                userList.add(user);
            } while (cursor.moveToNext());
            cursor.close();
            return userList;
        }else {
            return null;
        }
    }

    public boolean validateContact(String contact) {
        Cursor cursor = null;
        final String MY_QUERY = "SELECT *" +
                "FROM user " +
                "WHERE login_number=?";
        try {
            cursor = database.rawQuery(MY_QUERY, new String[]{contact});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (cursor.moveToFirst()) ? true : false;
    }
}
