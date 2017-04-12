package mz.co.insystems.trackingservice.model.type;

import java.io.Serializable;

/**
 * Created by voloide on 9/15/16.
 */
public class UserType implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String description;
    private boolean isAdmin;

    public UserType(boolean isAdmin, String description, int id) {
        this.isAdmin = isAdmin;
        this.description = description;
        this.id = id;
    }

    public UserType(int id) {
        this.id = id;
    }

    public UserType(int id, boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
    }

    public UserType() {
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
