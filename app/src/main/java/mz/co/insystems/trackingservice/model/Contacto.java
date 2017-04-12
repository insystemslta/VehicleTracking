package mz.co.insystems.trackingservice.model;

import java.io.Serializable;

/**
 * Created by voloide on 9/15/16.
 */
public class Contacto implements Serializable{

    private static final long serialVersionUID = 1L;

    private long id;
    private String email;
    private String telefone_1;
    private String telefone_2;


    public Contacto(long id, String email, String telefone_1, String telefone_2) {
        this.id = id;
        this.email = email;
        this.telefone_1 = telefone_1;
        this.telefone_2 = telefone_2;
    }

    public Contacto(){}

    public Contacto(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone_1() {
        return telefone_1;
    }

    public void setTelefone_1(String telefone_1) {
        this.telefone_1 = telefone_1;
    }

    public String getTelefone_2() {
        return telefone_2;
    }

    public void setTelefone_2(String telefone_2) {
        this.telefone_2 = telefone_2;
    }
}
