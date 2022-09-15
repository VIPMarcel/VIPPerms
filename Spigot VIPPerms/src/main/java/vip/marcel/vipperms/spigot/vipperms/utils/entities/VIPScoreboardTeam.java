package vip.marcel.vipperms.spigot.vipperms.utils.entities;

public class VIPScoreboardTeam {

    private int sortId;

    private String name;

    private String colorCode;

    private String prefix;

    private String suffix;

    public VIPScoreboardTeam() {

    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColor(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
