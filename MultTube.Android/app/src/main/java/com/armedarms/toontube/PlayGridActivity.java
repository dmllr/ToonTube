package com.armedarms.toontube;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.armedarms.toontube.adapters.PlayGridAdapter;
import com.armedarms.toontube.classes.PlayItem;
import com.armedarms.toontube.classes.PlayItemIterator;
import com.armedarms.toontube.youtube.DeveloperKey;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * http://armedarms.com/
 */
public class PlayGridActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_play_grid);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlayGridFragment())
                    .commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            // do nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public static class PlayGridFragment
        extends
            Fragment
        implements
            YouTubePlayer.OnInitializedListener,
            YouTubeThumbnailView.OnInitializedListener
    {

        private boolean isInitialPlaylist;
        private String playlistId;
        private final ArrayList<PlayItem> playlist = new ArrayList<PlayItem>(10);
        private final PlayItemIterator playlistIterator = new PlayItemIterator();

        private YouTubeThumbnailView thumbnailView;
        private YouTubeThumbnailLoader thumbnailLoader;
        private YouTubePlayerSupportFragment playerFragment;
        private YouTubePlayer player;
        private View playerView;

        private boolean resumed;
        private State state;

        private enum State {
            UNINITIALIZED,
            LOADING_THUMBNAILS,
        }

        private GridView playGrid;
        private ViewGroup errorView;
        private ViewGroup progressView;

        public PlayGridFragment() {
            state = State.UNINITIALIZED;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // ДА, Я ЗНАЮ! Каталог должен быть в ресурсах свой в разбивке по языкам-странам или вообще качаться извне. ЗНАЮ.
            playlist.add(PlayItem.playlist("PLXnIohISHNIvbhOG_TkVaGCOglsuWM4CQ"));
            playlist.add(PlayItem.playlist("PL0C60546EB06D619A"));
            playlist.add(PlayItem.playlist("PLF40DD57434E48E66"));
            playlist.add(PlayItem.playlist("LLdgDIIKpFlpHB1L0LZnh5EQ"));
            playlist.add(PlayItem.playlist("SPeVA7eICJ6d2SuuluqOfYXke_6YgAM-7_"));
            playlist.add(PlayItem.playlist("PLeVA7eICJ6d0lUOAYM7o5aZvfkeHGxV0H"));
            playlist.add(PlayItem.playlist("PL425xO8HuUCcPI6-X62YIJae4_9PZA64L"));
            playlist.add(PlayItem.playlist("PL425xO8HuUCdZMs2dIgyvRzHR9N_-H3UT"));
            playlist.add(PlayItem.playlist("PL425xO8HuUCdiXT2PJ8u_5B7nNBXxRtuT"));
            playlist.add(PlayItem.playlist("PL425xO8HuUCe1UNf_thY4WlCU7Vp7cViL"));
            playlist.add(PlayItem.playlist("PL425xO8HuUCesSo7sdae1L8Vro0PjuKKa"));
            playlist.add(PlayItem.playlist("PL4CB19A6AF0747C00"));
            playlist.add(PlayItem.playlist("PL5D1AA44DF2E41EA6"));
            playlist.add(PlayItem.playlist("PL425xO8HuUCewRfoc094TPCG4y2nnXu4h"));
            playlist.add(PlayItem.playlist("PLA7F02C76BA376766"));

            playlist.add(PlayItem.video("XK0LN8EdKnw"));
            playlist.add(PlayItem.video("HwV_vpY3wB4"));
            playlist.add(PlayItem.video("rgSNmapyrHw"));
            playlist.add(PlayItem.video("8CzrPCZYr7Y"));
            playlist.add(PlayItem.video("TI-4W6Hm6QQ"));
            playlist.add(PlayItem.video("Zb4MmPqzo9A"));
            playlist.add(PlayItem.video("MWwILW7ebTs"));
            playlist.add(PlayItem.video("o_UBam8BCjI"));
            playlist.add(PlayItem.video("syxUdDka0ls"));
            playlist.add(PlayItem.video("1PmGG10KLCI"));

            Bundle extras = getActivity().getIntent().getExtras();
            if (extras == null) {
                playlistId = null;
            } else {
                playlistId = extras.getString(getString(R.string.playlist_id));
            }

            isInitialPlaylist = playlistId == null || "".equals(playlistId);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(getString(R.string.playerVisibility), playerView.getVisibility());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_play_grid, container, false);

            errorView = (ViewGroup)rootView.findViewById(R.id.error);

            progressView = (ViewGroup)rootView.findViewById(android.R.id.progress);

            playGrid = (GridView)rootView.findViewById(R.id.videoGrid);
            playGrid.setAdapter(new PlayGridAdapter(getActivity().getApplicationContext(), !isInitialPlaylist));
            playGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PlayItem item = ((PlayItem) getAdapter().getItem(position));
                    switch (item.type) {
                        case VIDEO:
                            openVideo(item);
                            break;
                        case PLAYLIST:
                            Intent __intent = new Intent(getActivity(), PlayGridActivity.class);
                            __intent.putExtra(getString(R.string.playlist_id), item.id);
                            startActivity(__intent);
                            break;
                        case CONTROL_PLAY_PLAYLIST:
                            openPlaylist();
                            break;
                        case CONTROL_BACK:
                            getActivity().finish();
                            break;
                    }
                }
            });

            playerView = getActivity().findViewById(R.id.playerFragment);
            if (savedInstanceState != null) {
                //noinspection ResourceType
                playerView.setVisibility(savedInstanceState.getInt(getString(R.string.playerVisibility), View.GONE));
            } else {
                playerView.setVisibility(View.GONE);
            }
            playerFragment = (YouTubePlayerSupportFragment)(getActivity().getSupportFragmentManager().findFragmentById(R.id.playerFragment));
            playerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);

            thumbnailView = new YouTubeThumbnailView(getActivity().getApplicationContext());
            thumbnailView.initialize(DeveloperKey.DEVELOPER_KEY, this);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            resumed = true;
            if (thumbnailLoader != null && player != null) {
                if (state.equals(State.LOADING_THUMBNAILS)) {
                    loadNextThumbnail();
                } else if (state.equals(State.UNINITIALIZED)) {
                    setPlaylist();
                }
            }
        }

        @Override
        public void onPause() {
            resumed = false;
            super.onPause();
        }

        @Override
        public void onDestroy() {
            if (thumbnailLoader != null)
                thumbnailLoader.release();
            if (player != null)
                player.release();

            super.onDestroy();
        }

        PlayGridAdapter getAdapter()
        {
            return (PlayGridAdapter) playGrid.getAdapter();
        }

        private void openVideo(PlayItem item) {
            playerView.setVisibility(View.VISIBLE);
            player.loadVideo(item.id);
        }

        private void openPlaylist() {
            playerView.setVisibility(View.VISIBLE);
            player.loadPlaylist(playlistId);
        }

        private void setPlaylist() {
            if ( resumed && thumbnailLoader != null && state.equals(State.UNINITIALIZED)) {
                state = State.LOADING_THUMBNAILS;

                if (isInitialPlaylist)
                    playlistIterator.start(playlist, new PlaylistIterateListener());
                else
                    thumbnailLoader.setPlaylist(playlistId);
            }
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player, boolean b) {
            this.player = player;
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult initializationResult) {
            if (initializationResult == YouTubeInitializationResult.SERVICE_MISSING) {
                new YouTubeInstallDialogFragment().show(getFragmentManager(), YouTubeInstallDialogFragment.class.getName());
            }

        }

        @Override
        public void onInitializationSuccess(YouTubeThumbnailView thumbnailView, YouTubeThumbnailLoader thumbnailLoader) {
            this.thumbnailLoader = thumbnailLoader;
            thumbnailLoader.setOnThumbnailLoadedListener(new ThumbnailListener());

            setPlaylist();
        }

        @Override
        public void onInitializationFailure(YouTubeThumbnailView thumbnailView, YouTubeInitializationResult initializationResult) {
            initializationResult.ordinal();
        }

        private void loadNextThumbnail() {
            progressView.setVisibility(View.GONE);

            if (!isInitialPlaylist && thumbnailLoader.hasNext()) {
                thumbnailLoader.next();
            } else {
                playlistIterator.next();
            }
        }

        private final class PlaylistIterateListener implements PlayItemIterator.PlayItemIterateListener {
            @Override
            public void onIterate(PlayItem item) {
                if (item.type == PlayItem.Type.VIDEO)
                    thumbnailLoader.setVideo(item.id);
                if (item.type == PlayItem.Type.PLAYLIST)
                    thumbnailLoader.setPlaylist(item.id);
            }
        }

        private final class ThumbnailListener implements
                YouTubeThumbnailLoader.OnThumbnailLoadedListener {

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView thumbnail, String videoId) {
                PlayItem item = new PlayItem();
                item.type = isInitialPlaylist ? playlistIterator.current().type : PlayItem.Type.VIDEO;
                if (item.type == PlayItem.Type.PLAYLIST)
                    item.id = playlistIterator.current().id;
                if (item.type == PlayItem.Type.VIDEO)
                    item.id = videoId;
                item.drawable = thumbnail.getDrawable();

                getAdapter().add(item);

                if (resumed) {
                    if (state.equals(State.LOADING_THUMBNAILS))
                        loadNextThumbnail();
                }
            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView thumbnail, YouTubeThumbnailLoader.ErrorReason reason) {
                // TODO NETWORK_ERROR on fake playlist
                if (reason == YouTubeThumbnailLoader.ErrorReason.NETWORK_ERROR) {
                    connectionError();
                }
                else
                    loadNextThumbnail();
            }
        }

        private void connectionError() {
            progressView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
            // TODO Try again
        }

        public static final class YouTubeInstallDialogFragment extends DialogFragment {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.install_youtube)
                    .setPositiveButton(R.string.install_youtube_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final String appPackageName = "com.google.android.youtube";
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    })
                    .setNegativeButton(R.string.install_youtube_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });

                return builder.create();
            }
        }
    }
}
