package com.zebra.windevmobilehd4000wrapper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.symbol.zebrahud.ZebraHud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WindevMobileHD4000Facade {

    public interface IAppelProcedureWL
    {
        void appelProcedureWLSS(String param1, String param2);
        void appelProcedureWLSSS(String param1, String param2, String param3);
        void appelProcedureWLSSSS(String param1, String param2, String param3, String param4);
    }

    public interface IActivityRetriever
    {
        Activity getActivity();
    }

    // Membres
    private final static String TAG = "WindevMobileHUDFacade";

    // TODO: Localize me
    private final String STATUS_SUCCESS = "SUCCES";
    private final String STATUS_ERROR = "ERREUR";
    private final String STATUS_EXCEPTION = "EXCEPTION";
    private final String STATUS_TRUE = "VRAI";
    private final String STATUS_FALSE = "FAUX";
    private final String STATUS_ON = "ALLUME";
    private final String STATUS_OFF = "ETEINT";

    // Interface pour executer les procedures WL
    // Cet objet doit être implémenté dans la collection de procedures WL
    private IAppelProcedureWL mAppelProcedureWL = null;

    // Interface pour récupérer l'activité courante de l'application
    // Cet objet doit être implémenté dans la collection de procédures WL
    private IActivityRetriever mActivityRetriever = null;

    // Procedure WL appelée en cas d'erreur
    private String msErrorCallback = "";

    // Procedure WL appelée en cas de succès
    private String msSuccesCallback = "";

    private ZebraHud mZebraHud;

    public WindevMobileHD4000Facade(IAppelProcedureWL aAppelProcedureWLInterface, IActivityRetriever aActivityRetrieverInterface)
    {
        mAppelProcedureWL = aAppelProcedureWLInterface;
        mActivityRetriever = aActivityRetrieverInterface;
        mZebraHud = new ZebraHud();
    }

    private Activity getActivity()
    {
        if(mActivityRetriever != null)
        {
            return mActivityRetriever.getActivity();
        }
        return null;
    }

    private void logMessage(String message)
    {
        Log.d(TAG, message);
    }

    public void onStart() {
        if (mZebraHud != null) {
            logMessage("onStart");
            mZebraHud.onStart(getActivity());
        }
    }

    public void onPause() {
        if (mZebraHud != null) {
            logMessage("onPause");
            mZebraHud.onPause(getActivity());
        }
    }

    public void onStop() {
        if (mZebraHud != null) {
            logMessage("onStop");
            Activity currentActivity = getActivity();
            if (currentActivity.isFinishing()) { mZebraHud.clearDisplay(); }
            mZebraHud.onStop(currentActivity, currentActivity.isFinishing());
        }
    }

    public void onResume(final String fsCallbackOnConnected, final String fsCallbackOnImageUpdated, final String fsCallbackOnCameraImage) {
        if (mZebraHud != null) {
            logMessage("onResume");
            mZebraHud.onResume(getActivity(), new ZebraHud.EventListener() {
                @Override
                public void onConnected(Boolean aBoolean) {
                    if (mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackOnConnected, aBoolean ? STATUS_TRUE : STATUS_FALSE);
                    }
                }

                @Override
                public void onImageUpdated(byte[] bytes) {
                    if (mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackOnImageUpdated, getImageBytesAsJsonString(bytes));
                    }
                }

                @Override
                public void onCameraImage(Bitmap bitmap) {
                    if (mAppelProcedureWL != null) {
                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackOnCameraImage, getCameraBitmapAsJsonString(bitmap));
                    }
                }
            });
        }
    }

    private String getCameraBitmapAsJsonString(Bitmap bitmap) {
        return null;
    }

    private String getImageBytesAsJsonString(byte[] bytes) {
        return null;
    }

    public String getDeviceInfo(final String fsCallbackSucces, final String fsCallbackErreur)
    {
        String deviceInfo = null;
        try
        {
            if(isConnected())
            {
                deviceInfo = mZebraHud.getDeviceInfo();
                logMessage("Getting device Info:\n" + deviceInfo);
                if (fsCallbackSucces != "") {
                    if (mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "getDeviceInfo", deviceInfo);
                    }
                }
                return deviceInfo;
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "getDeviceInfo", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: getDeviceInfo\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "getDeviceInfo", e.getLocalizedMessage());
        }
        return deviceInfo;
    }

    public String getServiceVersion() {
        return "1.0";
    }

    public boolean isConnected() {
        return mZebraHud.isConnected();
    }

    public void setDisplayOn(boolean on, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if (isConnected())
            {
                logMessage("Settings display to:" + (on ? "On" : "Off"));
                mZebraHud.setDisplayOn(on);
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces))
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "setDisplayOn", (on ? STATUS_ON : STATUS_OFF));

            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "setDisplayOn", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: isDisplayOn\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "setDisplayOn", e.getLocalizedMessage());
        }
    }

    public boolean isDisplayOn(final String fsCallbackSucces, final String fsCallbackErreur) {
        try
        {
            if(isConnected())
            {
                boolean isDisplayOn = mZebraHud.isDisplayOn();
                logMessage("Display is: " + (isDisplayOn ? "On" : "Off"));
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "isDisplayOn", (isDisplayOn ? STATUS_ON : STATUS_OFF));
                return mZebraHud.isDisplayOn();
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "isDisplayOn", "No HD4000 connected.");
                return false;
            }
        }
        catch (Exception e)
        {
            logMessage("Exception in: isDisplayOn\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "isDisplayOn", e.getLocalizedMessage());
            return false;
        }

    }

    public void clearDisplay(final String fsCallbackSucces, final String fsCallbackErreur) {
        try
        {
            if(isConnected())
            {
                mZebraHud.clearDisplay();
                logMessage("Display cleared");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "clearDisplay", "");
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "clearDisplay", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: clearDisplay\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "clearDisplay", e.getLocalizedMessage());
        }
    }

    public void setBrightness(int percent, final String fsCallbackSucces, final String fsCallbackErreur) {
        try
        {
            if(isConnected())
            {
                mZebraHud.setBrightness(percent);
                logMessage("Brightness set to: " + percent + "%");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "setBrightness", Integer.toString(percent));
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "setBrightness", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: setBrightness\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "setBrightness", e.getLocalizedMessage());
        }
    }

    public int getBrightness() {
        return mZebraHud.getBrightness();
    }

    public void setScale(int percent) {
        mZebraHud.setScale(percent);
    }

    public int getScale() {
        return mZebraHud.getScale();
    }

    public byte[] showMessage(final String title, final String message, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            byte[] returnValue = mZebraHud.showMessage(title, message);
            if(returnValue != null) {
                logMessage("Success to display message: \nTitle: " + title + "\nMessage: " + message);
                if (fsCallbackSucces != "") {
                    if (mAppelProcedureWL != null) {
                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Success to display message: \nTitle: " + title + "\nMessage: " + message);
                    }
                }
                return returnValue;
            }
            else
            {
                logMessage("Error to display message: \nTitle: " + title + "\nMessage: " + message);
                mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "Error to display message: \nTitle: " + title + "\nMessage: " + message);
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
        return null;
    }

    public byte[] showImage(final Bitmap bitmap, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            byte[] returnValue = mZebraHud.showImage(bitmap);
            if(returnValue != null) {
                logMessage("Image sent to HUD.\nSize:" + bitmap.getByteCount());

                if (fsCallbackSucces != "") {
                    if (mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Image sent to HUD.\nSize:" + bitmap.getByteCount());
                    }
                }
                return returnValue;
            }
            else
            {
                logMessage("Error displaying image on HUD.\nSize:" + bitmap.getByteCount());
                mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "Error displaying image on HUD.\nSize:" + bitmap.getByteCount());
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
        return null;
    }

    public void showImage(final String bitmapPath, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        // Call Glide to Build Bitmap on Background thread from image path
        Glide.with(getActivity()) // Context
                .asBitmap() // Set Image Type
                .load(bitmapPath) // File Path
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // This is a Glide Callback when Bitmap has been created
                        // Call showImage() & pass through the generated bitmap
                       showImage(resource, fsCallbackSucces, fsCallbackErreur);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Empty Calllback
                    }
                });
    }

    public byte[] showJim(final String fsJim, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            byte[] returnValue = mZebraHud.showJim(fsJim, (String)null, (String)null, (String)null);
            if(returnValue != null) {
                logMessage("Jim sent to HUD.\n" + fsJim);

                if (fsCallbackSucces != "") {
                    if (mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Jim sent to HUD.\n" + fsJim);
                    }
                }
                return returnValue;
            }
            else
            {
                logMessage("Error sending Jim to HUD.\n" + fsJim);
                mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "Error sending Jim to HUD.\n" + fsJim);
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
        return null;
    }

    public byte[] showJim(final String fsJim, final String fsDirectoryImages, final String fsImageFilename, final String fsJimBase, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            byte[] returnValue = mZebraHud.showJim(fsJim, fsDirectoryImages, fsImageFilename, fsJimBase);
            if(returnValue != null) {
                logMessage("Jim sent to HUD.\n" + fsJim);

                if (fsCallbackSucces != "") {
                    if (mAppelProcedureWL != null) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Jim sent to HUD.\n" + fsJim);
                    }
                }
                return returnValue;
            }
            else
            {
                logMessage("Error sending Jim to HUD.\n" + fsJim);
                mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "Error sending Jim to HUD.\n" + fsJim);
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
        return null;
    }

    public void showCachedImage(final byte[] fsBytes, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            mZebraHud.showCachedImage(fsBytes);
            logMessage("Image sent to HUD.\nSize:" + fsBytes.length);

            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Image sent to HUD.\nSize:" + fsBytes.length);
                }
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
    }

    public void setMicrophoneEnabled(final boolean fsEnable, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            mZebraHud.setMicrophoneEnabled(fsEnable);
            logMessage("Microphone enable set to:" + fsEnable);
            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Microphone enable set to:" + fsEnable);
                }
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
    }

    public boolean getMicrophoneEnabled() {
        if(isConnected())
        {
            return mZebraHud.getMicrophoneEnabled();
        }
        else
        {
            return false;
        }
    }

    public void setCameraEnabled(boolean fsEnable, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            mZebraHud.setCameraEnabled(fsEnable);
            logMessage("Camera enable set to:" + fsEnable);
            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Camera enable set to:" + fsEnable);
                }
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
    }

    public boolean getCameraEnabled() {
        if(isConnected())
        {
            return mZebraHud.getCameraEnabled();
        }
        else
        {
            return false;
        }
    }

    public void startCameraCapture(final String fsCallbackSucces, final String fsCallbackErreur) {
        if(isConnected())
        {
            mZebraHud.startCameraCapture();
            logMessage("Camera capture started");
            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Camera capture started");
                }
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
    }

    public void stopCameraCapture(final String fsCallbackSucces, final String fsCallbackErreur) {
        if(isConnected())
        {
            mZebraHud.stopCameraCapture();
            logMessage("Camera capture stopped");
            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Camera capture stopped");
                }
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
    }

    public boolean getImuStatus() {
        if(isConnected())
        {
            return mZebraHud.getImuStatus();
        }
        else
        {
            return false;
        }
    }

    public void startImu(ZebraHud.ImuListener imuListener, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            mZebraHud.startImu(imuListener);
            logMessage("Starting IMU");
            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Starting IMU");
                }
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
    }

    public void stopImu(final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            mZebraHud.stopImu();
            logMessage("Stopping IMU");
            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Stopping IMU");
                }
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
    }

    public ZebraHud.OperationMode getOperationMode(final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            logMessage("Getting operation mode");
            ZebraHud.OperationMode opmode =  mZebraHud.getOperationMode();
            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Success getting operation mode.");
                }
                return opmode;
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
            return ZebraHud.OperationMode.NORMAL;
        }
        return ZebraHud.OperationMode.NORMAL;
    }

    public void setOperationMode(ZebraHud.OperationMode mode, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        if(isConnected())
        {
            logMessage("Setting operation mode.");
            mZebraHud.setOperationMode(mode);
            if (fsCallbackSucces != "") {
                if (mAppelProcedureWL != null) {

                    mAppelProcedureWL.appelProcedureWLSS(fsCallbackSucces, "Success setting operation mode.");
                }
            }
        }
        else
        {
            logMessage("No HD4000 connected.");
            mAppelProcedureWL.appelProcedureWLSS(fsCallbackErreur, "No HD4000 connected.");
        }
    }

}
