package mz.co.insystems.trackingservice.model;

import java.io.Serializable;

import mz.co.insystems.trackingservice.model.vehicle.Vehicle;

/**
 * Created by voloide on 9/16/16.
 */
public class UpdateHeader implements Serializable{
    private static final long serialVersionUID = 1L;
    private long id;
    private Vehicle vehicle;
    private String table;
    private int sync;
    private int syncType;
    private int target;


    public UpdateHeader(long id, Vehicle vehicle, String table, int sync, int syncType, int target) {
        this.id = id;
        this.vehicle = vehicle;
        this.table = table;
        this.sync = sync;
        this.syncType = syncType;
        this.target = target;
    }

    public UpdateHeader(long id) {
        this.id = id;
    }

    public UpdateHeader() {
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Vehicle getVehicle() {
        return vehicle;
    }
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    public String getTable() {
        return table;
    }
    public void setTable(String table) {
        this.table = table;
    }
    public int getSync() {
        return sync;
    }
    public void setSync(int sync) {
        this.sync = sync;
    }
    public int getSyncType() {
        return syncType;
    }
    public void setSyncType(int syncType) {
        this.syncType = syncType;
    }
    public int getTarget() {
        return target;
    }
    public void setTarget(int target) {
        this.target = target;
    }
}
