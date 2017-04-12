package mz.co.insystems.trackingservice.model;

import java.io.Serializable;

import mz.co.insystems.trackingservice.model.type.OwnerType;

/**
 * Created by voloide on 9/15/16.
 */
public class Owner extends Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private OwnerType type;

    public Owner(long id, String name, String surname, Contacto contacto, long id1, OwnerType type, Contacto contacto1) {
        super(id, name, surname, contacto);
        id = id1;
        this.type = type;
        contacto = contacto1;
    }

    public Owner(long id) {
        this.id = id;
    }

    public Owner() {
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }



    public OwnerType getType() {
        return type;
    }

    public void setType(OwnerType type) {
        this.type = type;
    }
}
