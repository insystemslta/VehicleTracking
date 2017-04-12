package mz.co.insystems.trackingservice.model;

import java.io.Serializable;

/**
 * Created by voloide on 9/15/16.
 */
public class Pessoa implements Serializable{

    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private String surname;
    private Contacto contacto;

    public Pessoa(long id, String name, String surname, Contacto contacto) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.contacto = contacto;
    }

    public Pessoa() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }
}
