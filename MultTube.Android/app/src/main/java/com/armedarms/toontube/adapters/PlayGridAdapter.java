package com.armedarms.toontube.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.armedarms.toontube.R;
import com.armedarms.toontube.classes.PlayItem;

import java.util.ArrayList;

public class PlayGridAdapter extends BaseAdapter {
    private final LayoutInflater _layoutInflater;
    Context _context;
    ArrayList<PlayItem> _list;
    boolean _needControls;

    public PlayGridAdapter(Context context, boolean needControls)
    {
        _context = context;
        _needControls = needControls;
        _layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        _list = new ArrayList<PlayItem>();
    }

    @Override
    public int getCount() {
        if (_list == null)
            return 0;

        int __size = _list.size();

        __size += ((_needControls && __size > 1) ? 2 : 0); // PlayItem.Type.CONTROL_PLAY_PLAYLIST + PlayItem.Type.CONTROL_BACK

        return __size;
    }

    @Override
    public Object getItem(int i) {
        if (_needControls) {
            if (i == 0)
                return PlayItem._backItem;
            if (i == 1)
                return PlayItem._playPlaylistItem;

            i -= 2;
        }

        return _list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return PlayItem.Type.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        PlayItem __item = (PlayItem)getItem(position);

        return __item.type.ordinal();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PlayItem item =  (PlayItem)getItem(i);

        switch (item.type) {
            case PLAYLIST:
                if (view == null)
                    view = _layoutInflater.inflate(R.layout.play_grid_item, viewGroup, false);
                ((ImageView) view.findViewById(R.id.itemImage)).setImageDrawable(item.drawable);
                view.findViewById(R.id.imagePlaylist).setVisibility(View.VISIBLE);
                break;
            case VIDEO:
                if (view == null)
                    view = _layoutInflater.inflate(R.layout.play_grid_item, viewGroup, false);
                ((ImageView) view.findViewById(R.id.itemImage)).setImageDrawable(item.drawable);
                break;
            case CONTROL_PLAY_PLAYLIST:
                if (view == null)
                    view = _layoutInflater.inflate(R.layout.play_grid_item_playall, viewGroup, false);
                break;
            case CONTROL_BACK:
                if (view == null)
                    view = _layoutInflater.inflate(R.layout.play_grid_item_back, viewGroup, false);
                break;
        }

        return view;
    }

    public PlayGridAdapter add(PlayItem item) {
        _list.add(item);
        notifyDataSetChanged();

        return this;
    }
}
