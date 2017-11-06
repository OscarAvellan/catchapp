package com.ammonia.catchapp.ViewTypes;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Dalzy on 10/09/2017.
 * Reference: https://code.tutsplus.com/tutorials/android-sdk-augmented-reality-camera-sensor-setup--mobile-7873
 */

public class ARDisplayView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String DEBUG_TAG = "ArDisplayView Log";
    Camera mCamera;
    SurfaceHolder mHolder;
    Activity mActivity;

    public ARDisplayView(Context context, Activity activity) {
        super(context);

        mActivity = activity;
        mHolder = getHolder();

        // This value is supposedly deprecated and set "automatically" when
        // needed.
        // Without this, the application crashes.
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // callbacks implemented by ArDisplayView
        mHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(DEBUG_TAG, "surfaceCreated");

        // Grab the camera
        mCamera = Camera.open();

        // Set Display orientation
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

        int rotation = mActivity.getWindowManager().getDefaultDisplay()
                .getRotation();
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

        mCamera.setDisplayOrientation((info.orientation - degrees + 360) % 360);

        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "surfaceCreated exception: ", e);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d(DEBUG_TAG, "surfaceChanged");

        Camera.Parameters params = mCamera.getParameters();

        // Find an appropriate preview size that fits the surface
        List<Camera.Size> prevSizes = params.getSupportedPreviewSizes();
        for (Camera.Size s : prevSizes) {
            if ((s.height <= height) && (s.width <= width)) {
                params.setPreviewSize(s.width, s.height);
                break;
            }

        }

        // Set the preview format
        //params.setPreviewFormat(ImageFormat.JPEG);

        // Consider adjusting frame rate to appropriate rate for AR

        // Confirm the parameters
        mCamera.setParameters(params);

        // Begin previewing
        mCamera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(DEBUG_TAG, "surfaceDestroyed");

        // Shut down camera preview
        mCamera.stopPreview();
        mCamera.release();
    }

}

