/*
 *  Copyright (c) 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc.voiceengine;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;

// WebRtcAudioManager handles tasks that uses android.media.AudioManager.
// At construction, storeAudioParameters() is called and it retrieves
// fundamental audio parameters like native sample rate and number of channels.
// The result is then provided to the caller by nativeCacheAudioParameters().
// It is also possible to call init() to set up the audio environment for best
// possible "VoIP performance". All settings done in init() are reverted by
// dispose(). This class can also be used without calling init() if the user
// prefers to set up the audio environment separately. However, it is
// recommended to always use AudioManager.MODE_IN_COMMUNICATION.
// This class also adds support for output volume control of the
// STREAM_VOICE_CALL-type stream.
class WebRtcAudioManager {
  private static final boolean DEBUG = false;

  private static final String TAG = "WebRtcAudioManager";

   // Use 44.1kHz as the default sampling rate.
  private static final int SAMPLE_RATE_HZ = 44100;

  // TODO(henrika): add stereo support for playout.
  private static final int CHANNELS = 1;

  // List of possible audio modes.
  private static final String[] AUDIO_MODES = new String[] {
      "MODE_NORMAL",
      "MODE_RINGTONE",
      "MODE_IN_CALL",
      "MODE_IN_COMMUNICATION",
    };

  private final long nativeAudioManager;
  private final Context context;
  private final AudioManager audioManager;

  private boolean initialized = false;
  private boolean audioModeNeedsRestore = false;
  private int nativeSampleRate;
  private int nativeChannels;
  private int savedAudioMode = AudioManager.MODE_INVALID;

  WebRtcAudioManager(Context context, long nativeAudioManager) {
    Logd("ctor" + WebRtcAudioUtils.getThreadInfo());
    this.context = context;
    this.nativeAudioManager = nativeAudioManager;
    audioManager = (AudioManager) context.getSystemService(
        Context.AUDIO_SERVICE);
    if (DEBUG) {
      WebRtcAudioUtils.logDeviceInfo(TAG);
    }
    storeAudioParameters();
    // TODO(henrika): add stereo support for playout side.
    nativeCacheAudioParameters(
      nativeSampleRate, nativeChannels, nativeAudioManager);
  }

  private boolean init() {
    Logd("init" + WebRtcAudioUtils.getThreadInfo());
    if (initialized) {
      return true;
    }

    // Store current audio state so we can restore it when close() or
    // setCommunicationMode(false) is called.
    savedAudioMode = audioManager.getMode();

    if (DEBUG) {
      Logd("savedAudioMode: " + savedAudioMode);
      Logd("hasEarpiece: " + hasEarpiece());
    }

    initialized = true;
    return true;
  }

  private void dispose() {
    Logd("dispose" + WebRtcAudioUtils.getThreadInfo());
    if (!initialized) {
      return;
    }
    // Restore previously stored audio states.
    if (audioModeNeedsRestore) {
      audioManager.setMode(savedAudioMode);
    }
  }

  private void setCommunicationMode(boolean enable) {
    Logd("setCommunicationMode(" + enable + ")"
        + WebRtcAudioUtils.getThreadInfo());
    assertTrue(initialized);
    if (enable) {
      // Avoid switching mode if MODE_IN_COMMUNICATION is already in use.
      if (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
        return;
      }
      // Switch to COMMUNICATION mode for best possible VoIP performance.
      audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
      audioModeNeedsRestore = true;
      Logd("changing audio mode to: " + AUDIO_MODES[audioManager.getMode()]);
    } else if (audioModeNeedsRestore) {
      // Restore audio mode that was stored in init().
      audioManager.setMode(savedAudioMode);
      audioModeNeedsRestore = false;
      Logd("restoring audio mode to: " + AUDIO_MODES[audioManager.getMode()]);
    }
  }

  private void storeAudioParameters() {
    // Only mono is supported currently (in both directions).
    // TODO(henrika): add support for stereo playout.
    nativeChannels = CHANNELS;
    // Get native sample rate and store it in |nativeSampleRate|.
    // Most common rates are 44100 and 48000 Hz.
    if (!WebRtcAudioUtils.runningOnJellyBeanMR1OrHigher()) {
      nativeSampleRate = SAMPLE_RATE_HZ;
    } else {
      String sampleRateString = audioManager.getProperty(
          AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
      nativeSampleRate = (sampleRateString == null) ?
          SAMPLE_RATE_HZ : Integer.parseInt(sampleRateString);
    }
    Logd("nativeSampleRate: " + nativeSampleRate);
    Logd("nativeChannels: " + nativeChannels);
  }

  /** Gets the current earpiece state. */
  private boolean hasEarpiece() {
    return context.getPackageManager().hasSystemFeature(
        PackageManager.FEATURE_TELEPHONY);
  }

  /** Helper method which throws an exception  when an assertion has failed. */
  private static void assertTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError("Expected condition to be true");
    }
  }

  private static void Logd(String msg) {
    Log.d(TAG, msg);
  }

  private static void Loge(String msg) {
    Log.e(TAG, msg);
  }

  private native void nativeCacheAudioParameters(
      int sampleRate, int channels, long nativeAudioManager);
}