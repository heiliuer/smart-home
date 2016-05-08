package com.myhome.frame;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.heiliuer.myhome.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.InjectFragment;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhome.utils.Constants;
import com.myhome.utils.QrcodeUtils;

@ContentView(R.layout.fra_webview)
public class FraWebview extends InjectFragment {
    @ViewInject(R.id.webview)
    private WebView webView;

    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        url = getUrl();
    }

    private View initViews(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        if (rootView == null) {
            super.onCreateView(inflater, container, savedInstanceState);
            ViewUtils.inject(this);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.loadUrl(url);
        } else {
            ((ViewGroup) rootView.getParent()).removeAllViews();
        }
        return rootView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initViews(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fra_webview, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showQrcodeDialog();
        return super.onOptionsItemSelected(item);
    }


    private void showQrcodeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.qrcode_comment);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dia_qrcode, null);
        dialogBuilder.setView(view);
        try {
            ((ImageView) view.findViewById(R.id.img_qrcode)).setImageBitmap(QrcodeUtils.getQrcode(url));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        dialogBuilder.create().show();
    }


    private String getUrl() {
        return "http://" + getIp() + ":" + Constants.PORT_HTTP_SERVER + "/index.html";
    }


    private String getIp() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }
}
