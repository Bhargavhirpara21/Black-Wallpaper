/*
 * Copyright 2019 Alynx Zhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wallpaper.black.live.uhd.Services;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;

import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;

public class ExoVideoService extends WallpaperService {
    @SuppressWarnings("unused")
    private static final String TAG = "GLWallpaperService";
    private String videoWallpaperPath;
    private InAppPreference inAppPreference;
    public static boolean isExoServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        inAppPreference = InAppPreference.getInstance(this);
        videoWallpaperPath = inAppPreference.getVideoLiveWallpaperPath();
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            inAppPreference =null;
            videoWallpaperPath =null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoggerCustom.erorr(TAG,"onDestroy");
    }

    class GLLiveWallpaperEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences sharedPreferences;
        private static final String TAG = "GLWallpaperEngine";
        private Context mContext;
        private GLWallpaperSurfaceView glWallpaperSurfaceView = null;
        private SimpleExoPlayer simpleExoPlayer = null;
        private MediaSource mediaSource = null;
        private DefaultTrackSelector defaultTrackSelector = null;
        private GLWallpaperRenderer glWallpaperRenderer = null;
        private int videoRotation = 0;
        private int videoWidth = 0;
        private int videoHeight = 0;
        private long progress = 0;
        private boolean isPreview = false;

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        }

        private class GLWallpaperSurfaceView extends GLSurfaceView {
            @SuppressWarnings("unused")
            private static final String TAG = "GLWallpaperSurface";

            public GLWallpaperSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            void onDestroy() {
                super.onDetachedFromWindow();
            }
        }

        GLLiveWallpaperEngine(@NonNull final Context context) {
            this.mContext = context;
            setTouchEventsEnabled(false);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            Log.e(TAG, "onCreate: " + videoWallpaperPath);
            if (!isPreview()) {
                isExoServiceRunning = true;
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
            isPreview = isPreview();

            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            if (inAppPreference == null)
                inAppPreference = InAppPreference.getInstance(ExoVideoService.this);

            if (isPreview && !TextUtils.isEmpty(inAppPreference.getVideoLiveWallpaperPathTemp())) {
                videoWallpaperPath = inAppPreference.getVideoLiveWallpaperPathTemp();
            } else {
                if (TextUtils.isEmpty(videoWallpaperPath) || !inAppPreference.getVideoLiveWallpaperPath().equalsIgnoreCase(videoWallpaperPath)) {
                    videoWallpaperPath = inAppPreference.getVideoLiveWallpaperPath();
                }
            }
            createWallSurfaceViewer();
            int width = surfaceHolder.getSurfaceFrame().width();
            int height = surfaceHolder.getSurfaceFrame().height();
            if(glWallpaperRenderer!=null)
                glWallpaperRenderer.setScreenSize(width, height);
            startPlay();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (glWallpaperRenderer != null) {
                if (visible) {
                    if(glWallpaperSurfaceView !=null)
                        glWallpaperSurfaceView.onResume();
                    startPlay();
                } else {
                stopVideoPlayer(false);
		        if(glWallpaperSurfaceView !=null)
                    glWallpaperSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xOffsetStep, float yOffsetStep,
                                     int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep,
                    yOffsetStep, xPixelOffset, yPixelOffset);
            if (!isPreview()) {
                if(glWallpaperRenderer!=null)
                    glWallpaperRenderer.setOffset(0.5f - xOffset, 0.5f - yOffset);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int format,
                                     int width, int height) {
            super.onSurfaceChanged(surfaceHolder, format, width, height);
            if(glWallpaperRenderer!=null)
                glWallpaperRenderer.setScreenSize(width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (!isPreview) {
                isExoServiceRunning = false;
            }
            stopVideoPlayer(true);
	        if(glWallpaperSurfaceView !=null)
            	glWallpaperSurfaceView.onDestroy();
 	        glWallpaperSurfaceView =null;
            glWallpaperRenderer=null;
            mContext =null;
            LoggerCustom.erorr(TAG,"onSurfaceDestroyed");

        }

        private void createWallSurfaceViewer() {
            if (glWallpaperSurfaceView != null) {
                glWallpaperSurfaceView.onDestroy();
                glWallpaperSurfaceView = null;
            }
            if(mContext ==null)
                return;

            glWallpaperSurfaceView = new GLWallpaperSurfaceView(mContext);
            final ActivityManager activityManager = (ActivityManager) getSystemService(
                    Context.ACTIVITY_SERVICE
            );
            if (activityManager == null) {
                throw new RuntimeException("Cannot get ActivityManager");
            }
            final ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
            if (configInfo.reqGlEsVersion >= 0x30000) {
                LoggerCustom.erorr(TAG, "Support GLESv3");
                if(glWallpaperSurfaceView !=null)
                    glWallpaperSurfaceView.setEGLContextClientVersion(3);
                glWallpaperRenderer = new GLES30WallpaperRenderer(mContext);
            } else if (configInfo.reqGlEsVersion >= 0x20000) {
                LoggerCustom.erorr(TAG, "Fallback to GLESv2");
                if(glWallpaperSurfaceView !=null)
                    glWallpaperSurfaceView.setEGLContextClientVersion(2);
                glWallpaperRenderer = new GLES20WallpaperRenderer(mContext);
            } else {
                throw new RuntimeException("Needs GLESv2 or higher");
            }
            if(glWallpaperSurfaceView !=null) {
                glWallpaperSurfaceView.setPreserveEGLContextOnPause(true);
                glWallpaperSurfaceView.setRenderer(glWallpaperRenderer);
                // On demand render will lead to black screen.
                glWallpaperSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            }
        }

        private void getLiveWallpaperMetadata(Uri uri) {

            final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(String.valueOf(uri));
            final String rotation = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
            );
            final String width = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
            );
            final String height = mmr.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
            );
            try {
                mmr.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!TextUtils.isEmpty(rotation))
                videoRotation = Integer.parseInt(rotation);

            if(!TextUtils.isEmpty(width))
                videoWidth = Integer.parseInt(width);
            else
                videoWidth=1080;

            if(!TextUtils.isEmpty(height))
                videoHeight = Integer.parseInt(height);
            else
                videoHeight=1920;

            LoggerCustom.erorr(TAG,"videoRotation:"+videoRotation+" videoWidth:"+videoWidth+" videoHeight:"+videoHeight);
        }

        private void startPlay() {
            String tempPath= videoWallpaperPath;
            if (inAppPreference == null) {
                inAppPreference = InAppPreference.getInstance(ExoVideoService.this);
            }
            if (isPreview && !TextUtils.isEmpty(inAppPreference.getVideoLiveWallpaperPathTemp())) {
                videoWallpaperPath = inAppPreference.getVideoLiveWallpaperPathTemp();
            } else {
                videoWallpaperPath = inAppPreference.getVideoLiveWallpaperPath();
            }

            LoggerCustom.erorr("VideoWallpaperService", "resetPlayer:" + videoWallpaperPath);
            if (TextUtils.isEmpty(videoWallpaperPath)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    if (!isPreview) {
                        videoWallpaperPath = inAppPreference.getVideoLiveWallpaperPathTemp();
                    }
                } else {
                    return;
                }
            }

            try {
                if(!tempPath.equalsIgnoreCase(videoWallpaperPath)){
                    videoWidth=0;
                    videoHeight=0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Uri uri = Uri.parse(videoWallpaperPath);
//            if (exoPlayer != null) {
//                stopPlayer();
//            }
            LoggerCustom.erorr(TAG, "Player starting");
            if(TextUtils.isEmpty(videoWallpaperPath))
                return;

            File file=new File(videoWallpaperPath);
            if(file!=null && file.exists()){
                try {
                    if(videoWidth==0 && videoHeight==0)
                        getLiveWallpaperMetadata(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if(mContext ==null)
                    return;

                defaultTrackSelector = new DefaultTrackSelector();
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, defaultTrackSelector);
                simpleExoPlayer.setVolume(0.0f);

                final int count = simpleExoPlayer.getRendererCount();
                for (int i = 0; i < count; ++i) {
                    try {
                        if (simpleExoPlayer.getRendererType(i) == C.TRACK_TYPE_AUDIO) {
                            defaultTrackSelector.setParameters(
                                    defaultTrackSelector.buildUponParameters().setRendererDisabled(i, true)
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                final DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                        mContext, Util.getUserAgent(mContext, "dark.black.live.wallpapers"));

                mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                glWallpaperRenderer.setVideoSizeAndRotation(videoWidth, videoHeight, videoRotation);
                glWallpaperRenderer.setSourcePlayer(simpleExoPlayer);
                simpleExoPlayer.prepare(mediaSource);
                simpleExoPlayer.seekTo(progress);
                simpleExoPlayer.setPlayWhenReady(true);
                Log.e(TAG, "startPlayer: " + simpleExoPlayer.getPlayWhenReady());
                Log.e(TAG, "startPlayer: " + uri);
            }else{
                LoggerCustom.erorr(TAG,"stopSelf");
                stopSelf();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    WallpaperManager myWallpaperManager
                            = WallpaperManager.getInstance(getApplicationContext());
                    try {
                        myWallpaperManager.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void stopVideoPlayer(boolean isRelease) {
            if (simpleExoPlayer != null) {
                if (simpleExoPlayer.getPlayWhenReady()) {
                    LoggerCustom.erorr(TAG, "Player stopping");
                    simpleExoPlayer.setPlayWhenReady(false);
                    progress = simpleExoPlayer.getCurrentPosition();
                    simpleExoPlayer.stop();
                }
                try {
                    if(isRelease)
                        simpleExoPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                simpleExoPlayer = null;
            }
            mediaSource = null;
            defaultTrackSelector = null;
        }
    }

    @Override
    public Engine onCreateEngine() {
        return new GLLiveWallpaperEngine(this);
    }
}
