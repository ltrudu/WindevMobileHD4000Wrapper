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
        void appelProcedureWLSSSS(String param1, String param2, String param3, String param4);
    }

    public interface IActivityRetriever
    {
        Activity getActivity();
    }

    // Membres
    private final static String TAG = "WDMhd4k";

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
                    if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackOnConnected)) {
                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackOnConnected, aBoolean ? STATUS_TRUE : STATUS_FALSE);
                    }
                }

                @Override
                public void onImageUpdated(byte[] bytes) {
                    if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackOnImageUpdated)) {

                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackOnImageUpdated, getImageBytesAsJsonString(bytes));
                    }
                }

                @Override
                public void onCameraImage(Bitmap bitmap) {
                    if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackOnCameraImage)) {
                        mAppelProcedureWL.appelProcedureWLSS(fsCallbackOnCameraImage, getCameraBitmapAsJsonString(bitmap));
                    }
                }
            });
        }
    }

    //TODO: Transcript Bitmap Data as JSON String to allow marshalling to Windev Mobile
    private String getCameraBitmapAsJsonString(Bitmap bitmap) {
        return null;
    }

    //TODO: Transcript Bytes Image Data as JSON String to allow marshalling to Windev Mobile
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
                    if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
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

    public int getBrightness(final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                return mZebraHud.getBrightness();
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "getBrightness", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: getBrightness\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "getBrightness", e.getLocalizedMessage());
        }
        return -1;
    }

    public void setScale(int percent, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                mZebraHud.setScale(percent);
                logMessage("Scale set to: " + percent + "%");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "setScale", Integer.toString(percent));

            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "setScale", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: setScale\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "setScale", e.getLocalizedMessage());
        }
    }

    public int getScale(final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                return mZebraHud.getScale();
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "getScale", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: getScale\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "getScale", e.getLocalizedMessage());
        }
        return -1;
    }

    public byte[] showMessage(final String title, final String message, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                byte[] returnValue = mZebraHud.showMessage(title, message);
                if(returnValue != null) {
                    logMessage("Success to display message: \nTitle: " + title + "\nMessage: " + message);
                    if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "showMessage", "Message:\nTitle: " + title + "\nMessage: " + message);
                    }
                    return returnValue;
                }
                else
                {
                    logMessage("Error to display message: \nTitle: " + title + "\nMessage: " + message);
                    if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showMessage", "Error displaying message:\nTitle: " + title + "\nMessage: " + message);
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showMessage", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: showMessage\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "showMessage", e.getLocalizedMessage());
        }
        return null;
    }

    public byte[] showImage(final Bitmap bitmap, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                byte[] returnValue = mZebraHud.showImage(bitmap);
                if(returnValue != null) {
                    logMessage("Image sent to HUD.\nSize:" + bitmap.getByteCount());
                    if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "showImage", "Image sent to HUD.\nSize:" + bitmap.getByteCount());
                    }
                    return returnValue;
                }
                else
                {
                    logMessage("Error displaying image on HUD.\nSize:" + bitmap.getByteCount());
                    if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showImage", "Error displaying image on HUD.\nSize:" + bitmap.getByteCount());
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showImage", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: showImage\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "showImage", e.getLocalizedMessage());
        }
        return null;
    }

    public void showImageFromPath(final String bitmapPath, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
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
        catch(Exception e)
        {
            logMessage("Exception in: showImageFromPath\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "showImageFromPath", e.getLocalizedMessage());
        }
    }

    public byte[] showJim(final String fsJim, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                byte[] returnValue = mZebraHud.showJim(fsJim, (String)null, (String)null, (String)null);
                if(returnValue != null) {
                    logMessage("Jim sent to HUD.\n" + fsJim);
                    if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "showJim", "Jim sent to HUD.\n" + fsJim);
                    }
                    return returnValue;
                }
                else
                {
                    logMessage("Error sending Jim to HUD.\n" + fsJim);
                    if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showJim", "Error sending Jim to HUD.\n" + fsJim);
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showJim", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: showJim\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "showJim", e.getLocalizedMessage());
        }
        return null;
    }

    public byte[] showJimWithImages(final String fsJim, final String fsDirectoryImages, final String fsImageFilename, final String fsJimBase, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                byte[] returnValue = mZebraHud.showJim(fsJim, fsDirectoryImages, fsImageFilename, fsJimBase);
                if(returnValue != null) {
                    logMessage("Jim sent to HUD.\n" + fsJim);
                    if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "showJimWithImages", "Jim sent to HUD.\n" + fsJim);
                    }
                    return returnValue;
                }
                else
                {
                    logMessage("Error sending Jim to HUD.\n" + fsJim);
                    if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                        mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showJimWithImages", "Error sending Jim to HUD.\n" + fsJim);                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showJimWithImages", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: showJimWithImages\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "showJimWithImages", e.getLocalizedMessage());
        }
        return null;
    }

    public void showCachedImage(final byte[] fsBytes, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                mZebraHud.showCachedImage(fsBytes);
                logMessage("Image sent to HUD.\nSize:" + fsBytes.length);
                if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "showCachedImage", "Image sent to HUD.\nSize:" + fsBytes.length);
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "showCachedImage", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: showCachedImage\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "showCachedImage", e.getLocalizedMessage());
        }
    }

    public void setMicrophoneEnabled(final boolean fsEnable, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                mZebraHud.setMicrophoneEnabled(fsEnable);
                logMessage("Microphone enable set to:" + fsEnable);
                if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "setMicrophoneEnabled", "Microphone enable set to:" + fsEnable);
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "setMicrophoneEnabled", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: setMicrophoneEnabled\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "setMicrophoneEnabled", e.getLocalizedMessage());
        }
    }

    public boolean getMicrophoneEnabled(final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                return mZebraHud.getMicrophoneEnabled();
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "getMicrophoneEnabled", "No HD4000 connected.");
            return false;
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: getMicrophoneEnabled\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "getMicrophoneEnabled", e.getLocalizedMessage());
        }
        return false;
    }

    public void setCameraEnabled(boolean fsEnable, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                mZebraHud.setCameraEnabled(fsEnable);
                logMessage("Camera enable set to:" + fsEnable);
                if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "setCameraEnabled", "Camera enable set to:" + fsEnable);
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "setCameraEnabled", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: setCameraEnabled\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "setCameraEnabled", e.getLocalizedMessage());
        }
    }

    public boolean getCameraEnabled(final String fsCallbackErreur) {
        try
        {
            if(isConnected())
            {
                return mZebraHud.getCameraEnabled();
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "getCameraEnabled", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: getCameraEnabled\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "getCameraEnabled", e.getLocalizedMessage());
        }
        return false;
    }

    public void startCameraCapture(final String fsCallbackSucces, final String fsCallbackErreur) {
        try
        {
            if(isConnected())
            {
                mZebraHud.startCameraCapture();
                logMessage("Camera capture started");
                if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "startCameraCapture", "Camera capture started");
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "startCameraCapture", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: startCameraCapture\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "startCameraCapture", e.getLocalizedMessage());
        }
    }

    public void stopCameraCapture(final String fsCallbackSucces, final String fsCallbackErreur) {
        try
        {
            if(isConnected())
            {
                mZebraHud.stopCameraCapture();
                logMessage("Camera capture stopped");
                if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "stopCameraCapture", "Camera capture stopped");
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "stopCameraCapture", "No HD4000 connected.");

            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: stopCameraCapture\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "stopCameraCapture", e.getLocalizedMessage());
        }
    }

    public boolean getImuStatus(final String fsCallbackErreur) {
        try
        {
            if(isConnected())
            {
                return mZebraHud.getImuStatus();
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "getImuStatus", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: getImuStatus\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "getImuStatus", e.getLocalizedMessage());
        }
        return false;
    }

    public void startImu(ZebraHud.ImuListener imuListener, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                mZebraHud.startImu(imuListener);
                logMessage("Starting IMU");
                if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "startImu", "Starting IMU");
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "startImu", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: startImu\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "startImu", e.getLocalizedMessage());
        }
    }

    public void stopImu(final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected()) {
                mZebraHud.stopImu();
                logMessage("Stopping IMU");
                if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "stopImu", "Stopping IMU");
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "stopImu", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: stopImu\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "stopImu", e.getLocalizedMessage());
        }
    }

    public ZebraHud.OperationMode getOperationMode(final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                logMessage("Getting operation mode");
                return mZebraHud.getOperationMode();
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "getOperationMode", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: getOperationMode\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "getOperationMode", e.getLocalizedMessage());
        }
        return ZebraHud.OperationMode.NORMAL;
    }

    public void setOperationMode(ZebraHud.OperationMode mode, final String fsCallbackSucces, final String fsCallbackErreur)
    {
        try
        {
            if(isConnected())
            {
                logMessage("Setting operation mode.");
                mZebraHud.setOperationMode(mode);
                if (mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackSucces)) {
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackSucces, STATUS_SUCCESS, "setOperationMode", "Setting operation mode to:" + mode.toString());
                }
            }
            else
            {
                logMessage("No HD4000 connected.");
                if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                    mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_ERROR, "setOperationMode", "No HD4000 connected.");
            }
        }
        catch(Exception e)
        {
            logMessage("Exception in: setOperationMode\nMessage:"+e.getLocalizedMessage());
            if(mAppelProcedureWL != null && !TextUtils.isEmpty(fsCallbackErreur))
                mAppelProcedureWL.appelProcedureWLSSSS(fsCallbackErreur, STATUS_EXCEPTION, "setOperationMode", e.getLocalizedMessage());
        }
    }

}
