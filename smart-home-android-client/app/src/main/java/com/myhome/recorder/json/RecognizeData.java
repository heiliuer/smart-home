package com.myhome.recorder.json;

public class RecognizeData {
    private Content content;

    private Result result;

    public void setContent(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return this.content;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }

}