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
import mz.co.insystems.trackingservice.model.Command;
import mz.co.insystems.trackingservice.model.CommandPart;
import mz.co.insystems.trackingservice.model.CommandResponse;
import mz.co.insystems.trackingservice.model.Plan;
import mz.co.insystems.trackingservice.model.PlanItem;

/**
 * Created by voloide on 9/16/16.
 */
public class CommandDAO {

    private SQLiteDatabase database;
    private String[] columns = {InSystemsDB.COLUMN_COMMAND_ID,
            InSystemsDB.COLUMN_COMMAND_NAME,
            InSystemsDB.COLUMN_COMMAND_CODE,
            InSystemsDB.COLUMN_COMMAND_ITEM_CODE,
            InSystemsDB.COLUMN_COMMAND_ICON,
            InSystemsDB.COLUMN_COMMAND_TARGET};

    private String[] commandPartsColumns = {InSystemsDB.COLUMN_COMMAND_PARTS_ID,
            InSystemsDB.COLUMN_COMMAND_PARTS_COMMAND_ID,
            InSystemsDB.COLUMN_COMMAND_PARTS_PART,
            InSystemsDB.COLUMN_COMMAND_PARTS_POSITION,
            InSystemsDB.COLUMN_COMMAND_PARTS_TYPE};

    private String[] commandResponseColumns = {	InSystemsDB.COLUMN_COMMAND_RESPONSE_ID,
            InSystemsDB.COLUMN_COMMAND_RESPONSE_HEADER,
            InSystemsDB.COLUMN_COMMAND_RESPONSE_TITLE,
            InSystemsDB.COLUMN_COMMAND_RESPONSE_DESCRIPTION};

    private InSystemsDB inSystemsDB;
    private static final String TAG                     = "CommandDAO";

    public CommandDAO(Context context){
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

    public boolean addICommand(Command command, PlanItem planItem){
        long inserted = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_COMMAND_NAME, 		command.getName());
            values.put(InSystemsDB.COLUMN_COMMAND_CODE, 		command.getCode());
            values.put(InSystemsDB.COLUMN_COMMAND_ITEM_CODE, 	planItem.getCode());
            values.put(InSystemsDB.COLUMN_COMMAND_ICON, 		command.getIcon());
            values.put(InSystemsDB.COLUMN_COMMAND_TARGET, 		command.getTarget());

            inserted = database.insert(InSystemsDB.TABLE_COMMAND, null, values);
            Log.d(TAG, "Created Command "+ inserted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (inserted > 0) {
            return true;
        }else {
            return false;
        }
    }

    public boolean addCommamndPart(CommandPart part, Command command) {
        ContentValues values = new ContentValues();
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_ID, 		part.getId());
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_COMMAND_ID, command.getId());
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_PART, 		part.getPart());
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_POSITION, 	part.getPosition());
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_TYPE, 		part.getType());

        long inserted = 0;

        try {
            inserted = database.insert(InSystemsDB.TABLE_COMMAND_PARTS, null, values);
            Log.d(TAG, "Created CommandPart "+ inserted);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (inserted > 0) {
            return true;
        }else {
            return false;
        }
    }

    public List<Command> getAllByPlanItems(Plan singlePkg){
        List<Command> items = new ArrayList<Command>();
        return items;
    }

    public List<Command> getCommandByItemCode(String itemCode) {
        List<Command> commandList = new ArrayList<Command>();

        final String MY_QUERY = "SELECT * " +
                "FROM command " +
                "WHERE item_code=? " +
                "ORDER BY name, item_code";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{itemCode});

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            do {
                Command command = new Command();
                command.setId(cursor.getInt(0));
                command.setName(cursor.getString(1));
                command.setCode(cursor.getString(2));
                command.setIcon(cursor.getString(4));
                command.setTarget(cursor.getInt(5));
                commandList.add(command);

            } while (cursor.moveToNext());
        }


        cursor.close();

        return commandList;
    }

    public Command get(int commandId){

        List<CommandPart> commandParts = new ArrayList<CommandPart>();
        Command command = new Command();
        try {

            Cursor cursor = database.query(InSystemsDB.TABLE_COMMAND, columns, InSystemsDB.COLUMN_COMMAND_ID + " = " + commandId, null, null, null, null);
            if (cursor.moveToFirst()){

                command.setId(cursor.getInt(0));
                command.setName(cursor.getString(1));
                command.setCode(cursor.getString(2));
                command.setIcon(cursor.getString(4));
                command.setTarget(cursor.getInt(5));

                Cursor commandPartsCursor = database.query(InSystemsDB.TABLE_COMMAND_PARTS, commandPartsColumns, InSystemsDB.COLUMN_COMMAND_PARTS_COMMAND_ID + " = " + commandId, null, null, null, null);
                if (commandPartsCursor.moveToFirst()) {
                    commandPartsCursor.moveToFirst();
                    do {
                        CommandPart part = new CommandPart(commandPartsCursor.getInt(0), commandPartsCursor.getString(2), commandPartsCursor.getInt(3), commandPartsCursor.getString(4));
                        commandParts.add(part);
                    } while (commandPartsCursor.moveToNext());


                }else {
                    commandParts = null;
                }

                command.setCommandParts(commandParts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return command;
    }

    public boolean update(Command cmd) {
        long inserted = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(InSystemsDB.COLUMN_COMMAND_NAME, 		cmd.getName());
            values.put(InSystemsDB.COLUMN_COMMAND_CODE, 		cmd.getCode());
            values.put(InSystemsDB.COLUMN_COMMAND_ITEM_CODE, 	cmd.getCode());
            values.put(InSystemsDB.COLUMN_COMMAND_ICON, 		cmd.getIcon());
            values.put(InSystemsDB.COLUMN_COMMAND_TARGET, 		cmd.getTarget());

            inserted = database.update(InSystemsDB.TABLE_COMMAND, values, InSystemsDB.COLUMN_COMMAND_ID+ " = " + cmd.getId(), null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (inserted > 0) {
            return true;
        }else {
            return false;
        }
    }

    public boolean updateCommamndPart(CommandPart part, Command cmd) {
        ContentValues values = new ContentValues();
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_COMMAND_ID, cmd.getId());
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_PART, 		part.getPart());
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_POSITION, 	part.getPosition());
        values.put(InSystemsDB.COLUMN_COMMAND_PARTS_TYPE, 		part.getType());

        long inserted = 0;

        try {
            inserted = database.update(InSystemsDB.TABLE_COMMAND_PARTS,values, InSystemsDB.COLUMN_COMMAND_ID+ " = " + part.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (inserted > 0) {
            return true;
        }else {
            return false;
        }

    }

    public boolean deleteCommamndPart(CommandPart part) {
        long deleted = 0;
        try {
            deleted = database.delete(InSystemsDB.TABLE_COMMAND_PARTS, InSystemsDB.COLUMN_COMMAND_PARTS_ID + " = " + part.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (deleted > 0) {
            return true;
        }else {
            return false;
        }

    }

    public boolean delete(Command cmd) {
        long deleted = 0;
        try {
            deleted = database.delete(InSystemsDB.TABLE_COMMAND, InSystemsDB.COLUMN_COMMAND_ID + " = " + cmd.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (deleted > 0) {
            return true;
        }else {
            return false;
        }

    }

    public String getCommandResponseTitleFromHeader(String header) {
        final String MY_QUERY = "SELECT title " +
                "FROM commandresponse " +
                "WHERE header LIKE ? ";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{"%"+header+"%"});

        return (cursor.moveToFirst()) ? cursor.getString(0) : null;
    }

    public String getCommandResponseDescriptionFromHeader(String header) {
        final String MY_QUERY = "SELECT description " +
                "FROM commandresponse " +
                "WHERE header LIKE ? ";

        Cursor cursor = database.rawQuery(MY_QUERY, new String[]{"%"+header+"%"});

        return (cursor.moveToFirst()) ? cursor.getString(0) : null;
    }

    public boolean addCommamndResponse(CommandResponse response) {
        ContentValues values = new ContentValues();
        values.put(InSystemsDB.COLUMN_COMMAND_RESPONSE_ID, 			response.getId());
        values.put(InSystemsDB.COLUMN_COMMAND_RESPONSE_HEADER, 		response.getHeader());
        values.put(InSystemsDB.COLUMN_COMMAND_RESPONSE_TITLE, 		response.getTitle());
        values.put(InSystemsDB.COLUMN_COMMAND_RESPONSE_DESCRIPTION, response.getDescription());

        long inserted = 0;

        try {
            inserted = database.insert(InSystemsDB.TABLE_COMMAND_RESPONSE, null, values);
            Log.d(TAG, "Created Commamnd Response "+ inserted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (inserted > 0) ? true : false;
    }

    public boolean updateCommamndResponse(CommandResponse response) {
        ContentValues values = new ContentValues();
        values.put(InSystemsDB.COLUMN_COMMAND_RESPONSE_ID, 			response.getId());
        values.put(InSystemsDB.COLUMN_COMMAND_RESPONSE_HEADER, 		response.getHeader());
        values.put(InSystemsDB.COLUMN_COMMAND_RESPONSE_TITLE, 		response.getTitle());
        values.put(InSystemsDB.COLUMN_COMMAND_RESPONSE_DESCRIPTION, response.getDescription());

        long updated = 0;

        try {

            updated = database.update(InSystemsDB.TABLE_COMMAND_RESPONSE,values, InSystemsDB.COLUMN_COMMAND_RESPONSE_ID+ " = " + response.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (updated > 0) ? true : false;
    }

    public boolean deleteCommamndResponse(int id) {
        long deleted = 0;
        try {
            deleted = database.delete(InSystemsDB.TABLE_COMMAND_RESPONSE, InSystemsDB.COLUMN_COMMAND_RESPONSE_ID + " = " + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (deleted > 0) ? true : false;

    }

    public boolean planItemHasCommands(String itemCode) {
        Cursor cursor = database.query(InSystemsDB.TABLE_COMMAND, columns, InSystemsDB.COLUMN_COMMAND_ITEM_CODE + " = " + "'" + itemCode + "'", null, null, null, null);
        return cursor.moveToFirst();
    }
}
