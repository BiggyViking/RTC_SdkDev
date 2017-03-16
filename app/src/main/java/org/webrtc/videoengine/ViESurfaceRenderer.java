/*
 *  Copyright (c) 2012 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc.videoengine;

// The following four imports are needed saveBitmapToJPEG which
// is for debug only
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ViESurfaceRenderer implements Callback {

    private final static String TAG = "ViESurfaceRenderer";

    // the bitmap used for drawing.
    private Bitmap bitmap = null;
    private ByteBuffer byteBuffer = null;
    private SurfaceHolder surfaceHolder;
    // Rect of the source bitmap to draw
    private Rect srcRect = new Rect();
    // Rect of the destination canvas to draw to
    private Rect dstRect = new Rect();
    private float dstTopScale = 0;
    private float dstBottomScale = 1;
    private float dstLeftScale = 0;
    private float dstRightScale = 1;

    private volatile boolean mPause;

    public ViESurfaceRenderer(SurfaceView view) {
        Log.d(TAG,"ViESurfaceRenderer create");
        surfaceHolder = view.getHolder();
        if(surfaceHolder == null)
            return;
        surfaceHolder.addCallback(this);
    }

    // surfaceChanged and surfaceCreated share this function
    private void changeDestRect(int dstWidth, int dstHeight) {
        dstRect.right = (int)(dstRect.left + dstRightScale * dstWidth);
        dstRect.bottom = (int)(dstRect.top + dstBottomScale * dstHeight);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
            int in_width, int in_height) {
        changeDestRect(in_width, in_height);
        DrawBitmap();
        Log.d(TAG, "surfaceChanged" +
                " in_width:" + in_width + " in_height:" + in_height +
                " srcRect.left:" + srcRect.left +
                " srcRect.top:" + srcRect.top +
                " srcRect.right:" + srcRect.right +
                " srcRect.bottom:" + srcRect.bottom +
                " dstRect.left:" + dstRect.left +
                " dstRect.top:" + dstRect.top +
                " dstRect.right:" + dstRect.right +
                " dstRect.bottom:" + dstRect.bottom);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG,"surfaceCreated");
        Canvas canvas = surfaceHolder.lockCanvas();
        if(canvas != null) {
            Rect dst = surfaceHolder.getSurfaceFrame();
            if(dst != null) {
                changeDestRect(dst.right - dst.left, dst.bottom - dst.top);
                Log.d(TAG, "surfaceCreated" +
                        " dst.left:" + dst.left +
                        " dst.top:" + dst.top +
                        " dst.right:" + dst.right +
                        " dst.bottom:" + dst.bottom +
                        " srcRect.left:" + srcRect.left +
                        " srcRect.top:" + srcRect.top +
                        " srcRect.right:" + srcRect.right +
                        " srcRect.bottom:" + srcRect.bottom +
                        " dstRect.left:" + dstRect.left +
                        " dstRect.top:" + dstRect.top +
                        " dstRect.right:" + dstRect.right +
                        " dstRect.bottom:" + dstRect.bottom);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
            mPause = false;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mPause = true;
    }

    public Bitmap CreateBitmap(int width, int height) {
        Log.d(TAG, "CreateByteBitmap " + width + ":" + height);
        if (bitmap == null) {
            try {
                android.os.Process.setThreadPriority(
                    android.os.Process.THREAD_PRIORITY_DISPLAY);
            }
            catch (Exception e) {
            }
        }
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        srcRect.left = 0;
        srcRect.top = 0;
        srcRect.bottom = height;
        srcRect.right = width;
        return bitmap;
    }

    public ByteBuffer CreateByteBuffer(int width, int height) {
        Log.d(TAG, "CreateByteBuffer " + width + ":" + height + " " + Thread.currentThread());
        if (bitmap == null) {
            bitmap = CreateBitmap(width, height);
            byteBuffer = ByteBuffer.allocateDirect(width * height * 2);
        }
        surfaceCreated(surfaceHolder);
        return byteBuffer;
    }

    public void SetCoordinates(float left, float top,
            float right, float bottom) {
        Log.d(TAG, "SetCoordinates " + left + "," + top + ":" +
                right + "," + bottom);
        dstLeftScale = left;
        dstTopScale = top;
        dstRightScale = right;
        dstBottomScale = bottom;
    }

    // It saves bitmap data to a JPEG picture, this function is for debug only.
    private void saveBitmapToJPEG(int width, int height) {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteOutStream);

        try{
            FileOutputStream output = new FileOutputStream(String.format(
                "/sdcard/render_%d.jpg", System.currentTimeMillis()));
            output.write(byteOutStream.toByteArray());
            output.flush();
            output.close();
        }
        catch (FileNotFoundException e) {
        }
        catch (IOException e) {
        }
    }

    public void DrawByteBuffer() {
        if(byteBuffer == null){
            Log.d(TAG, "DrawByteBuffer byteBuffer == null");
            return;
        }
        if(mPause){
            Log.d(TAG, "DrawByteBuffer mPause");
            return;
        }
        byteBuffer.rewind();
        bitmap.copyPixelsFromBuffer(byteBuffer);
        DrawBitmap();
    }

    public void DrawBitmap() {
        if(bitmap == null){
            Log.d(TAG, "DrawBitmap bitmap == null");
            return;
        }
        Canvas canvas = surfaceHolder.lockCanvas();
        if(canvas == null) {
            Log.d(TAG, "DrawBitmap canvas == null");
            return;
        }
        // The follow line is for debug only
        // saveBitmapToJPEG(srcRect.right - srcRect.left,srcRect.bottom - srcRect.top);
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

}
