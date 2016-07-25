package com.myhome.recorder;


import com.myhome.recorder.json.RecognizeData;

/**
 * Created by Heiliuer on 2016/5/23 0023.
 */
public interface RecognizeServiceHandler {

    void addOnRecognizeListener(OnRecognizeListener listener);

    void removeOnRecognizeListener(OnRecognizeListener listener);

    interface OnRecognizeListener {
        void onRecognizeSuccess(RecognizeData data);
    }
}
