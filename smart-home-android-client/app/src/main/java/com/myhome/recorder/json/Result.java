
package com.myhome.recorder.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("intent")
    @Expose
    private String intent;
    @SerializedName("object")
    @Expose
    private RObject object;
    @SerializedName("score")
    @Expose
    private Double score;

    /**
     * @return The domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain The domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return The intent
     */
    public String getIntent() {
        return intent;
    }

    /**
     * @param intent The intent
     */
    public void setIntent(String intent) {
        this.intent = intent;
    }

    public RObject getObject() {
        return object;
    }

    public void setObject(RObject object) {
        this.object = object;
    }

    /**
     * @return The score
     */
    public Double getScore() {
        return score;
    }

    /**
     * @param score The score
     */
    public void setScore(Double score) {
        this.score = score;
    }

}
