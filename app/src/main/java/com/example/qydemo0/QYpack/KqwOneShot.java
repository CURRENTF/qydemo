package com.example.qydemo0.QYpack;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.example.qydemo0.utils.JsonParser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class KqwOneShot{
    private String TAG = "ivw";
    private Toast mToast;
    private TextView textView;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 语音识别对象
    private SpeechRecognizer mAsr;
    // 唤醒结果内容
    private String resultString;
    // 识别结果内容
    private String recoString;
    private int curThresh = 1450;
    // 本地语法id
    private String mLocalGrammarID;
    // 本地语法文件
    private String mLocalGrammar = null;
    // 本地语法构建路径
    private String grmPath;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_LOCAL;
    private Context mContext;
    private Handler mHandler;
    
    public KqwOneShot(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        StringBuffer param = new StringBuffer();
        param.append("appid="+"4f537480");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(mContext, param.toString());
        grmPath = mContext.getExternalCacheDir().getPath()
                + "/msc/test";

        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(mContext, null);
        // 初始化识别对象---唤醒+识别,用来构建语法
        mAsr = SpeechRecognizer.createRecognizer(mContext, null);
        // 初始化语法文件
        mLocalGrammar = readFile(mContext, "call.bnf", "utf-8");
        btn_grammar();
    }

    GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {

                mLocalGrammarID = grammarId;
                showTip("语法构建成功：" + grammarId);
                btn_oneshot();
            } else {
                showTip("语法构建失败,错误码：" + error.getErrorCode()+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    public void btn_oneshot(){
        // 非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            resultString = "";
            recoString = "";
            Log.i("whc_testOnehot",resultString);

            final String resPath = ResourceUtil.generateResourcePath(mContext, RESOURCE_TYPE.assets, "ivw/4f537480.jet");
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 设置识别引擎
            mIvw.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            // 设置唤醒资源路径
            mIvw.setParameter(ResourceUtil.IVW_RES_PATH, resPath);
            /**
             * 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
             * 示例demo默认设置第一个唤醒词，建议开发者根据定制资源中唤醒词个数进行设置
             */
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"
                    + curThresh);
            // 设置唤醒+识别模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "oneshot");
            // 设置返回结果格式
            mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
//
//				mIvw.setParameter(SpeechConstant.IVW_SHOT_WORD, "0");

            // 设置唤醒录音保存路径，保存最近一分钟的音频
            mIvw.setParameter( SpeechConstant.IVW_AUDIO_PATH, mContext.getExternalCacheDir().getPath()+"/msc/ivw.wav" );
            mIvw.setParameter( SpeechConstant.AUDIO_FORMAT, "wav" );

            if (!TextUtils.isEmpty(mLocalGrammarID)) {
                // 设置本地识别资源
                mIvw.setParameter(ResourceUtil.ASR_RES_PATH,
                        getResourcePath());
                // 设置语法构建路径
                mIvw.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
                // 设置本地识别使用语法id
                mIvw.setParameter(SpeechConstant.LOCAL_GRAMMAR,
                        mLocalGrammarID);
                mIvw.startListening(mWakeuperListener);
            } else {
                Log.i("whc_testOnehot","请先构建语法");
            }

        } else {
            Log.i("whc_testOnehot","唤醒未初始化");
        }
    }

    public void btn_grammar() {
        int ret = 0;

        mAsr.setParameter(SpeechConstant.PARAMS, null);
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // 设置引擎类型
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        // 设置资源路径
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        ret = mAsr.buildGrammar("bnf", mLocalGrammar, grammarListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.i("whc_testOnehot","语法构建失败,错误码：" + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        }
    }

    public void btn_stop(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.stopListening();
        } else {
            Log.i("whc_testOnehot","唤醒未初始化");
        }
    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 "+text);
                buffer.append("\n");
                buffer.append("【操作类型】"+ object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】"+ object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString =buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            showTip(resultString);
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
            mIvw.startListening(mWakeuperListener);
        }

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            Log.d(TAG, "eventType:"+eventType+ "arg1:"+isLast + "arg2:" + arg2);
            // 识别结果
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                RecognizerResult result = ((RecognizerResult)obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                String text = JsonParser.parseGrammarResult(result.getResultString(), mEngineType);
                showTip(text);
                int ind = text.indexOf("置信度");
                String content = text.substring(4,ind-1);
                Log.i("whc_", content);
                Integer scores = Integer.parseInt(text.substring(ind+4).trim());
                if(scores>=30){
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", content);
                    Message message = Message.obtain();
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                mIvw.startListening(mWakeuperListener);
            }
        }

        @Override
        public void onVolumeChanged(int volume) {
            // TODO Auto-generated method stub

        }

    };

    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取识别资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        // 识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext,
                RESOURCE_TYPE.assets, "asr/common.jet"));
        return tempBuffer.toString();
    }

    private void showTip(final String str) {
        Log.i("whc_oneshot", str);
    }

}
