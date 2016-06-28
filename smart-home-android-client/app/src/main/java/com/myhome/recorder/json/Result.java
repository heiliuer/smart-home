package com.myhome.recorder.json;

public class Result {
    private long corpus_no;

    private long err_no;

    private long idx;

    private long res_type;

    private String sn;

    public void setCorpus_no(long corpus_no) {
        this.corpus_no = corpus_no;
    }

    public long getCorpus_no() {
        return this.corpus_no;
    }

    public void setErr_no(long err_no) {
        this.err_no = err_no;
    }

    public long getErr_no() {
        return this.err_no;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public long getIdx() {
        return this.idx;
    }

    public void setRes_type(long res_type) {
        this.res_type = res_type;
    }

    public long getRes_type() {
        return this.res_type;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return this.sn;
    }

}