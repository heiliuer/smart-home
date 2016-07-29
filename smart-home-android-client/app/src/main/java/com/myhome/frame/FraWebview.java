package com.myhome.frame;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.heiliuer.myhome.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.InjectFragment;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhome.utils.Constants;
import com.myhome.utils.DroidUtils;
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
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ServiceMain.getServiceMain().getComService().send(new byte[]{(byte) 0xaa, (byte) 0xff});
            }
        }, 100);

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
        View view = getActivity().getLayoutInflater().inflate(R.layout.dia_qrcode, null);
        dialogBuilder.setView(view);
        ((TextView) view.findViewById(R.id.txt_open_url)).setText(getUrl());
        try {
            ((ImageView) view.findViewById(R.id.img_qrcode)).setImageBitmap(QrcodeUtils.getQrcode(url));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        TextView customTitleView = new TextView(getActivity());
        customTitleView.setGravity(Gravity.CENTER);
        customTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);
        customTitleView.setText(R.string.qrcode_comment);
        dialogBuilder.setCustomTitle(customTitleView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    private String getUrl() {
        return "http://" + DroidUtils.getIp(getActivity()) + ":" + Constants.PORT_HTTP_SERVER + "/index.html";
    }


}
