package com.example.qydemo0;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.qydemo0.QYpack.AudioPlayer;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.KqwOneShot;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.Widget.QYDIalog;
import com.example.qydemo0.utils.SoundTipUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameActivity extends MyAppCompatActivity {

    private SurfaceView sf;
    private Camera.Size mSize = null;//相机的尺寸
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;//默认后置摄像头
    private static final SparseIntArray orientations = new SparseIntArray();//手机旋转对应的调整角度
    private Camera camera;
    private String path_cur;
    private ImageView img_tem;
    private QYFile cur_file_manager = new QYFile();
    private ImageView template_image, user_image;
    private Handler mHandler;
    private int cur_alpha=0;
    private int cur_img_ind = 0;
    private List<String> img_list = new ArrayList<>(), diffi_list = new ArrayList<>();
    private List<Integer>  gid_list = new ArrayList<>();
    private RelativeLayout.LayoutParams sf_fill, sf_tiny;
    private ImageView mirrors;
    private int mode = -1;
    private boolean is_ready = false;
    private Handler handler;
    private int star_num;
    private boolean is_mirror = false;
    private QYDIalog qydIalog;
    private QYrequest cur_request = new QYrequest();
    private QYFile cur_file = new QYFile();
    private int guan;

    private TextView pass_rate;

    private KqwOneShot kqw;

    private String will_upload_img;

    static {
        orientations.append(Surface.ROTATION_0, 90);
        orientations.append(Surface.ROTATION_90, 0);
        orientations.append(Surface.ROTATION_180, 270);
        orientations.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        final Intent intent = getIntent();
        List<String> params = intent.getStringArrayListExtra("GameParams");
        mode = Integer.valueOf(params.get(0));
        initCamera();
        initUI();
        if(mode == 0){
            guan = Integer.valueOf(params.get(1));
            new getTZImageList(this).execute(guan);
        }
        else if(mode == 2 || mode == 3){
            img_list.add(params.get(1));
            diffi_list.add(mode==3?params.get(2):"未知");
            if(mode==2) will_upload_img=img_list.get(0);
            else gid_list.add(Integer.valueOf(params.get(3)));
            refresh_img();
        }
        else{
            new getImageList(this).execute();
        }
        initKqw();
        star_num = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        kqw.btn_stop();
    }

    private void initUI(){
        findViewById(R.id.start_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_game();
            }
        });

        sf = findViewById(R.id.sf);
        sf.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                stop();
            }
        });

        sf_fill = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sf_tiny = new RelativeLayout.LayoutParams(1,1);
        sf.setLayoutParams(sf_tiny);
        mirrors = (ImageView) findViewById(R.id.mirror);
        mirrors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!btn_mirrors(0)){
                    btn_mirrors(1);
                }
            }
        });
        template_image = findViewById(R.id.template_image);
        template_image.setImageResource(R.drawable.test_game);
        user_image = findViewById(R.id.user_image);
        user_image.setImageResource(R.drawable.test_game_user);
        user_image.setAlpha(cur_alpha);
        user_image.setVisibility(View.GONE);
        pass_rate = findViewById(R.id.pass_rate_content);
    }

    private void start_game(){
        if(is_ready){
            AudioPlayer audioPlayer = null;
            try {
                audioPlayer = new AudioPlayer(GameActivity.this, R.raw.count_number_5);
                audioPlayer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        camera.takePicture(null, null, new Camera.PictureCallback() {//开始拍照；
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {//拍完之后回调；
                                String res_path = savePhoto(data);
                                if (res_path!=null) {
                                    new SendPic(GameActivity.this).execute(res_path);
                                } else {
                                    Toast.makeText(GameActivity.this, "保存出错啦！", Toast.LENGTH_LONG).show();
                                    Toast.makeText(GameActivity.this, "保存出错啦！", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    }
                });
                audioPlayer.getMediaPlayer().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initKqw(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String date = bundle.getString("msg");
                if(date.equals("【打开】镜子")) {
                    if(btn_mirrors(1)) {
                        SoundTipUtil.soundTip(GameActivity.this, "好的");
                    }
                }
                else if(date.equals("【关闭】镜子")) {
                    if(btn_mirrors(0)) {
                        SoundTipUtil.soundTip(GameActivity.this, "好的");
                    }
                }
                }
        };
        kqw = new KqwOneShot(this, handler);
        kqw.btn_grammar();
    }

    private boolean btn_mirrors(int isi){
        if(!is_mirror && isi==1){
            sf.setLayoutParams(sf_fill);
            is_mirror = true;
            return true;
        }
        else if(is_mirror && isi==0){
            sf.setLayoutParams(sf_tiny);
            is_mirror = false;
            return true;
        }
        return false;
    }

    private void refresh_img(){
        user_image.setVisibility(View.GONE);
        if(cur_img_ind < img_list.size()){
            Glide.with(GameActivity.this)
                    .load(img_list.get(cur_img_ind))
                    .transform(/*new CenterInside(), */new RoundedCorners(20))
                    .into(template_image);
            pass_rate.setText(diffi_list.get(cur_img_ind));
            cur_alpha = 0;
            is_ready = true;
            stop();
            start();
            cur_img_ind++;
        } else {
            if(mode==0){
                new FinishTZ(this).execute(star_num);
            }
            else {
            cur_img_ind = 0;
            new getImageList(this).execute();
            }
        }
    }

    private void showResult(int cur_star_num){
        mHandler = new Handler();
        cur_alpha = 0;
        user_image.setAlpha(cur_alpha);
        user_image.setVisibility(View.VISIBLE);
        if(is_mirror){
        sf.setLayoutParams(sf_tiny);
        is_mirror = false;
        }
        mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cur_alpha += 5;
                        if (cur_alpha <= 255) {
                            user_image.setAlpha(cur_alpha);
                            mHandler.postDelayed(this, 40);
                        } else {
                            vibrate();
                            switch (mode) {
                                case 0:
                                    if(cur_star_num!=0) {
                                        star_num += cur_star_num;
                                        new GameDialog(GameActivity.this, new int[]{1}, cur_star_num, new lsr());
                                    } else {
                                        new GameDialog(GameActivity.this, new int[]{1,2}, cur_star_num, new lsr());
                                    }
                                    break;
                                case 1:
                                    if(cur_star_num!=0) {
                                        new GameDialog(GameActivity.this, new int[]{1}, cur_star_num, new lsr());
                                    }
                                    else {
                                        new GameDialog(GameActivity.this, new int[]{}, cur_star_num, new lsr());
                                    }
                                    break;
                                default:
                                    if(cur_star_num!=0){
                                        new GameDialog(GameActivity.this, new int[]{1, 2}, cur_star_num, new lsr());
                                    } else {
                                        new GameDialog(GameActivity.this, new int[]{2}, cur_star_num, new lsr());
                                    }
                                    break;
                            }
                        }
                    }
                });
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    private void initCamera() {
        if (Camera.getNumberOfCameras() == 2) { camera = Camera.open(mCameraFacing);
        } else {
            camera = Camera.open();
        }

        GameActivity.CameraSizeComparator sizeComparator = new GameActivity.CameraSizeComparator();
        Camera.Parameters parameters = camera.getParameters();

        if (mSize == null) {
            List<Camera.Size> vSizeList = parameters.getSupportedPreviewSizes();
            Collections.sort(vSizeList, sizeComparator);

            for (int num = 0; num < vSizeList.size(); num++) {
                Camera.Size size = vSizeList.get(num);

                if (size.width >= 800 && size.height >= 480) {
                    this.mSize = size;
                    break;
                }
            }
            mSize = vSizeList.get(0);

            List<String> focusModesList = parameters.getSupportedFocusModes();

            //增加对聚焦模式的判断
            if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            camera.setParameters(parameters);
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int orientation = orientations.get(rotation);
        camera.setDisplayOrientation(orientation);
    }

    public void start() {
        try {
            initCamera();
            camera.setPreviewDisplay(sf.getHolder());
            camera.startPreview();//开始预览画面
            camera.setDisplayOrientation(90);//拍摄画面旋转90度
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        camera.stopPreview();
        camera.release();
    }

    private String savePhoto(byte[] bytes) {
        try {
            path_cur = getExternalCacheDir().getPath();
            if (path_cur != null) {
                File dir = new File(path_cur + "/imgs");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                path_cur = dir + "/" + System.currentTimeMillis() + ".jpg";
                FileOutputStream fos = new FileOutputStream(path_cur);
                fos.write(bytes);
                fos.flush();
                fos.close();
                user_image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                return path_cur;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }

    private String upload_img(String path){
        String render_img_id = cur_file.uploadFileAllIn(Constant.mInstance.file_upload_verify_url, Img.compressWithUrl(path, GameActivity.this), 0, cur_file.hashFileUrl(path));
        return render_img_id;
    }

    private Boolean post_is_pass(int gid, int oper){
        try {
            String[] j = new String[0];
            JSONObject res_json = new JSONObject(cur_request.advancePut(GenerateJson.universeJson2(j), Constant.mInstance.game_url + "/free/"+gid+"/"+oper+"/", "Authorization", GlobalVariable.mInstance.token));
            if(res_json.getString("msg").equals("Success")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Boolean canInDataBase(String imgId){
        String[] callToJson = {"img","string",imgId};
        try {
            JSONObject res_json = new JSONObject(cur_request.advancePost(GenerateJson.universeJson2(callToJson),Constant.mInstance.game_url+"free/", "Authorization", GlobalVariable.mInstance.token));
            if(res_json.getString("msg").equals("Success")){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public class SendPic extends MyAsyncTask<String, Void, List<Integer> > {

        protected SendPic(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(List<Integer> score) {
            super.onPostExecute(score);
            Log.i("user_score",""+score);
            showResult(score.get(0));
        }

        @Override
        protected List<Integer> doInBackground(String... paths) {

            String cur_img_id = upload_img(paths[0]);
            int cur_star_num = 0;
            String[] callToJson;
            if(mode==2) {
                String cur_img_game_id = upload_img(will_upload_img);
                callToJson = new String[]{"img_game", "int", "" + cur_img_game_id,
                        "img", "string", cur_img_id};
            }
            else {
                callToJson = new String[]{"game", "int", "" + gid_list.get(cur_img_ind - 1),
                        "img", "string", cur_img_id};
            }
            try {
                JSONObject res_json = new JSONObject(cur_request.advancePost(GenerateJson.universeJson2(callToJson),Constant.mInstance.task_url+"/"+"game/", "Authorization", GlobalVariable.mInstance.token));
                Log.i("whc_res_json", String.valueOf(res_json));
                if(!res_json.getString("msg").equals("Success")) return null;
                String tid = res_json.getJSONObject("data").getString("tid");
                boolean isi = false;
                for(int i=0;i<10;i++){
                    JSONObject cur_res = new JSONObject(cur_request.advanceGet("https://api.yhf2000.cn/api/qingying/v1/task/schedule"+tid+"/","Authorization", GlobalVariable.mInstance.token));
                    Log.i("whc_cur_res", String.valueOf(cur_res));
                    if(!cur_res.getString("msg").equals("Success")) return null;
                    JSONObject cur_data = cur_res.getJSONObject("data");
                    if(cur_data.getBoolean("is_finish")){
                        isi = true;
                        cur_star_num = cur_data.getJSONObject("data").getInt("star");
                        break;
                    }
                    Thread.sleep(200);
                }
                if(!isi) return null;
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
            if(mode!=2) post_is_pass(Integer.valueOf(gid_list.get(cur_img_ind-1)), cur_star_num>0?1:0);
            if(cur_star_num > 0){
                switch (mode){
                    case 2:
                        canInDataBase(cur_img_id);
                        break;
                    default:
                        break;
                }
            }
            List<Integer> res_score = new ArrayList<>();
            res_score.add(cur_star_num);
            return res_score;
        }
    }

    public class getTZImageList extends MyAsyncTask<Integer, Void, Boolean >{

        protected getTZImageList(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            //获取闯关模式图片链接
            try {
                JSONObject cur_res = new JSONObject(cur_request.advanceGet("https://api.yhf2000.cn/api/qingying/v1/game/challenge/"+integers[0]+"/","Authorization", GlobalVariable.mInstance.token));
                if(!cur_res.getString("msg").equals("Success")) return false;
                JSONArray content_list = cur_res.getJSONArray("data");
                for(int i=0;i<content_list.length();i++){
                    JSONObject jo = content_list.getJSONObject(i);
                    gid_list.add(jo.getInt("gid"));
                    img_list.add(jo.getJSONObject("img").getString("url"));
                    diffi_list.add(jo.getString("pass_rate"));
                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
//            List<String > cur_img_list = new ArrayList<>();
//            cur_img_list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimage.biaobaiju.com%2Fuploads%2F20190508%2F18%2F1557312867-xFCRluDivm.jpeg&refer=http%3A%2F%2Fimage.biaobaiju.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623054775&t=eda060d1b9671a786cb1e5c41f75d61b");
//            cur_img_list.add("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAKcAfQDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+iiigAooooAKKKKACiiigAooooAKK53xnL4gg0B5/DYRr2Jw7RsoYuncDPeuO0X4x24kSy8R2MtldZw0iKSo+q9R+tAHqdFMhmjuIUmhdXjcblZTkEetPoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAqjqmsafolqLnUblbeEsEDMCck9uKl1C/t9L0+e+u5PLt4ELyNjOAK4LxFpFz4+srfVfDevL9kePY0EgzGxB9OzZ65FAGhrnxQ8P6XG0dpOL+7H3YoshfxbGP514/448a23iwRKdIgt7qJ8maOQlsehOOas+K/Bnivw/p739xILuNvlkeIbiue57gVwEMtup+cOG7nNAHqPw38c3Gi3kOl3ku/S5m2qWPMLHuD6eor3oEEZByK+R7eZScx4x717j8OfHKahbQ6NqDBbuNdsMhP+sA6A++PzoA9IooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKqajqllpFr9q1C5jtoNwXzJDgZPSgCS9tIb+ymtLlA8EyFHU9wRg14LZX+o/B3xnLp9yr3GiXTblP95ezD/aHQ16HqHxf8LWgkSGae6nRivlxxFcke7YrjfEPjPQPiJpU2j3sH9m3wO+wnmfKl/7pbA256elAHtFtc22pWMdxA6zW86BlYchga8n8efBtNSlfUPDuyGVuZLU8Kx9VPb6Vj/B/x2bC7/4RjUpf3LNi3djwj55X6H+de8UAfG+p6Ve6DffZr6NoZFGSrUWupSQzI8bsrA5DA4INelfHuBBr2nygfM9sQfwY/wCNeRQgg4oA+tPAGtza/wCD7O9uW3TjdHI3qVOM/liunrzb4KSs/g6dCeEuWx+IFek0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFc7420e713wzcWNklrJM5UhLpcowB5HsfeuiooA+ZtYtJdCl+y6pY3OnSEbRKj+Yn4ZGcD6muRvnuLh2tnninijOFuFHJH1719a63oWn+INNksdRt1licYBPVT6g9jXzF478JzeENWNpsfyW+aKXs6/40AVrbRYbbThctP5NwxWS1mGSGIYhlOOnY//AK6+kfAfiFvEPhqGS4GL2DEU49WHRh7Ec18z+Hb6Z3Nm4E0LZYwt0JA7Hscd6774XaqLLxn5QuXFrKhj8tuMk9Mj1FAEvx7bOu6cvcW5P/jxryFPvA16v8dmz4otVz0tR/M15THzQB9C/A6f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hzHFsPvtx/Q0AeXzXDSOzlQSTnJ5qu8jEEmOMjuSuKkYqF+cs5HYHiqk8rlwowoHYUAS21tPeTLFBEWZ2CgIMliewr6r+Evgibwf4aY3wAv7xhJInH7sY4XPr61wHwI8HQ35k8R6ghf7PJttlZfl3Yzu/DjFfQFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFeBfHa0RddtryTGz7MqqM8s25v5D+de+14P8AtARTnUdIdkH2bynAbtuB5B/DFAHiZDSZIGFH5Cs8Au5b1NaksoMUiqONp/lVCNDjODigD69+FUccXw00QRsGBhyxH97cciuyrzv4JXf2n4a2qEAG3mli/Xd/7NXolABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFcf8TfDv/CR+CL2BE3XEC/aIf8AeUcj8RmuwpCARg9KAPhroSpBz0xUULjBVuo4rtvit4W/4RbxrPHApWzu/wB/BjoATyPwOa4gLCZQxJAP3sGgD6Y+AbqfA10isSVvnJHplEr1SvK/gNGqeDLsxg+Wbw7WPf5Vr1SgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiisLxh4ltvCXhm81i5wfJXEcef8AWOeFX86APCf2h/ECXfiOy0WEofsUXmSsBzuf+HP0AP414t8x7mrmq6nc6zql1qN5IZLm5kMjse5NUwT2oA+o/wBn3U7m+8C3FtcNuWzujHEcYwpUHH5k16zXkX7O9v5fgO7nLEmW+fjHTCKK9doAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAr5v8A2idbvJfEdjom/bZRW4uNgP3nYsMn6Afqa+kDXyn8fHZ/iZMCeEtoVX6YJ/rQB5eeKQHmjtSUAfXnwTsfsXwu0wk5M7STH8WP9AK9Crlvhx/yTnw9wB/oMfT6V1NABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAH//Z");
        }

        @Override
        protected void onPostExecute(Boolean strings) {
            super.onPostExecute(strings);
            if(strings){
                refresh_img();
            } else{
                Toast.makeText(GameActivity.this, "请求新图失败！", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class getImageList extends MyAsyncTask<Void, Void, Boolean >{

        protected getImageList(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            is_ready = false;
            // get image list

            try {
                JSONObject cur_res = new JSONObject(cur_request.advanceGet("https://api.yhf2000.cn/api/qingying/v1/game/free/?lens=10","Authorization", GlobalVariable.mInstance.token));
                Log.i("whc_", String.valueOf(cur_res));
                if(!cur_res.getString("msg").equals("Success")) return false;
                JSONArray content_list = cur_res.getJSONArray("data");
                for(int i=0;i<content_list.length();i++){
                    JSONObject jo = content_list.getJSONObject(i);
                    gid_list.add(jo.getInt("gid"));
                    img_list.add(jo.getJSONObject("img").getString("url"));
                    diffi_list.add(jo.getString("pass_rate"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
//            List<String > cur_img_list = new ArrayList<>();
//            cur_img_list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimage.biaobaiju.com%2Fuploads%2F20190508%2F18%2F1557312867-xFCRluDivm.jpeg&refer=http%3A%2F%2Fimage.biaobaiju.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1623054775&t=eda060d1b9671a786cb1e5c41f75d61b");
//            cur_img_list.add("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAKcAfQDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+iiigAooooAKKKKACiiigAooooAKK53xnL4gg0B5/DYRr2Jw7RsoYuncDPeuO0X4x24kSy8R2MtldZw0iKSo+q9R+tAHqdFMhmjuIUmhdXjcblZTkEetPoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAqjqmsafolqLnUblbeEsEDMCck9uKl1C/t9L0+e+u5PLt4ELyNjOAK4LxFpFz4+srfVfDevL9kePY0EgzGxB9OzZ65FAGhrnxQ8P6XG0dpOL+7H3YoshfxbGP514/448a23iwRKdIgt7qJ8maOQlsehOOas+K/Bnivw/p739xILuNvlkeIbiue57gVwEMtup+cOG7nNAHqPw38c3Gi3kOl3ku/S5m2qWPMLHuD6eor3oEEZByK+R7eZScx4x717j8OfHKahbQ6NqDBbuNdsMhP+sA6A++PzoA9IooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKqajqllpFr9q1C5jtoNwXzJDgZPSgCS9tIb+ymtLlA8EyFHU9wRg14LZX+o/B3xnLp9yr3GiXTblP95ezD/aHQ16HqHxf8LWgkSGae6nRivlxxFcke7YrjfEPjPQPiJpU2j3sH9m3wO+wnmfKl/7pbA256elAHtFtc22pWMdxA6zW86BlYchga8n8efBtNSlfUPDuyGVuZLU8Kx9VPb6Vj/B/x2bC7/4RjUpf3LNi3djwj55X6H+de8UAfG+p6Ve6DffZr6NoZFGSrUWupSQzI8bsrA5DA4INelfHuBBr2nygfM9sQfwY/wCNeRQgg4oA+tPAGtza/wCD7O9uW3TjdHI3qVOM/liunrzb4KSs/g6dCeEuWx+IFek0AFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFc7420e713wzcWNklrJM5UhLpcowB5HsfeuiooA+ZtYtJdCl+y6pY3OnSEbRKj+Yn4ZGcD6muRvnuLh2tnninijOFuFHJH1719a63oWn+INNksdRt1licYBPVT6g9jXzF478JzeENWNpsfyW+aKXs6/40AVrbRYbbThctP5NwxWS1mGSGIYhlOOnY//AK6+kfAfiFvEPhqGS4GL2DEU49WHRh7Ec18z+Hb6Z3Nm4E0LZYwt0JA7Hscd6774XaqLLxn5QuXFrKhj8tuMk9Mj1FAEvx7bOu6cvcW5P/jxryFPvA16v8dmz4otVz0tR/M15THzQB9C/A6f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hzHFsPvtx/Q0AeXzXDSOzlQSTnJ5qu8jEEmOMjuSuKkYqF+cs5HYHiqk8rlwowoHYUAS21tPeTLFBEWZ2CgIMliewr6r+Evgibwf4aY3wAv7xhJInH7sY4XPr61wHwI8HQ35k8R6ghf7PJttlZfl3Yzu/DjFfQFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFeBfHa0RddtryTGz7MqqM8s25v5D+de+14P8AtARTnUdIdkH2bynAbtuB5B/DFAHiZDSZIGFH5Cs8Au5b1NaksoMUiqONp/lVCNDjODigD69+FUccXw00QRsGBhyxH97cciuyrzv4JXf2n4a2qEAG3mli/Xd/7NXolABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFcf8TfDv/CR+CL2BE3XEC/aIf8AeUcj8RmuwpCARg9KAPhroSpBz0xUULjBVuo4rtvit4W/4RbxrPHApWzu/wB/BjoATyPwOa4gLCZQxJAP3sGgD6Y+AbqfA10isSVvnJHplEr1SvK/gNGqeDLsxg+Wbw7WPf5Vr1SgAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiisLxh4ltvCXhm81i5wfJXEcef8AWOeFX86APCf2h/ECXfiOy0WEofsUXmSsBzuf+HP0AP414t8x7mrmq6nc6zql1qN5IZLm5kMjse5NUwT2oA+o/wBn3U7m+8C3FtcNuWzujHEcYwpUHH5k16zXkX7O9v5fgO7nLEmW+fjHTCKK9doAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAr5v8A2idbvJfEdjom/bZRW4uNgP3nYsMn6Afqa+kDXyn8fHZ/iZMCeEtoVX6YJ/rQB5eeKQHmjtSUAfXnwTsfsXwu0wk5M7STH8WP9AK9Crlvhx/yTnw9wB/oMfT6V1NABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFAH//Z");
////            cur_img_list.add("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCALjAfQDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3aiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAoopGJ4xQAtFAooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAZLMkKbnbA7VBHci4mRVBA2eZn1HasbxVepFahEJMmSrbedoI6n9Kzo9UureDzbcLwghAfgD/a4qW9S1HS509xqATEdsnnztkKgOBx1JPYVJYzTXEHmTIqknjFZekI0iD5zIT/rJAensMetbgAUAAYA7U0SxaKKKYgooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKCQBk0Ac34ktUitzOnDsSXZj29KxtLsTeSlnZnhDADI++2P4Qev17Vc8Sbrr9zl/nz+HOK3NFGbfY0Sj7NiJGHptGf1rPeRre0S5YWotLVY8AHqcetWaKK0MgooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigBEYOiuOjDIpaZCFEEYQYUKAAfTFPoAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACmSgmJwDg460+jtQwONu7aWe5jd3YMuQwHAbPeuqs4VhR9vR239enA/wrn5YnWZztIAkIJxzjP8AKukgG2FRnPA5qIlyZJRRRVkBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFACAAAAdBS0UUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQBgX7GDUZsg4cBl9+Of5VqadOJrVCM5AxzUs8Ecy/OgbHTPaiCJIo1WPhR2qUrMpvQmoooqiQooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAIyMVGmFJXv1qSqN7N5EiMQSGHY96TGtS9RVOyvEuVIHUdyetXKE7iasFFFFMAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAEU7lzjHJFLRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABVO+h+0QsmDuXkH0q5SYHJ9amSuhp2ZzVrc+QVwG4J2/4Gugt5xMg6B8ZK+lYOo24gvmK8B8OPrWhDJyrRlc4BP0qE+V2NGrq6NSiiitTIKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooApahYi7iyOJV+6f6VgRXNwsroiITkowJxg11hrC1KKO31FZSOJwP++h1/Ss5I0g+hswktChYYbAyM5wakqvZyCSJsfwtirFWiGFFFFMQUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFNT/Vr9BTqACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKADtWD4kYx2kLZO9Jcg+2K3qx/EVukunF3Qsq9cdfrUT2KhuGhTpLHLgksSCSfpWxXP6AVNkkytg7duPpW8rZ47jrTi9AktR1FFFUSFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAU2RRJGysMgjBFOrE1bxBFZym1t1E1yQcgHAX601FydkJyUdWUbG8ttPkEMhSNSxVFBxzVjVPEsGmWi3Ow8naPM4B9vrXluqa/8AZdemDO80iMUlAUAKTzgZ96gvNXuruaKMeabNCAyHqpJOc+/OK3p4ZXVyJ1r7HulrOLm2jnUgpIoZSO4NMvp5LW0knjVWMaltrHGfxrz7R9d1h2SFb0LEFG1fJXAUcY+tamparLJo89vf3TReVhzOqffGeBj9CO9P6u73urCdVLTqa9l4hnvFWb+zpI4CCSScsQPQCtyKVZolkXO1hkZGDXn41+5sNPit3lEzzkmKeNQFZDzjHbAIFa3h/VLme7gjmmJBUhlb+I+o9DTqUHZyjoiY1dbM62iiiuU3CiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiignAyelABRTUljkQPG6up6MpyKdkUAFFZNxrDQeIbLTPs77LiORzN2UrjA+prVV1bO1gcHBx60ALXF+KNNaC++3W5K7lLSL1BHRsD8q7SqOqQeda7tu4ockeqng/pWlKfLJMipHmjY8F8XwxJryX0GPIuY1DeoYDGfxFULW4kiTyw5KBt2ex6fjW/4xs1t0uYVAaHJeJieh7Af571w5mAjDFiMjp0rv3enqc61PSfD10oCH5T8xbdnnGcHFJ46vPMtdOtomO190rjPyn+EZrmdL1H7HpayEb855zzTdVv59SFvgl5JOEDHlFHrV4dcz12FXuS211dxTCKOYyqgATeeg64B+pNdTpVzOJ/MWYCRAsioDnac4Nc/FNb6ZB5b5LKOcDJ+v1rYgAjtvOtFRn25KSH5Wz1HHPPX6iuipFSjZHPHR3PaUOUU+opax/DOqxaxodvdQs5XGw71wdy8H9a2K8KSs7Hpxd1cKKKKQwooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooACQBk9BXHX/ji1g8RRWCXESwJ/rnJzn0Aqt8TvEMuk6H9ktZHSe4UkuhwVQda8F+0vFKGB3N97Ldxn+VNK4H1dbXMdzCsiH7wyAeuKra3DHcaNdxSvKkTRneYjhtvcZ+lcb4O8YWOoW8rszKYG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3RhJWdgooopiCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooARmCjn6UtFFABRRRQAVg+LXU6HcwSW1zIssTfPChbYR0yBzW9RQB87zNJKyrNuRsL8pUjHocduKoavBEbSOQSos+8IYWXBK4J3KR27EHngGvUvFvhvUdXkmfT9Ft4sMWkmLgSzEdMe2PU157daLqFnfw21/aG1knBWLz3VQ2MZ5zjjI/OtE7iscm8QJ3EgmtXStMe+hdUmhR1w4U8lh3rotD8CX2vFo4UtFjgnAmu0feGPcAg84B59a9Ys/A3h+xn8+DTollxywGRnGDgHpS5gseV21rJFH8iooA+THTjrmtG20u91Gf8A1cysvzK0aHj2r1ldJshMsv2WIMowDtHTj/Cp4ovKLncxBOcHoPpWctS4ux47K93p1w1nft+8UnGe49q29I1SW0iC+YT5bKIivICnoD6c1u+ONMjksjqGdrxYB6YYf0xXF27ukblZCBIB8jYBPoayN73Vz0C6e31q1aCbEF2owkgGDn2rlporpomhuIzmDIG3hkPqPY+lRW2oyiRIwzySAYJ2dR71veQNVQMSPMQZDI+WGeO306Gp3C1jHtn1IRBIJ5oty4kjAyjA8cg9OPSur8P2f/EnTTbi3hNtEnl7QOG+tM020vBtSeOPahyWA6n/AArookVEAVQo9BVxvtcibXY4bVvhVomoTvPb+ZaOeV8s8A/TtWP/AMIf4k0eTMZTUraMfIC+JPXqeuK9V4qKQ4BLHoM4HetJNvcyi7bHKeHtdmvZDY6jBLbalDkxieMqZF789GPuK65SSoJGDjkelcvfSTGTzkmkiYcjLcfiPxrz7xR4vvX020ie7eKWO4+W6hlMbqwzkMBwQRVU488lFBPRXPadw3bcjce2aWvFtJ8fT2Ai+0XD3YXq0hy59s16TofjDS9c+SJ2imzt8uXgnAzke1b1cNOnruYQrRlodBRRRXObBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAdBVK70/TdXhUXlnbXcak7fOiDhfXGe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WrpsOnrGy20Ucb/xp1INQ+IkFzp01vtJ+XqDgg9iPeuKTUdW0RI54iJV7hx27/SpbsVZtHXeLNCi1zQp7Z1jyBvVmGMFeevauA8OaHoM8AS3UvKcszTrkBvRcfnmrOr+PtTvoJLKG2jtUkGySbcScHghfesrwu/namLW3TL52xY4Bx2/StY16kFaLJ9lGWskdqfAMG1HglUHAJyvU+tKnhKWNDG6h1fg4Ofxrs4wViRT1CgU6tli6vVmLoQONtvBscm6O4XbFu3Z6lgecV0tppdvZqBEMBRgY4q7RWc605bsqNKMdgooorI0CiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAKtzGXQjqCOR61weqsGmnt1+YKSMgd8djXoTrkj1zXF6jCLfUbtRyM5UfXtUsqJzWlac2pX8VpGgLNy5I4AHrXZeG/D0dpey3booaEmGIgdhwT+PNU/CkUNzqjTqgUwqeR69Oa7VFCrgfrQhyY6iiiqICiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigApDS0negBGO1S3oK4nVCWkldCDIFIJwePSuzuGxHgZ3NwAK4ueMT3t4rMApkCjnpwM4qZFRNXwhavBYSyuhBlbIJ710lV7OIRW6oucDjk1YpoT3CiiimIKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACq87SK3DKq471Yqs5V53R8ZTDKPb3/ABzSYIg1F1MCSE/L1BHesG2UT3Tn7oaTIzkZHbnuKt6qpfEYmJByMA0/TRuuBtODwMY6Cp3L2RuQLthUd8VJQOlFWQFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAVFIoD7uORg8cmpaQ0MDJu9O3OHQ4JbOfSmWVsyTO4JBVsEgda0z0BYE7T2NMRvLySpOX6n3qbFXLVFFFUSFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUh6GlooApwXSS3E0Wf3kZGQeDzTLmN/JAyoO/cMnirC2sa3DzjO98Z564pJDklSm7njjP40h3C1laRRv8Avd+MVYqqkoS4VAOJMkHOatUIQUUUUwCiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAQDAxUMsbM5OQBjr3FT1FPkRkgkHHUUgM22W4OriWbhAGjQA9enOK16x4LtZb62LAq53A85zWxQhsKKKKYgooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKADvTJEEiFT0NO70bgaAMFYWsnt5XO4LcBc4x14/wrfqlqKlrRgrYYMpyRnoRV0dKSAKKKKYBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFMEYDbsn86fRQA3YuQccgYFOoooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAazhBk5644GadRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUE4ooAKKKKACiiigAooooAKKKKAENLRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRTHmRGVWOCxwBQA+ioxOplMeGyOpI4/OpKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACjHOaKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAP//Z");
////            cur_img_list.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3590955150,1412850945&fm=26&gp=0.jpg");
////            cur_img_list.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2333542448,1545334133&fm=26&gp=0.jpg");
//            return cur_img_list;
        }

        @Override
        protected void onPostExecute(Boolean strings) {
            super.onPostExecute(strings);
            if(strings){
                refresh_img();
            } else{
                Toast.makeText(GameActivity.this, "请求新图失败！", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class FinishTZ extends MyAsyncTask<Integer, Void, Boolean >{

        protected FinishTZ(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Integer... voids) {
            //发送挑战模式分数 voids[0]
            String[] callToJson = {"star","int", ""+star_num};
            try {
                JSONObject res_json = new JSONObject(cur_request.advancePost(GenerateJson.universeJson2(callToJson),Constant.mInstance.game_url+"challenge/"+guan+"/", "Authorization", GlobalVariable.mInstance.token));
                if(res_json.getString("msg").equals("Success")){
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean is_success) {
            super.onPostExecute(is_success);
            if(is_success){
            Toast.makeText(GameActivity.this, "恭喜你完成本关", Toast.LENGTH_LONG).show();
            }
            else{
            Toast.makeText(GameActivity.this, "出错啦！", Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(GameActivity.this, GameContentActivity.class);
            startActivity(intent);
        }
    }

    public class GameDialog {
        ImageView content;
        int[] ui = {R.id.content, R.id.retry, R.id.next, R.id.out};
        int[] star_img = {R.drawable.star0, R.drawable.star1, R.drawable.star2, R.drawable.star3};
        public GameDialog(Context context, int[] p, int star_num, QYDIalog.OnCenterItemClickListener lsr){
            qydIalog = new QYDIalog(context, R.layout.game_dialog, ui);
            qydIalog.show();
            content = qydIalog.findViewById(R.id.content);
            set_content(star_num);
            for(int i=0;i<p.length;i++){
                (qydIalog.findViewById(ui[p[i]])).setVisibility(View.GONE);
            }
            qydIalog.setOnCenterItemClickListener(lsr);
        }
        public void set_content(int star_num){
            content.setImageResource(star_img[star_num]);
        }
    }

    class lsr implements QYDIalog.OnCenterItemClickListener{
        @Override
        public void OnCenterItemClick(QYDIalog dialog, View view) {
            switch (view.getId()){
                case R.id.content:
                    break;
                case R.id.retry:
                    cur_img_ind--;
                    refresh_img();
                    qydIalog.dismiss();
                    break;
                case R.id.next:
                    refresh_img();
                    qydIalog.dismiss();
                    break;
                case R.id.out:
                    qydIalog.dismiss();
                    Intent intent = new Intent(GameActivity.this, GameContentActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

}