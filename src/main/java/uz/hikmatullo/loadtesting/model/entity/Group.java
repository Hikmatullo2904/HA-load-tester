package uz.hikmatullo.loadtesting.model.entity;

public class Group extends BaseEntity {
    private String name;
    private String description;
    private String masterHost;

    public Group(String name, String description, String masterHost) {
        this.name = name;
        this.description = description;
        this.masterHost = masterHost;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMasterHost() { return masterHost; }
    public void setMasterHost(String masterHost) { this.masterHost = masterHost; }
}