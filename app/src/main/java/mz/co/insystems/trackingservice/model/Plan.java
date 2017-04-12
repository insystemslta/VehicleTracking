package mz.co.insystems.trackingservice.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by voloide on 9/15/16.
 */
public class Plan implements Serializable{

    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private List<PlanItem> planItemList;

    public Plan(long id, String name, List<PlanItem> planItemList) {
        this.id = id;
        this.name = name;
        this.planItemList = planItemList;
    }

    public Plan(long id) {
        this.id = id;
    }

    public Plan() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<PlanItem> getPlanItemList() {
        return planItemList;
    }

    public void setPlanItemList(List<PlanItem> planItemList) {
        this.planItemList = planItemList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
