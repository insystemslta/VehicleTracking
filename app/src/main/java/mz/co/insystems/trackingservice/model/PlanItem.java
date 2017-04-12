package mz.co.insystems.trackingservice.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by voloide on 9/15/16.
 */
public class PlanItem implements Serializable{

    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private String code;
    private List<Command> commandList;

    public PlanItem(long id, String name, String code, List<Command> commandList) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.commandList = commandList;
    }

    public PlanItem(long id) {
        this.id = id;
    }

    public PlanItem() {
    }

    public List<Command> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<Command> commandList) {
        this.commandList = commandList;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
