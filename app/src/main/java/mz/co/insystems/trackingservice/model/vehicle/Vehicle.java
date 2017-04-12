package mz.co.insystems.trackingservice.model.vehicle;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mz.co.insystems.trackingservice.model.Owner;
import mz.co.insystems.trackingservice.model.Plan;
import mz.co.insystems.trackingservice.model.PlanItem;
import mz.co.insystems.trackingservice.model.type.VehicleType;

/**
 * Created by voloide on 9/15/16.
 */
public class Vehicle implements Serializable{

    private static final long serialVersionUID = 1L;

    private long 	id;
    private String 	make;
    private String 	model;
    private String 	nrPlate;
    private String 	IMEI;
    private Owner owner;
    private Date registrationDate;
    private String 	callNumber;
    private VehicleType type;
    private String  image;
    private Plan plan;
    private boolean isActive;
    private List<PlanItem> extraPlanItems;
    private String password;

    public Vehicle(long id, String make, String model, String nrPlate, String IMEI, Owner owner, Date registrationDate, String callNumber, VehicleType type, String image, Plan plan, boolean isActive, List<PlanItem> extraPlanItems, String password) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.nrPlate = nrPlate;
        this.IMEI = IMEI;
        this.owner = owner;
        this.registrationDate = registrationDate;
        this.callNumber = callNumber;
        this.type = type;
        this.image = image;
        this.plan = plan;
        this.isActive = isActive;
        this.extraPlanItems = extraPlanItems;
        this.password = password;
    }

    public Vehicle(long id) {
        this.id = id;
    }

    public Vehicle() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNrPlate() {
        return nrPlate;
    }

    public void setNrPlate(String nrPlate) {
        this.nrPlate = nrPlate;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getRegistrationDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(registrationDate);
    }

    public void setRegistrationDate(String registrationDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(registrationDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.registrationDate = convertedDate;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<PlanItem> getExtraPlanItems() {
        return extraPlanItems;
    }

    public void setExtraPlanItems(List<PlanItem> extraPlanItems) {
        this.extraPlanItems = extraPlanItems;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
