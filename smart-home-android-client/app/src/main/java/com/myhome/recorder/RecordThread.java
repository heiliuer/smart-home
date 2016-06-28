package com.myhome.recorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.heiliuer.myhome.R;

/**
 * Created by Administrator on 2016/5/2 0002.
 */
public class RecordThread extends Thread implements MediaPlayer.OnCompletionListener {
    private AudioRecord recorder;
    private Context context;
    private OnRecorgVoice onRecorgVoice;
    private final int min;

    //4s延时
    private long repareTime = 1000 * 4, beginTime;
    private MediaPlayer mediaPlayer;


    public RecordThread(OnRecorgVoice onRecorgVoice, Context context) {
        super();
        this.context = context;
        this.onRecorgVoice = onRecorgVoice;
        int sampleRateInHz = 8000;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;

//        int sampleRateInHz =6000;
//        int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
//        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
//        int audioSource = MediaRecorder.AudioSource.MIC;

        min = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        recorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig,
                audioFormat, min);

        mediaPlayer = MediaPlayer.create(context, R.raw.bdspeech_recognition_start);
        mediaPlayer.setOnCompletionListener(this);

    }

    @Override
    public synchronized void start() {
        running = true;
        beginTime = System.currentTimeMillis();
        super.start();
    }

    @Override
    public void run() {
        super.run();
        byte[] buffer = new byte[min], bytes_pkg;//越大，延时越大
        try {
            recorder.startRecording();
            int maxVolumeCount = 5;
            int count = 0;
            int maxVolumeSum = 0;
            F1:
            while (running) {
//                int size = recorder.read(buffer, 0, buffer.length);
//                int hightCount = 0;
//                Log.v("myLog", "buffer:" + Arrays.toString(buffer));
//                for (int i = 0; i < size; i++) {
//                    if (buffer[i] > 60) {
//                        hightCount++;
//                        if (hightCount > size * 0.2) {
//                            if (onRecorgVoice != null) {
//                               // onRecorgVoice.onRecorgVoice();
//                               // break F1;
//                            }
//                        }
//                    }
//                }

                //从内存获取数据
                int r = recorder.read(buffer, 0, buffer.length);
                bytes_pkg = buffer.clone();


                //音量
                int maxVol = getVolumeMax(r, bytes_pkg);


//                Log.v("myLog", "maxVolume:" + maxVol);
                int v = getVolume(r, bytes_pkg);


                if (System.currentTimeMillis() - beginTime >= repareTime) {
                    //如果音量大于最小值 打开开关
                    count++;
                    maxVolumeSum += maxVol;

                    //发现几个声音的平均值大就开始
                    if (count >= maxVolumeCount) {
                        int maxVolumeAverage = maxVolumeSum / count;
                        if (maxVolumeAverage > 1000) {
                            recorder.stop();
                            recorgVoice();
                            break F1;
                        }
                        count = 0;
                        maxVolumeSum = 0;
                    }

                    //发现一个大声音就开始
//                    if (maxVol > socketVol) {
//                        if (onRecorgVoice != null) {
//                            onRecorgVoice.onRecorgVoice();
//                            break F1;
//                        }
//                    }
                }

                //如果开关为打开状态，开始计时，计时大约1个周期的时候 ，关闭开关，停止计时，停止发送数据
//                if (flagVol) {
//                    m_in_q.add(bytes_pkg);
//                    volTime++;
//                    if ((volTime / cycle) > 0) {
//                        flagVol = false;
//                        volTime = 0;
//                    }
//                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recorder.release();
        }

    }

    private void recorgVoice() {
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onRecorgVoice.onRecorgVoice();
    }

    private final int socketVol = 2000;

    private int getVolume(int r, byte[] bytes_pkg) {
        //way 1
        int v = 0;
//      将 buffer 内容取出，进行平方和运算
        for (byte aBytes_pkg : bytes_pkg) {
            // 这里没有做运算的优化，为了更加清晰的展示代码
            v += aBytes_pkg * aBytes_pkg;
        }
//      平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
//      如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。
        int volume = (int) (v / (float) r);
        return volume;
    }

    private int getVolumeMax(int r, byte[] bytes_pkg) {

        //way 2
        int mShortArrayLenght = r / 2;
        short[] short_buffer = byteArray2ShortArray(bytes_pkg, mShortArrayLenght);
        int max = 0;
        if (r > 0) {
            for (int i = 0; i < mShortArrayLenght; i++) {
                if (Math.abs(short_buffer[i]) > max) {
                    max = Math.abs(short_buffer[i]);
                }
            }
        }
        return max;
    }


    private short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    public interface OnRecorgVoice {
        public void onRecorgVoice();
    }


    private boolean running = false;

    public void release() {
        running = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
