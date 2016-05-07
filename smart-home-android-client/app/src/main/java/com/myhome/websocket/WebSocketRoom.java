
package com.myhome.websocket;

import android.util.Log;

import com.google.gson.Gson;
import com.myhome.frame.ServiceMain;
import com.myhome.service.ComService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class WebSocketRoom implements ServiceMain.OnDataIOListener {
    private static final String TAG = "WebSocketRoom";
    private final ComService comService;
    private int mIdCounter = 0;
    private Map<ClientWebSocket, Integer> mUsers = new ConcurrentHashMap<ClientWebSocket, Integer>();

    public WebSocketRoom(ComService comService) {
        this.comService = comService;
        comService.send(new byte[]{(byte) 0xaa, (byte) 0xff});
    }

    // WebSocketコネクションを登録する
    // ついでに適当なIDを割り当てる
    public synchronized void join(final ClientWebSocket ws) {
        if (mUsers.containsKey(ws)) {
            return;
        }
        mIdCounter++;
        mUsers.put(ws, mIdCounter);
        Log.d(TAG, "login " + mIdCounter);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    ws.send(new Gson().toJson(status));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // 登録されていたコネクションを取り除く
    public synchronized void leave(ClientWebSocket ws) {
        int id = mUsers.get(ws);
        mUsers.remove(ws);
        Log.d(TAG, "logoff " + id);
    }

    // WebSocketからメッセージが届いたら付加情報を付けて全コネクションに流す
    public void onMessage(ClientWebSocket ws, String msg) {

        Log.d(TAG, msg);
        int id = mUsers.get(ws);

//        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//        msg = id + ": " + msg + " " + fmt.format(new Date());
//        sendAll(msg);


        try {
            status = new Gson().fromJson(msg, LightsStatus.class);
            byte newData = status.writeStatus(data);
            sendData(newData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData(byte data) {
        comService.send(new byte[]{(byte) 0xa1, (byte) (data + 0)});
    }


    // 抱えてる全コネクションに流す
    public void sendAll(String msg) {
        for (ClientWebSocket socket : mUsers.keySet()) {
            try {
                socket.send(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte data = (byte) 0xff;

    @Override
    public void onReadData(byte[] datas) {
        status.readStatus(datas[1]);
        this.data = datas[1];

        new Thread() {
            @Override
            public void run() {
                super.run();
                sendAll(new Gson().toJson(status));
            }
        }.start();

    }

    @Override
    public void onWriteData(byte[] datas) {
    }

    private LightsStatus status = new LightsStatus();


    class LightsStatus {
        boolean deskLightOn = true;
        boolean floorLightOn = true;

        public boolean isDeskLightOn() {
            return deskLightOn;
        }

        public void setDeskLightOn(boolean deskLightOn) {
            this.deskLightOn = deskLightOn;
        }

        void readStatus(byte data) {
            deskLightOn = (1 & data) != 0;
            floorLightOn = (2 & data) != 0;
        }

        byte writeStatus(byte data) {
            if (deskLightOn) {
                data = (byte) (1 | data);
            } else {
                data = (byte) (0xfe & data);
            }
            if (floorLightOn) {
                data = (byte) (2 | data);
            } else {
                data = (byte) (0xfd & data);
            }
            return data;
        }

    }
}
