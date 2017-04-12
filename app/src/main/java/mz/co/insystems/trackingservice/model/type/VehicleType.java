package mz.co.insystems.trackingservice.model.type;

import java.io.Serializable;

/**
 * Created by voloide on 9/15/16.
 */
public class VehicleType implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String description;

    public VehicleType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public VehicleType(int id) {
        this.id = id;
    }

    public VehicleType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
