package com.hookedroid.chromecastdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

public class MainActivity extends AppCompatActivity {

    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();

    private class SessionManagerListenerImpl implements SessionManagerListener<CastSession> {
        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            mCastSession = session;
            invalidateOptionsMenu();

            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

            movieMetadata.putString(MediaMetadata.KEY_TITLE, "Test Title");
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, "Test Subtitle");
            movieMetadata.addImage(new WebImage(Uri.parse("https://blog.animationstudies.org/wp-content/uploads/2017/03/Zy2vKfU.png")));
//        movieMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(1))));

            MediaInfo mediaInfo = new MediaInfo.Builder("http://stg-ec-norcal-u.uplynk.com/slices/089/34d28c6069b34f1d96307c80809697d7/089bbf4316aa48d9bdadd94ede08efd3/089bbf4316aa48d9bdadd94ede08efd3_g.mp4")
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("videos/mp4")
                    .setMetadata(movieMetadata)
                    .setStreamDuration(100000)
                    .build();
            RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
            remoteMediaClient.load(mediaInfo, true, 0);
        }
        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            mCastSession = session;

            invalidateOptionsMenu();
        }
        @Override
        public void onSessionEnded(CastSession session, int error) {
            if (session == mCastSession) {
                mCastSession = null;
            }

            invalidateOptionsMenu();
        }

        @Override
        public void onSessionSuspended(CastSession session, int i) {

        }

        @Override
        public void onSessionStarting(CastSession session) {

        }

        @Override
        public void onSessionStartFailed(CastSession session, int i) {

        }

        @Override
        public void onSessionEnding(CastSession session) {

        }

        @Override
        public void onSessionResuming(CastSession session, String s) {

        }

        @Override
        public void onSessionResumeFailed(CastSession session, int i) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        mCastSession = mSessionManager.getCurrentCastSession();
        mSessionManager.addSessionManagerListener(mSessionManagerListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.menu_item_cast);
        return true;
    }
}
