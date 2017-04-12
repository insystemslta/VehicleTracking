package mz.co.insystems.trackingservice.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import mz.co.insystems.trackingservice.model.type.UserType;
import mz.co.insystems.trackingservice.security.MD5;

/**
 * Created by voloide on 9/15/16.
 */
public class User extends Pessoa implements Serializable{

    private static final long serialVersionUID = 1L;
    private long id;
    private String loginNumber;
    private String password;
    private boolean isActive;
    private String passwordRestoreCode;
    private String oldPassword;
    private Date lastResetDate;
    private UserType type;

    public static final int APP_ADMIN		= 2;
    public static final int APP_USER		= 1;

    public User(long id, String name, String surname, Contacto contacto, long id1, String loginNumber, String password, boolean isActive, String passwordRestoreCode, String oldPassword, Date lastResetDate, UserType type) {
        super(id, name, surname, contacto);
        id = id1;
        this.loginNumber = loginNumber;
        this.password = MD5.crypt(password);
        this.isActive = isActive;
        this.passwordRestoreCode = passwordRestoreCode;
        this.oldPassword = oldPassword;
        this.lastResetDate = lastResetDate;
        this.type = type;
    }

    public User(long id) {
        this.id = id;
    }

    public User(String loginNumber, String password) {
        this.loginNumber = loginNumber;
        this.password = MD5.crypt(password);
    }

    public User() {}


    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }


    public String getLoginNumber() {
        return loginNumber;
    }

    public void setLoginNumber(String loginNumber) {
        this.loginNumber = loginNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password, boolean isToCrypt) {
        if (isToCrypt)
            this.password = MD5.crypt(password.trim());
        else
            this.password = password;


    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPasswordRestoreCode() {
        return passwordRestoreCode;
    }

    public void setPasswordRestoreCode(String passwordRestoreCode) {
        this.passwordRestoreCode = passwordRestoreCode;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getLastResetDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (lastResetDate != null)  return dateFormat.format(lastResetDate);
        return null;
    }

    public void setLastResetDate(String lastResetDate) {
        if (lastResetDate != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(lastResetDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.lastResetDate = convertedDate;
        }

    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }
}
