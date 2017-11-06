package com.ammonia.catchapp.ViewTypes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.ammonia.catchapp.interfaces.NetworkManagerInterface;
import com.ammonia.catchapp.mockImplementations.MockNetworkManager;
import com.ammonia.catchapp.structures.UserProfile;

import static java.lang.Float.min;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;

/**
 * Created by Dalzy on 10/09/2017.
 * Reference: https://code.tutsplus.com/tutorials/android-sdk-augmented-reality-camera-sensor-setup--mobile-7873
 */

public class AROverlayView extends View implements SensorEventListener {

    public static final String DEBUG_TAG = "OverlayView Log";

    private Location selfLocation = null;

    private float verticalFOV;
    private float horizontalFOV;

    private final int FIX_OFFSET_DELAY = 1000;
    private final float STABILIZATION_FACTOR = 0.01f;

    NetworkManagerInterface networkManager;

    private static final float EPSILON = 0.00000001f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float gyroTimeStamp = 0;
    private float[] magneticVals = null;
    private float[] gravityVals = null;
    private boolean hasMagGravMatrix = false;
    private float[] currentRotationMatrix = null;
    private final float[] deltaRotationVector = new float[4];
    private final float[] magGravRotationMatrix = new float[9];
    private final float[] currentOrientation = new float[3];

    private float currentRollAngle;
    private float currentVertAngle;
    private float currentBearingNorth;


    private UserProfile targetUser;

    public AROverlayView(Context context, UserProfile user) {
        super(context);
        networkManager = new MockNetworkManager();


        targetUser = user;
        SensorManager sensors = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelSensor = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor compassSensor = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor gyroSensor = sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        boolean isAccelAvailable = sensors.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        boolean isCompassAvailable = sensors.registerListener(this, compassSensor, SensorManager.SENSOR_DELAY_NORMAL);
        boolean isGyroAvailable = sensors.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);

        /*Permissions check for location services*/
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        /*Get Camera FOV values*/

        Camera.Parameters params = Camera.open().getParameters();
        verticalFOV = params.getVerticalViewAngle();
        horizontalFOV = params.getHorizontalViewAngle();

        final Handler driftFix = new Handler();

        driftFix.postDelayed(new Runnable() {
            @Override
            public void run() {

                currentRotationMatrix = magGravRotationMatrix;

                driftFix.postDelayed(this,FIX_OFFSET_DELAY);
            }
        }, FIX_OFFSET_DELAY);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*Update our canvas manipulation values based on the info in our currentOrientation*/
        float horizonRotation = currentRollAngle;
        float horizonVertOffset = (float) ((canvas.getHeight() / verticalFOV) * currentVertAngle);

        /*Update the location of the user we are tracking so that we can draw them to the screen*/
        Location targetLocation = targetUser.getLocation();

        if(targetLocation == null || selfLocation == null){
            /*We don't have the values required to represent our tracking
             user in AR so draw nothing*/
            return;
        }

        float currentBearing = selfLocation.bearingTo(targetLocation);
        float currentDist = selfLocation.distanceTo(targetLocation);
        float angleToTarget = currentBearingNorth - currentBearing;
        if(angleToTarget > 180){
            angleToTarget -= 360;
        }
        float targetHorizontalOffset = (float)((canvas.getWidth() / horizontalFOV) * angleToTarget);

        /*Set up the paint tool to use on the canvas*/
        Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Paint.Align.CENTER);
        contentPaint.setTextSize(40);
        contentPaint.setColor(Color.RED);

        //If the target is too far right to be seen, draw indicator to screen
        if(angleToTarget < -(horizontalFOV / 2)){
            canvas.drawCircle(canvas.getWidth() - 50,canvas.getHeight() / 2, 30,contentPaint);
        }

        if(angleToTarget > (horizontalFOV) / 2){
            canvas.drawCircle(50, canvas.getHeight() / 2, 30, contentPaint);
        }

        /*Rotate the orientation of the canvas to match that of the real world*/
        canvas.rotate(horizonRotation,canvas.getWidth()/2, canvas.getHeight()/2);
        /*!!! Anything drawn after this point will have it's orientation rotated such that it
         is upright in the real world with respect to the image from the camera !!!*/

        /*Do a vertical translation such that the centre of the canvas
            now appears at the height of the horizon*/
        canvas.translate(0.0f,0.0f - horizonVertOffset);
        /*!!! Anything drawn after this point will be centered vertically on the horizon line of
        the real world!!!*/

        /*Draw a line to represent the horizon*/
        canvas.drawLine(0f - canvas.getHeight(),
                canvas.getHeight()/2,canvas.getWidth() + canvas.getHeight(), canvas.getHeight()/2,contentPaint);

        /*Draw the targetUser to the screen*/
        canvas.translate(0.0f - targetHorizontalOffset, 0.0f);
        drawUser(canvas, targetUser, currentDist);
        canvas.translate(targetHorizontalOffset, 0.0f);
    }

    private void drawUser(Canvas canvas, UserProfile trackingUser, float dist) {
         /*Set upt the paint tool to use on the canvas*/
        Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Paint.Align.CENTER);
        contentPaint.setTextSize(40);
        contentPaint.setColor(Color.BLUE);

        String distText = String.valueOf(dist) + " Metres";
        String nameText = trackingUser.getFirstName() + " " + trackingUser.getLastName();

        float circleSize = max(100 - 2 * dist, 10);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, circleSize, contentPaint);
        canvas.drawText(nameText, canvas.getWidth() / 2, canvas.getHeight() / 2 - 120, contentPaint);


    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
//        Log.d(DEBUG_TAG, "onAccuracyChanged");

    }

    public void onSensorChanged(SensorEvent event) {
//        Log.d(DEBUG_TAG, "onSensorChanged");

        switch(event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                gravityVals = event.values;
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroUpdate(event);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticVals = event.values;
                break;
        }
        this.invalidate();
    }

    private void gyroUpdate(SensorEvent event){

        float deltaRollAngle = 0;
        float deltaVertAngle = 0;
        float deltaBearingNorth = 0;

        /*If we have values from the accelerometer and the magnetic field then produce a
        * rotation matrix from those values*/

        if((magneticVals != null)&&(gravityVals != null)){
            float newOrientation[] = new float[9];

            SensorManager.getRotationMatrix(magGravRotationMatrix, null, gravityVals, magneticVals);
            hasMagGravMatrix = true;
            float magGravCameraRotation[] = new float[9];
            SensorManager.remapCoordinateSystem(magGravRotationMatrix,
                    SensorManager.AXIS_X,SensorManager.AXIS_Z,magGravCameraRotation);
            SensorManager.getOrientation(magGravCameraRotation, newOrientation);


            float newRollAngle = (float) (0.0f - Math.toDegrees(currentOrientation[2]));
            float newVertAngle = (float)(Math.toDegrees(currentOrientation[1]));
            float newBearingNorth = (float) (Math.toDegrees(currentOrientation[0]));

            deltaRollAngle += STABILIZATION_FACTOR * (newRollAngle - currentRollAngle);
            deltaVertAngle += STABILIZATION_FACTOR * (newVertAngle - currentVertAngle);
            deltaBearingNorth += STABILIZATION_FACTOR * (newBearingNorth - currentBearingNorth);
        }

        if((currentRotationMatrix == null) && (hasMagGravMatrix == true)){
            /*If our used rotation matrix hasn't been initialized then let's set it to
             * the rotation matrix generated by our magnetic field and gravity values*/
            currentRotationMatrix = magGravRotationMatrix;
            /*Using our Rotation matrix to update our orientation values*/
            SensorManager.getOrientation(magGravRotationMatrix, currentOrientation);


        }else if(currentRotationMatrix != null){
            /*If our rotation matrix has already been initialized then we can use an update
            * rotation matrix from our gyro input to manipulate it and keep it up to date*/

            if (gyroTimeStamp != 0) {
                /*Bunch of LinAlg mumbo jumbo that transforms gyro values and a
                * time diff into a rotation matrix*/
                final float dT = (event.timestamp - gyroTimeStamp) * NS2S;
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];
                float omegaMagnitude = (float) sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = (float) sin(thetaOverTwo);
                float cosThetaOverTwo = (float) cos(thetaOverTwo);
                deltaRotationVector[0] = sinThetaOverTwo * axisX;
                deltaRotationVector[1] = sinThetaOverTwo * axisY;
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                deltaRotationVector[3] = cosThetaOverTwo;
                float[] deltaRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);

                /*Multiplying the old rotation matrix by our rotation transformation matrix*/
                currentRotationMatrix = matrixMultiply3x3(currentRotationMatrix, deltaRotationMatrix);

                /*Adjust so we get the orientation of camera instead of phone*/
                float cameraRotation[] = new float[9];
                SensorManager.remapCoordinateSystem(currentRotationMatrix,
                        SensorManager.AXIS_X,SensorManager.AXIS_Z,cameraRotation);

                float newOrientation[] = new float[3];

                /*Using our Rotation matrix to update our orientation values*/
                SensorManager.getOrientation(cameraRotation, newOrientation);

                float newRollAngle = (float) (0.0f - Math.toDegrees(newOrientation[2]));
                float newVertAngle = (float)(Math.toDegrees(newOrientation[1]));
                float newBearingNorth = (float) (Math.toDegrees(newOrientation[0]));

                deltaRollAngle +=  (newRollAngle - currentRollAngle);
                deltaVertAngle += (newVertAngle - currentVertAngle);
                deltaBearingNorth +=  (newBearingNorth - currentBearingNorth);

            }
            /*Update timestamp to diff next time the gyro is updated*/
            currentRollAngle += deltaRollAngle;
            currentVertAngle += deltaVertAngle;
            currentBearingNorth += deltaBearingNorth;
            gyroTimeStamp = event.timestamp;
        }

    }

    private float[] matrixMultiply3x3(float[] a, float[] b){
        if((a.length != 9)||(b.length != 9)){
            exit(0);
        }
        float[] newMatrix = new float[9];
        newMatrix[0] = a[0] * b[0] + a[1] * b[3] + a[2] * b[6];
        newMatrix[1] = a[0] * b[1] + a[1] * b[4] + a[2] * b[7];
        newMatrix[2] = a[0] * b[2] + a[1] * b[5] + a[2] * b[8];
        newMatrix[3] = a[3] * b[0] + a[4] * b[3] + a[5] * b[6];
        newMatrix[4] = a[3] * b[1] + a[4] * b[4] + a[5] * b[7];
        newMatrix[5] = a[3] * b[2] + a[4] * b[5] + a[5] * b[8];
        newMatrix[6] = a[6] * b[0] + a[7] * b[3] + a[8] * b[6];
        newMatrix[7] = a[6] * b[1] + a[7] * b[4] + a[8] * b[7];
        newMatrix[8] = a[6] * b[2] + a[7] * b[5] + a[8] * b[8];


        return newMatrix;
    }

    public void onLocationChanged(Location location) {
        Log.d(DEBUG_TAG, "Location added");
        selfLocation = location;
    }

}


