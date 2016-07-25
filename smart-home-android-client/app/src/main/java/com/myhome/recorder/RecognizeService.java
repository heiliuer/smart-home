package com.myhome.recorder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.baidu.speech.VoiceRecognitionService;
import com.baidu.voicerecognition.android.DataUploader;
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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startVoiceRecorg();
            }
        }, 4000);
        mASREngine = VoiceRecognitionClient.getInstance(this);
        mASREngine.setTokenApis(Constants.API_KEY, Constants.SECRET_KEY);
        uploadContacts();
    }

    private void onRecognizeSuccess(RecognizeData recorgData) {
        List<String> item = recorgData.getContent().getItem();
        Log.i(TAG, "parseDat:" + new Gson().toJson(item));
        ComService cs = ServiceMain.getServiceMain().getComService();
        if (item.size() > 0) {
            String key = item
                    .get(0);
            if (key.indexOf("开灯") != -1) {
                cs.send(new byte[]{(byte) 0xa1, (byte) 0xff});
            } else if (key.indexOf("关灯") != -1) {
                cs.send(new byte[]{(byte) 0xa1, (byte) 0});
            }
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


    private void startVoiceRecorg() {
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
                    updateRecognitionResult(obj);
                    break;
                // 处理连续上屏
                case VoiceRecognitionClient.CLIENT_STATUS_UPDATE_RESULTS:
                    updateRecognitionResult(obj);
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
            setResult();
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

    public boolean onStartListening() {
        VoiceRecognitionConfig config = new VoiceRecognitionConfig();
        config.setProp(Config.CURRENT_PROP);
        config.setLanguage(Config.getCurrentLanguage());
        config.enableContacts(); // 启用通讯录
        config.enableVoicePower(Config.SHOW_VOL); // 音量反馈。
        if (Config.PLAY_START_SOUND) {
            config.enableBeginSoundEffect(R.raw.bdspeech_recognition_start); // 设置识别开始提示音
        }
        if (Config.PLAY_END_SOUND) {
            config.enableEndSoundEffect(R.raw.bdspeech_speech_end); // 设置识别结束提示音
        }
        config.setSampleRate(VoiceRecognitionConfig.SAMPLE_RATE_8K); // 设置采样率,需要与外部音频一致
        // 下面发起识别
        int code = mASREngine.startVoiceRecognition(mListener, config);
        if (code != VoiceRecognitionClient.START_WORK_RESULT_WORKING) {
            setResult(getString(R.string.error_start));
        }

        return code == VoiceRecognitionClient.START_WORK_RESULT_WORKING;
    }

    public boolean onCancel() {
        mASREngine.stopVoiceRecognition();
        return true;
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


    /**
     * 上传通讯录
     */
    private void uploadContacts() {
        DataUploader dataUploader = new DataUploader(RecognizeService.this);
        dataUploader.setApiKey(Constants.API_KEY, Constants.SECRET_KEY);

        String jsonString = "[{\"name\":\"兆维\", \"frequency\":1}, {\"name\":\"林新汝\", \"frequency\":2}]";
        try {
            dataUploader.uploadContactsData(jsonString.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
