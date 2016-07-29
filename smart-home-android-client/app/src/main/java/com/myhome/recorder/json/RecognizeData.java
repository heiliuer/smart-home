
package com.myhome.recorder.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RecognizeData {

    @SerializedName("item")
    @Expose
    private List<String> item = new ArrayList<String>();
    @SerializedName("json_res")
    @Expose
    private String jsonRes;

    /**
     * @return The item
     */
    public List<String> getItem() {
        return item;
    }

    /**
     * @param item The item
     */
    public void setItem(List<String> item) {
        this.item = item;
    }

    /**
     * @return The jsonRes
     */
    public String getJsonRes() {
        return jsonRes;
    }

    /**
     * @param jsonRes The json_res
     */
    public void setJsonRes(String jsonRes) {
        this.jsonRes = jsonRes;
    }

}
