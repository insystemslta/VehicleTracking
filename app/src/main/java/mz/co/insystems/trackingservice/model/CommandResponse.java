package mz.co.insystems.trackingservice.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by voloide on 9/15/16.
 */
public class CommandResponse implements Serializable{

    private static final long serialVersionUID = 1L;
    private int id;
    private String header;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private double speed;
    private String alarmImageUrl;
    private String locationUrl;
    private Date date;
    private Date time;
    private String title;
    private String description;
    private boolean power;
    private String bat;
    private boolean gprs;
    private boolean gps;
    private boolean acc;
    private boolean door;
    private double gsm;
    private String oil;
    private double odo;
    private String apn;
    private String ip;
    private String imei;
    private boolean arm;
    private int port;
    private String icon;


    public CommandResponse(int id, String header, Double latitude, Double longitude, double speed, String alarmImageUrl, String locationUrl, Date date, Date time, String title, String description, boolean power, String bat, boolean gprs, boolean gps, boolean acc, boolean door, double gsm, String oil, double odo, String apn, String ip, String imei, boolean arm, int port) {
        this.id = id;
        this.header = header;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.alarmImageUrl = alarmImageUrl;
        this.locationUrl = locationUrl;
        this.date = date;
        this.time = time;
        this.title = title;
        this.description = description;
        this.power = power;
        this.bat = bat;
        this.gprs = gprs;
        this.gps = gps;
        this.acc = acc;
        this.door = door;
        this.gsm = gsm;
        this.oil = oil;
        this.odo = odo;
        this.apn = apn;
        this.ip = ip;
        this.imei = imei;
        this.arm = arm;
        this.port = port;
    }

    public CommandResponse(int id) {
        this.id = id;
    }

    public CommandResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = Double.parseDouble(latitude);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = Double.parseDouble(longitude);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getAlarmImageUrl() {
        return alarmImageUrl;
    }

    public void setAlarmImageUrl(String alarmImageUrl) {
        this.alarmImageUrl = alarmImageUrl;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public String getBat() {
        return bat;
    }

    public void setBat(String bat) {
        this.bat = bat;
    }

    public boolean isGprs() {
        return gprs;
    }

    public void setGprs(boolean gprs) {
        this.gprs = gprs;
    }

    public boolean isGps() {
        return gps;
    }

    public void setGps(boolean gps) {
        this.gps = gps;
    }

    public boolean isAcc() {
        return acc;
    }

    public void setAcc(boolean acc) {
        this.acc = acc;
    }

    public boolean isDoor() {
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public double getGsm() {
        return gsm;
    }

    public void setGsm(double gsm) {
        this.gsm = gsm;
    }

    public String getOil() {
        return oil;
    }

    public void setOil(String oil) {
        this.oil = oil;
    }

    public double getOdo() {
        return odo;
    }

    public void setOdo(double odo) {
        this.odo = odo;
    }

    public String getApn() {
        return apn;
    }

    public void setApn(String apn) {
        this.apn = apn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isArm() {
        return arm;
    }

    public void setArm(boolean arm) {
        this.arm = arm;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
