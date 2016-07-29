
package com.myhome.recorder.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RObject {

    @SerializedName("_settingtype_on")
    @Expose
    private String settingtypeOn;
    @SerializedName("settingtype")
    @Expose
    private String settingtype;

    /**
     * 
     * @return
     *     The settingtypeOn
     */
    public String getSettingtypeOn() {
        return settingtypeOn;
    }

    /**
     * 
     * @param settingtypeOn
     *     The _settingtype_on
     */
    public void setSettingtypeOn(String settingtypeOn) {
        this.settingtypeOn = settingtypeOn;
    }

    /**
     * 
     * @return
     *     The settingtype
     */
    public String getSettingtype() {
        return settingtype;
    }

    /**
     * 
     * @param settingtype
     *     The settingtype
     */
    public void setSettingtype(String settingtype) {
        this.settingtype = settingtype;
    }

}
