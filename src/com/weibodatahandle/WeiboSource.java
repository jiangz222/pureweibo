package com.weibodatahandle;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * ΢����Դ
 * User: return
 * Date: 13-3-3
 * Time: ����1:05
 * To change this template use File | Settings | File Templates.
 */
public class WeiboSource{
    private static final String TAG = "test";
    private String text;

    public void parser(String data){
        Log.i(TAG, "���ڽ���Source json���ݣ�" + data);
        JSONTokener jsonToken = new JSONTokener(data);
        try {
            JSONObject jsonObject = (JSONObject) jsonToken.nextValue();
            this.setText(jsonObject.getString("text"));
        } catch (JSONException e) {
            Log.i(TAG,"Source json ���ݽ�������"+e.getMessage());
        }

    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
