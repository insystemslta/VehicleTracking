package mz.co.insystems.trackingservice.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by voloide on 9/15/16.
 */
public class Command implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private List<CommandPart> commandParts;
    private String code;
    private String icon;
    private int target;

    public Command(int id, String name, List<CommandPart> commandParts, String code, String icon, int target) {
        this.id = id;
        this.name = name;
        this.commandParts = commandParts;
        this.code = code;
        this.icon = icon;
        this.target = target;
    }

    public Command() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CommandPart> getCommandParts() {
        return commandParts;
    }

    public void setCommandParts(List<CommandPart> commandParts) {
        this.commandParts = commandParts;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
