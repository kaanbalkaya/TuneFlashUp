package com.kaanbalkaya.tuneflashup;

/**
 * Created by kaan on 01.01.2016.
 */

import android.hardware.Camera;
import android.media.audiofx.Visualizer;
import android.util.Log;
import android.hardware.Camera.Parameters;

import java.io.Serializable;

public class FlashThread extends Thread implements Serializable{
    private Visualizer visualizer;
    private Camera camera;
    private boolean isFlashOn;
    //private boolean hasFlash;
    Parameters params;
    public FlashThread(Visualizer visualizer) {
        this.visualizer=visualizer;
        //for flash:
        try {
            camera = Camera.open();
            params = camera.getParameters();

        } catch (RuntimeException e) {
            Log.e("Camera Error.", e.getMessage());
        }
        this.params=params;
        isFlashOn=false;
    }

    public void run() {

        int captureSize=visualizer.getCaptureSize();
        int samplingHertz=visualizer.getSamplingRate();
        byte[] waveform;
        byte[] fft;
        long time0, time1;
        Log.e("sampling rate : ",""+samplingHertz);
        for(;;){
            waveform=new byte[captureSize];
            fft=new byte[captureSize];
            visualizer.getWaveForm(waveform);
            visualizer.getFft(fft);
            time0=System.currentTimeMillis();
            time1=System.currentTimeMillis();
            for(int i=1;i<captureSize;i+=(captureSize/8)){
                if(i>captureSize)
                    break;
                if(fft[i]>2){
                    //for flash:
                    if (camera == null || params == null) {
                        return;
                    }
                    if(isFlashOn)
                        continue;
                    params = camera.getParameters();
                    params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview();
                    isFlashOn = true;
                    time1=System.currentTimeMillis();

                }else {
                    //for flash:
                    if (camera == null || params == null) {
                        return;
                    }
                    if(!isFlashOn)
                        continue;
                    params = camera.getParameters();
                    params.setFlashMode(Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    camera.stopPreview();
                    isFlashOn = false;
                    time1=System.currentTimeMillis();
                }
                if(this.isInterrupted())
                    break;

            }
            Log.e("time : ",""+(time1-time0));
            if(this.isInterrupted()){
                params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                camera.release();
                break;
            }

        }

    }

}
