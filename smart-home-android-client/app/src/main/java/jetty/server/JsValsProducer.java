package jetty.server;

import android.content.Context;
import android.os.Build;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.litesuits.common.io.FileUtils;
import com.myhome.utils.Constants;
import com.myhome.utils.DroidUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/25 0025.
 */
public class JsValsProducer {

    private Map<String, Object> datas;

    public JsValsProducer(Context context) {
        datas = Maps.newHashMap();
        datas.put("IP", DroidUtils.getIp(context));
        datas.put("PORT", Constants.PORT_HTTP_SERVER);
        datas.put("MODEL", Build.MODEL);
    }

    public void write(File jsFile) throws IOException {
        FileUtils.write(jsFile, "var G_CONFIGS=" + new Gson().toJson(datas));
    }
}
