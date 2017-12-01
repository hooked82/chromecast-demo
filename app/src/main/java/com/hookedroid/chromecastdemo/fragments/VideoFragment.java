package com.hookedroid.chromecastdemo.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.hookedroid.chromecastdemo.ChromecastApplication;
import com.hookedroid.chromecastdemo.R;
import com.hookedroid.chromecastdemo.constants.AppConstants;
import com.hookedroid.chromecastdemo.logging.VideoEventLogger;
import com.hookedroid.chromecastdemo.model.VideoModel;

public class VideoFragment extends Fragment implements Player.EventListener {

    public static final String TAG = VideoFragment.class.getSimpleName();

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private Handler mMainHandler;
    private VideoEventLogger mEventLogger;

    private SimpleExoPlayerView mPlayerView;
//    private FrameLayout mVideoContentFrame;
//    private TextView mStationId;
//    private TextView mVideoUrl;

    private SimpleExoPlayer mPlayer;

    private DefaultTrackSelector mTrackSelector;
    private TrackSelection.Factory mTrackSelectionFactory;

    private VideoModel mModel;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(@NonNull VideoModel model) {
        VideoFragment fragment = new VideoFragment();

        Bundle args = new Bundle();
        args.putParcelable(AppConstants.EXTRA_MODEL, model);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mModel = getArguments().getParcelable(AppConstants.EXTRA_MODEL);

            if (mModel != null) {
                Log.d(TAG, "Video: " + mModel.getTitle());
            }
        }

        mMainHandler = new Handler();

        mTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        mTrackSelector = new DefaultTrackSelector(mTrackSelectionFactory);

        mEventLogger = new VideoEventLogger(mTrackSelector);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        mPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.video_player);

//        mPlayerView.findViewById(R.id.volume).setOnClickListener(this);
//        mPlayerView.findViewById(R.id.fullscreen).setOnClickListener(this);
//        mPlayerView.findViewById(R.id.captions).setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPlayer = ExoPlayerFactory.newSimpleInstance(
                getContext(),
                mTrackSelector
        );

        final MediaSource mediaSource = buildMediaSource(Uri.parse(mModel.getVideoUrl()));

        mPlayer.addListener(this);
        mPlayer.addListener(mEventLogger);
        mPlayer.setAudioDebugListener(mEventLogger);
        mPlayer.setVideoDebugListener(mEventLogger);

        mPlayerView.setPlayer(mPlayer);

        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d(TAG, "Tracks Changed");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "Player State Changed");
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    private MediaSource buildMediaSource(Uri uri) {
        return new HlsMediaSource(uri, buildDataSourceFactory(true), mMainHandler, mEventLogger);
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((ChromecastApplication) getActivity().getApplication())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mPlayer != null) {
            mPlayer.release();
        }
    }
}
