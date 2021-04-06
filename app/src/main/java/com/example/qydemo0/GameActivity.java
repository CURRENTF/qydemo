package com.example.qydemo0;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.QYFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameActivity extends Activity {

    private SurfaceView sf;
    private Camera.Size mSize = null;//相机的尺寸
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;//默认后置摄像头
    private static final SparseIntArray orientations = new SparseIntArray();//手机旋转对应的调整角度
    private Camera camera;
    private String path_cur;
    private ImageView img_tem;
    private QYFile cur_file_manager = new QYFile();

    static {
        orientations.append(Surface.ROTATION_0, 90);
        orientations.append(Surface.ROTATION_90, 0);
        orientations.append(Surface.ROTATION_180, 270);
        orientations.append(Surface.ROTATION_270, 180);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        initCamera();
        findViewById(R.id.start_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, new Camera.PictureCallback() {//开始拍照；
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {//拍完之后回调；
                        String path = null;

                        if (savephoto(data)) {
                            Toast.makeText(GameActivity.this, "save photo success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(GameActivity.this, "save photo fail", Toast.LENGTH_LONG).show();
                        }

                    }
                });
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

        Log.i("123",""+mSize.width+mSize.height);

        RelativeLayout.LayoutParams sf_fill = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams sf_tiny = new RelativeLayout.LayoutParams(1,1);
        sf.setLayoutParams(sf_tiny);
        Button mirrors = (Button) findViewById(R.id.mirror);
        mirrors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mirrors.getText().equals("镜子")){
                    sf.setLayoutParams(sf_fill);
                    mirrors.setText("恢复");
                }
                else{
                    sf.setLayoutParams(sf_tiny);
                    mirrors.setText("镜子");
                }
            }
        });

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

    private Boolean savephoto(byte[] bytes) {
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
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public class SendPic extends AsyncTask<String, Void, Integer>{
        @Override
        protected void onPostExecute(Integer score) {
            super.onPostExecute(score);
            Log.i("user_score",""+score);
        }

        @Override
        protected Integer doInBackground(String... paths) {
            int score = 0;
            JSONObject res_json = cur_file_manager.verifyFileUpload(Constant.mInstance.file_upload_verify_url,0,cur_file_manager.hashFileUrl(paths[0]));
            try {
                if(!res_json.getBoolean("rapid_upload")){
                    if(!cur_file_manager.uploadFile(Constant.mInstance.file_upload_callback_url,paths[0],res_json.getString("token")))
                        return null;
                }

                /*
                GAME照片
                 */
                return score;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}