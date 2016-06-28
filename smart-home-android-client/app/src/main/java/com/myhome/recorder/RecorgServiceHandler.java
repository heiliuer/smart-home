package com.myhome.recorder;


import com.myhome.recorder.json.RecorgData;

/**
 * Created by Administrator on 2016/5/23 0023.
 */
public interface RecorgServiceHandler {

    void addOnRecorgListener(RecorgService.OnRecorgListener listener);

    void rmoveOnRecorgListener(RecorgService.OnRecorgListener listener);

    public interface OnRecorgListener {
        public void onRecorgSuccess(RecorgData recorgData);
    }
}
