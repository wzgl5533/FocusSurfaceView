package com.cymaybe.foucssurfaceview.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cymaybe.foucssurfaceview.R;
import com.cymaybe.foucssurfaceview.fragment.PictureFragment;
import com.cymaybe.foucsurfaceview.FocusSurfaceView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.cymaybe.foucssurfaceview.fragment.PictureFragment.CROP_PICTURE;
import static com.cymaybe.foucssurfaceview.fragment.PictureFragment.ORIGIN_PICTURE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "wzgl5533";
    private MainActivity instance;
    private FocusSurfaceView previewSFV;
    private Button mTakeBT, mThreeFourBT, mFourThreeBT, mNineSixteenBT, mSixteenNineBT, mFitImgBT, mCircleBT, mFreeBT, mSquareBT,
            mCircleSquareBT, mCustomBT;

    private Camera mCamera;
    private int cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;//1:采集指纹的前置摄像头. 0:拍照的后置摄像头.
    private SurfaceHolder mHolder;
    private boolean focus = false;
    private RxPermissions rxPermissions;
    private boolean isGranted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        initData();
        initView();
        setListener();
    }

    private void initData() {
        DetectScreenOrientation detectScreenOrientation = new DetectScreenOrientation(this);
        detectScreenOrientation.enable();
        rxPermissions = new RxPermissions(this);
    }

    private void initView() {
        previewSFV = (FocusSurfaceView) findViewById(R.id.preview_sv);
        mHolder = previewSFV.getHolder();
        mHolder.addCallback(MainActivity.this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mTakeBT = (Button) findViewById(R.id.take_bt);
        mThreeFourBT = (Button) findViewById(R.id.three_four_bt);
        mFourThreeBT = (Button) findViewById(R.id.four_three_bt);
        mNineSixteenBT = (Button) findViewById(R.id.nine_sixteen_bt);
        mSixteenNineBT = (Button) findViewById(R.id.sixteen_nine_bt);
        mFitImgBT = (Button) findViewById(R.id.fit_image_bt);
        mCircleBT = (Button) findViewById(R.id.circle_bt);
        mFreeBT = (Button) findViewById(R.id.free_bt);
        mSquareBT = (Button) findViewById(R.id.square_bt);
        mCircleSquareBT = (Button) findViewById(R.id.circle_square_bt);
        mCustomBT = (Button) findViewById(R.id.custom_bt);

        previewSFV.setTopTipText("wzgl5533");
        previewSFV.setBottomTipText("wzgl5533");
    }

    private void setListener() {
        mTakeBT.setOnClickListener(this);
        mThreeFourBT.setOnClickListener(this);
        mFourThreeBT.setOnClickListener(this);
        mNineSixteenBT.setOnClickListener(this);
        mSixteenNineBT.setOnClickListener(this);
        mFitImgBT.setOnClickListener(this);
        mCircleBT.setOnClickListener(this);
        mFreeBT.setOnClickListener(this);
        mSquareBT.setOnClickListener(this);
        mCircleSquareBT.setOnClickListener(this);
        mCustomBT.setOnClickListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera();
    }

    private void initCamera() {
        rxPermissions.request(CAMERA)
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean granted) throws Exception {
                                   if (granted) {
                                       Log.e(TAG, "granted");
                                       try {
                                           mCamera = Camera.open(cameraPosition);//1:采集指纹的前置摄像头. 0:拍照的后置摄像头.
                                           mCamera.setPreviewDisplay(mHolder);
                                           setCameraParams();
                                           isGranted = true;
                                       } catch (Exception e) {//5.0权限拒绝或者不选，都走异常
                                           ToastUtils.showShort("摄像头或存储权限被拒绝,请到权限管理中设置");
                                           finish();
                                           e.printStackTrace();
                                       }
                                   } else {
                                       ToastUtils.showShort("摄像头或存储权限被拒绝,请到权限管理中设置");
                                       finish();
                                   }

                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "onError", throwable);
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.i(TAG, "OnComplete");
                            }
                        });
    }

    private void setCameraParams() {
        if (mCamera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraPosition, info);
            int rotation = judgeScreenOrientation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
            mCamera.setDisplayOrientation(result);
            //parameters.setRotation(result);

            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPictureSize(ScreenUtils.getScreenHeight(), ScreenUtils.getScreenWidth());
            parameters.setPreviewSize(ScreenUtils.getScreenHeight(), ScreenUtils.getScreenWidth());
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //切换摄像头(备用)
    private void changeCameraOrientation() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraPosition, cameraInfo);
        mCamera.stopPreview();//停掉原来摄像头的预览
        mCamera.release();//释放资源
        mCamera = null;//取消原来摄像头
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//此时后置
            Log.i(TAG, cameraPosition + "BACK");
            cameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;

        } else {//此时前置
            Log.i(TAG, cameraPosition + "For");
            cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        initCamera();
        setCameraParams();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    //判断是否授权
    private boolean isGranted() {

        return isGranted;
    }

    /**
     * 判断屏幕方向
     *
     * @return 0：竖屏 1：左横屏 2：反向竖屏 3：右横屏
     */
    private int judgeScreenOrientation() {
        return getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_bt:
                if (isGranted()) {
                    takePicture();
                } else {
                    ToastUtils.showShort("摄像头权限未开启，不能拍照");
                }
                break;
            case R.id.three_four_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.RATIO_3_4);
                break;
            case R.id.four_three_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.RATIO_4_3);
                break;
            case R.id.nine_sixteen_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.RATIO_9_16);
                break;
            case R.id.sixteen_nine_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.RATIO_16_9);
                break;
            case R.id.fit_image_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.FIT_IMAGE);
                break;
            case R.id.circle_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.CIRCLE);
                break;
            case R.id.free_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.FREE);
                break;
            case R.id.square_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.SQUARE);
                break;
            case R.id.circle_square_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.CIRCLE_SQUARE);
                break;
            case R.id.custom_bt:
                previewSFV.setCropMode(FocusSurfaceView.CropMode.CUSTOM);
                break;
            default:
                break;
        }
    }

    /**
     * 拍照,（前置摄像头目前在三星某些手机上并不能自动获取焦点，拍照失效），可以先把mCamera.autoFocus去掉，直接拍照，本demo使用后置的演示
     */
    private void takePicture() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                focus = success;
                if (success) {
                    mCamera.cancelAutoFocus();
                    mCamera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {
                        }
                    }, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Bitmap originalBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Log.e(TAG, originalBitmap.getWidth()+"--original--"+ originalBitmap.getHeight());
                            //前置图片顺时针旋转180度加镜像翻转
                            if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                originalBitmap = mirrorRotate(rotate(originalBitmap,180));
                            }

                            //目前本人测试的几款手机都会有90度的旋转，所以需要复原图片，如果有不同之处，请在github留言
                            if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK){
                                originalBitmap = rotate(originalBitmap,90);
                            }else {
                                originalBitmap = rotate(originalBitmap,-90);
                            }
                            //到此图片和预览视角相同
                            Log.e(TAG, originalBitmap.getWidth()+"----"+ originalBitmap.getHeight());
                            Bitmap cropBitmap = previewSFV.getCropPicture(originalBitmap);

                            PictureFragment pictureFragment = new PictureFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(ORIGIN_PICTURE, originalBitmap);
                            bundle.putParcelable(CROP_PICTURE, cropBitmap);
                            pictureFragment.setArguments(bundle);
                            pictureFragment.show(getFragmentManager(), null);

                           Log.e(TAG,cropBitmap.getWidth()+"--crop--"+cropBitmap.getHeight());
                            focus = false;
                            mCamera.startPreview();
                        }
                    });
                }
            }
        });
    }

    /**
     * 用来监测左横屏和右横屏切换时旋转摄像头的角度
     */
    private class DetectScreenOrientation extends OrientationEventListener {
        DetectScreenOrientation(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (260 < orientation && orientation < 290) {
                setCameraParams();
            } else if (80 < orientation && orientation < 100) {
                setCameraParams();
            }
        }
    }

    /**前置图片顺时针旋转180度**/
    private Bitmap rotate(Bitmap bitmap, int degree){

        return ImageUtils.rotate(bitmap, degree,
                bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
    }
    /**图片镜像翻转**/
    private Bitmap mirrorRotate(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);// 镜像水平翻转
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
