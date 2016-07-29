package com.myhome.recorder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.baidu.voicerecognition.android.VoiceRecognitionClient;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.google.gson.Gson;
import com.heiliuer.myhome.R;
import com.myhome.frame.ServiceMain;
import com.myhome.recorder.json.RecognizeData;
import com.myhome.service.ComService;

import java.util.ArrayList;
import java.util.List;

public class RecognizeService extends Service implements RecognizeServiceHandler {
    private static final String TAG = "Sdk2Api";
    private static final int REQUEST_UI = 1;
    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    public static final int DELAY_MILLIS = 100;
    private SpeechRecognizer speechRecognizer;
    private int status = STATUS_None;
    private long speechEndTime = -1;
    private static final int EVENT_ERROR = 11;
    private RecordThread recordThread;
    private Handler handler;
    private VoiceRecognitionClient mASREngine;


    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler() {
        };

        mASREngine = VoiceRecognitionClient.getInstance(this);
        mASREngine.setTokenApis(Constants.API_KEY, Constants.SECRET_KEY);
        postStarRecorg();
    }

    private Runnable runnableStarRecorg = new Runnable() {
        @Override
        public void run() {
            startVoiceRecorg();
        }
    };

    private void postStarRecorg() {
        handler.removeCallbacks(runnableStarRecorg);
        handler.postDelayed(runnableStarRecorg, DELAY_MILLIS);
    }


    private void echoRecorgByAction(RecognizeData recorgData) {
        RecorgnizeJudgeResult recorgnizeJudgeResult = new RecorgnizeJudgeResult(recorgData);
        ComService cs = ServiceMain.getServiceMain().getComService();
        if (recorgnizeJudgeResult.isLightOn()) {
            cs.send(new byte[]{(byte) 0xa1, (byte) 0xff});
        } else if (recorgnizeJudgeResult.isLightOff()) {
            cs.send(new byte[]{(byte) 0xa1, (byte) 0});
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new RecorgBinder();
    }


    public class RecorgBinder extends Binder {
        public RecognizeServiceHandler getHandler() {
            return RecognizeService.this;
        }
    }


    public boolean startVoiceRecorg() {
        VoiceRecognitionConfig config = new VoiceRecognitionConfig();
        int prop = Config.CURRENT_PROP;
        // 输入法暂不支持语义解析
        if (prop == VoiceRecognitionConfig.PROP_INPUT) {
            prop = VoiceRecognitionConfig.PROP_SEARCH;
        }
        config.setProp(prop);
        config.setLanguage(Config.getCurrentLanguage());
        config.enableNLU();
        config.enableVoicePower(Config.SHOW_VOL); // 音量反馈。
        if (Config.PLAY_START_SOUND) {
            config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start); // 设置识别开始提示音
        }
        if (Config.PLAY_END_SOUND) {
            config.enableEndSoundEffect(R.raw.bdspeech_speech_end); // 设置识别结束提示音
        }
        config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率
        // 下面发起识别
        int code = mASREngine.startVoiceRecognition(mListener, config);
        if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
            Toast.makeText(RecognizeService.this, "启动失败:" + code,
                    Toast.LENGTH_LONG).show();
        } else {
//            Toast.makeText(RecognizeService.this, "开始识别语音",
//                    Toast.LENGTH_LONG).show();
        }

        return code == VoiceRecognitionClient.START_WORK_RESULT_WORKING;
    }

    public boolean onCancel() {
        mASREngine.stopVoiceRecognition();
        return true;
    }


    public boolean onStopListening() {
        mASREngine.speakFinish();
        return true;
    }


    /**
     * 识别回调接口
     */
    private MyVoiceRecogListener mListener = new MyVoiceRecogListener();

    /**
     * 重写用于处理语音识别回调的监听器
     */
    class MyVoiceRecogListener implements VoiceRecognitionClient.VoiceClientStatusChangeListener {

        @Override
        public void onClientStatusChange(int status, Object obj) {
            switch (status) {
                // 语音识别实际开始，这是真正开始识别的时间点，需在界面提示用户说话。
                case VoiceRecognitionClient.CLIENT_STATUS_START_RECORDING:
                    break;
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_START: // 检测到语音起点
                    break;
                // 已经检测到语音终点，等待网络返回
                case VoiceRecognitionClient.CLIENT_STATUS_SPEECH_END:
                    break;
                // 语音识别完成，显示obj中的结果
                case VoiceRecognitionClient.CLIENT_STATUS_FINISH:
                    if (obj != null && obj instanceof List) {
                        List results = (List) obj;
                        if (results.size() > 0) {
                            String temp_str = results.get(0).toString();
                            Log.v(TAG, "CLIENT_STATUS_FINISH:" + temp_str);
                            RecognizeData recognizeData = new Gson().fromJson(temp_str, RecognizeData.class);
                            echoRecorgByAction(recognizeData);
                        }
                    }
                    updateRecognitionResult(obj);
                    postStarRecorg();
                    break;
                // 用户取消
                case VoiceRecognitionClient.CLIENT_STATUS_USER_CANCELED:
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onError(int errorType, int errorCode) {
            setResult("onError:errorType:" + errorType);
            postStarRecorg();
        }

        @Override
        public void onNetworkStatusChange(int status, Object obj) {
            // 这里不做任何操作不影响简单识别
        }
    }

    private void updateRecognitionResult(Object obj) {
    }

    private void setResult(String result) {

    }


    @Override
    public void onDestroy() {
        speechRecognizer.destroy();
        stopRecorg();
        super.onDestroy();
    }

    private void stopRecorg() {
        if (recordThread != null) {
            recordThread.release();
            recordThread = null;
        }
    }


    @Override
    public void addOnRecognizeListener(OnRecognizeListener listener) {

    }

    @Override
    public void removeOnRecognizeListener(OnRecognizeListener listener) {

    }

    public List<OnRecognizeListener> listeners = new ArrayList<OnRecognizeListener>();


    private void print(String msg) {
        Log.d(TAG, "----" + msg);
    }

}
