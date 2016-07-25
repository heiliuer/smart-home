package com.myhome.recorder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.baidu.speech.VoiceRecognitionService;
import com.google.gson.Gson;
import com.heiliuer.myhome.R;
import com.myhome.frame.ServiceMain;
import com.myhome.recorder.json.RecognizeData;
import com.myhome.service.ComService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecognizeServiceBak extends Service implements RecognitionListener, RecognizeServiceHandler, RecognizeServiceHandler.OnRecognizeListener {
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


    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler() {
        };
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);

        addOnRecognizeListener(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startVoiceRecorg();
            }
        },4000);

    }

    @Override
    public void onRecognizeSuccess(RecognizeData recorgData) {
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
            return RecognizeServiceBak.this;
        }
    }


    private void startVoiceRecorg() {
//        stopRecorg();
//        recordThread = new RecordThread(new RecordThread.OnRecorgVoice() {
//            @Override
//            public void onRecorgVoice() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        userStart();
//                    }
//                });
//            }
//        }, this);
//        Log.v("myTag", "startRecorg");
//        recordThread.start();
        userStart();
    }

    private void userStart() {
        switch (status) {
            case STATUS_None:
                start();
                status = STATUS_WaitingReady;
                break;
            case STATUS_WaitingReady:
                cancel();
                status = STATUS_None;
                break;
            case STATUS_Ready:
                cancel();
                status = STATUS_None;
                break;
            case STATUS_Speaking:
                stop();
                status = STATUS_Recognition;
                break;
            case STATUS_Recognition:
                cancel();
                status = STATUS_None;
                break;
        }

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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            onResults(data.getExtras());
//        }
//    }

    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean tips_sound = sp.getBoolean("tips_sound", true);
        boolean tips_sound = true;
        if (tips_sound) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }

//        boolean extra_infile = sp.contains(Constant.EXTRA_INFILE);
        boolean extra_infile = false;
        if (extra_infile) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }

//        boolean extra_outfile = sp.getBoolean(Constant.EXTRA_OUTFILE, false);
        boolean extra_outfile = false;
        if (extra_outfile) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }

//        boolean extra_sample = sp.contains(Constant.EXTRA_SAMPLE);
        boolean extra_sample = false;
        if (extra_sample) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }

//        boolean extra_language = sp.contains(Constant.EXTRA_LANGUAGE);
        boolean extra_language = false;
        if (extra_language) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }

        boolean extra_nlu = sp.contains(Constant.EXTRA_NLU);
        if (extra_nlu) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        boolean extra_vad = sp.contains(Constant.EXTRA_VAD);
        if (extra_vad) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }

        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            intent.putExtra(Constant.EXTRA_LICENSE_FILE_PATH, "/sdcard/easr/license-tmp-20150530.txt");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    private void start() {
        print("点击了“开始”");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        {

            String args = sp.getString("args", "");
            if (null != args) {
                print("参数集：" + args);
                intent.putExtra("args", args);
            }
        }
        speechEndTime = -1;
        speechRecognizer.startListening(intent);
    }

    private void stop() {
        speechRecognizer.stopListening();
        print("点击了“说完了”");
    }

    private void cancel() {
        speechRecognizer.cancel();
        status = STATUS_None;
        print("点击了“取消”");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
        print("准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        status = STATUS_Speaking;
        print("检测到用户的已经开始说话");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        status = STATUS_Recognition;
        print("检测到用户的已经停止说话");
    }


    @Override
    public void onError(int error) {
        status = STATUS_None;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        print("识别失败：" + sb.toString());
        startVoiceRecorg();
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        status = STATUS_None;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            print("origin_result=\n" + new JSONObject(json_res).toString(4));

            RecognizeData recorgData = new Gson().fromJson(json_res, RecognizeData.class);
            for (OnRecognizeListener listener :
                    listeners) {
                listener.onRecognizeSuccess(recorgData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            print("origin_result=[warning: bad json]\n" + json_res);
        }
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        // txtResult.setText(nbest.get(0) + strEnd2Finish);
        startVoiceRecorg();
    }


    public List<OnRecognizeListener> listeners = new ArrayList<OnRecognizeListener>();


    @Override
    public void addOnRecognizeListener(OnRecognizeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeOnRecognizeListener(OnRecognizeListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }


    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            // txtResult.setText(nbest.get(0));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                print("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }

    private void print(String msg) {
        Log.d(TAG, "----" + msg);
    }
}
