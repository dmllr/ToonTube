package com.armedarms.toontube.classes;

import java.util.ArrayList;

public class PlayItemIterator {
    ArrayList<PlayItem> playlist;
    PlayItemIterateListener listener;
    int index;
    int size;

    public void start(ArrayList<PlayItem> playlist, PlayItemIterateListener listener) {
        this.playlist = playlist;
        this.listener = listener;
        index = 0;
        size = playlist.size();

        iterate();
    }

    private void iterate() {
        if (index < size)
            listener.onIterate(playlist.get(index));
    }

    public boolean hasNext() {
        return index + 1 < size;
    }

    public void next() {
        index++;
        iterate();
    }

    public PlayItem current() {
        return playlist.get(index);
    }

    public static interface PlayItemIterateListener {
        void onIterate(PlayItem item);
    }
}
