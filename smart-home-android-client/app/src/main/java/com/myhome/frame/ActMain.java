package com.myhome.frame;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiliuer.myhome.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhome.recorder.RecognizeService;
import com.myhome.recorder.RecognizeServiceHandler;
import com.myhome.recorder.json.RecognizeData;

@ContentView(R.layout.act_main)
public class ActMain extends FragmentActivity implements ServiceConnection, RecognizeServiceHandler.OnRecognizeListener {
    public final String TAG = getClass().getSimpleName();

    private RecognizeServiceHandler handler;

    @ViewInject(android.R.id.tabhost)
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        initTabs();

        //bindService(new Intent(this, RecognizeService.class), this, Service.BIND_AUTO_CREATE);
    }

    private final void initTabs() {
        LayoutInflater inflater = getLayoutInflater();
        for (Tab t : Tab.TABS) {
            View tabView = inflater.inflate(R.layout.tab, null);
            ((ImageView) tabView.findViewById(R.id.img))
                    .setImageResource(t.resImg);
            ((TextView) tabView.findViewById(R.id.txt)).setText(t.resTxt);
            mTabHost.addTab(mTabHost.newTabSpec(t.tabId).setIndicator(tabView),
                    t.fraCls, null);
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        RecognizeService.RecorgBinder binder = (RecognizeService.RecorgBinder) service;
        handler = binder.getHandler();
        handler.addOnRecognizeListener(this);
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (handler != null) {
            handler.removeOnRecognizeListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        //unbindService(this);
        super.onDestroy();
    }

    // 这里发送数据
    @Override
    public void onRecognizeSuccess(RecognizeData recorgData) {
    }


}
