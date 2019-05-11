package com.cssoftwaretech.smm.gp;

public class GroupItemInfo {
    private String gpId;
    private String gpName;
    private String gpSubTitle;

    public GroupItemInfo(String gpId, String gpName, String gpSubTitle) {
        this.gpId = gpId;
        this.gpName = gpName;
        this.gpSubTitle = gpSubTitle;
    }

    public String getGpId() {
        return gpId;
    }

    public String getGpName() {
        return gpName;
    }

    public String getGpSubTitle() {
        return gpSubTitle;
    }

}
