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

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ViERenderer {
    private static SurfaceHolder g_localRenderer;
    public static SurfaceView CreateRenderer(Context context) {
        return CreateRenderer(context, false);
    }

    public static SurfaceView CreateRenderer(Context context,
            boolean useOpenGLES2) {
        if(useOpenGLES2 == true && ViEAndroidGLES20.IsSupported(context))
            return new ViEAndroidGLES20(context);
        else
            return new SurfaceView(context);
    }

    public static SurfaceView CreateLocalRenderer(Context context) {
        SurfaceView localRender = new SurfaceView(context);
        g_localRenderer = localRender.getHolder();
        g_localRenderer.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);return localRender;

    }

    public static SurfaceHolder GetLocalRenderer() {
        return g_localRenderer;
    }
}
