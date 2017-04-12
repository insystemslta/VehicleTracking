package mz.co.insystems.trackingservice.model;

import java.io.Serializable;

/**
 * Created by voloide on 9/15/16.
 */
public class CommandPart implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String part;
    private int position;
    private String type;

    public CommandPart(int id, String part, int position, String type) {
        this.id = id;
        this.part = part;
        this.position = position;
        this.type = type;
    }

    public CommandPart() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
