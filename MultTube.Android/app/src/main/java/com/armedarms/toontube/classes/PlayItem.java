package com.armedarms.toontube.classes;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class PlayItem {
    public enum Type {
        PLAYLIST,
        VIDEO,
        CONTROL_PLAY_PLAYLIST,
        CONTROL_BACK
    }

    public Type type;
    public String id;
    public Drawable drawable;

    public static PlayItem _playPlaylistItem;
    public static PlayItem _backItem;

    static {
        _playPlaylistItem = new PlayItem();
        _playPlaylistItem.type = Type.CONTROL_PLAY_PLAYLIST;

        _backItem = new PlayItem();
        _backItem.type = Type.CONTROL_BACK;
    }

    public static PlayItem playlist(String id) {
        PlayItem item = new PlayItem();
        item.type = Type.PLAYLIST;
        item.id = id;
        return item;
    }

    public static PlayItem video(String id) {
        PlayItem item = new PlayItem();
        item.type = Type.VIDEO;
        item.id = id;
        return item;
    }
}
