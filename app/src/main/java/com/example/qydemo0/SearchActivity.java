package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.HttpCounter;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.Widget.QYScrollView;
import com.example.qydemo0.Widget.ListItem.WorkItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends MyAppCompatActivity {

    private int startPos = 0, len = Constant.mInstance.MAX_UPDATE_LEN;
    public EditText search_txt = null;
    private LinearLayout qyScrollView = null;
    private QYScrollView qysv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        GlobalVariable.mInstance.appContext = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        search_txt = findViewById(R.id.edit_text_search);
        search_txt.requestFocus();
        search_txt.setOnKeyListener(new SearchKey());
        search_txt.addTextChangedListener(new TxtChange());
        ConstraintLayout c = findViewById(R.id.container_search);
        c.setOnClickListener(new RemoveFocus());
        qyScrollView = findViewById(R.id.list_for_search);
        qysv = findViewById(R.id.search_scroll);
        qysv.setScanScrollChangedListener(new LazyLoad());
    }

    /**
     * ????????????
     *
     * @param et ????????????
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * ????????????
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    class RemoveFocus implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            search_txt.clearFocus();
        }
    }

    class LazyLoad implements QYScrollView.ISmartScrollChangedListener{

        @Override
        public void onScrolledToBottom() {
            Search s = new Search(SearchActivity.this);
            s.execute(search_txt.getText().toString());
        }

        @Override
        public void onScrolledToTop() {

        }
    }


    class SearchKey implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                qyScrollView.removeAllViews();
                Search s = new Search(SearchActivity.this);
                s.execute(((EditText)findViewById(R.id.edit_text_search)).getText().toString());
                search_txt.clearFocus();
                hideInput();
                return true;
            }
            return false;
        }
    }

    class TxtChange implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            startPos = 0;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    HttpCounter counter = new HttpCounter();
    class Search extends MyAsyncTask<String, Integer, String> {

        protected Search(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            String txt = strings[0];
            if(txt == null || txt.equals("")) return null;
            QYrequest htp = new QYrequest();
            String[] data = {"text", "string", txt,
                    "start", "int", String.valueOf(counter.start), "lens", "int", String.valueOf(counter.len)};
            return htp.advancePost(
                    GenerateJson.universeJson2(data),
                    Constant.mInstance.search_url,
                    "Authorization", GlobalVariable.mInstance.token
            );
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null) return;
            Log.d("hjt.search", s);
            JSONArray ja = MsgProcess.msgProcessArr(s, false, null);
            counter.inc(ja.length());
            for(int i = 0; i < ja.length(); i++){
                try {
                    JSONObject json = (JSONObject)ja.get(i);
                    Log.d("hjt.search.json", json.toString());
                    WorkItem workItem = new WorkItem(SearchActivity.this);
                    workItem.setOnClick();
                    JSONObject coverInfo = json.getJSONObject("cover");
                    workItem.init(coverInfo.getString("url"), json.getString("name"),
                            json.getInt("like_num"), json.getInt("play_num"),
                            json.getString("introduction"), json.getJSONObject("belong").getString("username"), json.getInt("id"));
                    qyScrollView.addView(workItem);
                    qyScrollView.addView(Img.linearLayoutDivideLine(SearchActivity.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}