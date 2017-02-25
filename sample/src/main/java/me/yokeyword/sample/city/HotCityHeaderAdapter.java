package me.yokeyword.sample.city;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import me.yokeyword.indexablerv.IndexableHeaderAdapter;
import me.yokeyword.sample.R;

/**
 * 自定义的HotCityHeaderAdapter
 */
public class HotCityHeaderAdapter extends IndexableHeaderAdapter<HotCityC> {
    private static final int TYPE = 1;
    private OnCityClickListener onCityClickListener;

    public HotCityHeaderAdapter(String index, String indexTitle, List<HotCityC> datas) {
        super(index, indexTitle, datas);
    }

    @Override
    public int getItemViewType() {
        return TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        return new HotCityHeaderAdapter.VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.cp_view_hot_city, parent, false));
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, HotCityC entity) {
        HotCityHeaderAdapter.VH vh = (HotCityHeaderAdapter.VH) holder;
        final MyHotCityGridAdapter hotCityGridAdapter = new MyHotCityGridAdapter(holder.itemView.getContext(), entity.getCity());
        vh.gridView.setAdapter(hotCityGridAdapter);
        vh.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (onCityClickListener != null) {
                        onCityClickListener.onCityClick(position, hotCityGridAdapter.getItem(position));
                    }
            }
        });
    }

    private class VH extends RecyclerView.ViewHolder {
        private GridView gridView;

        public VH(View itemView) {
            super(itemView);
            gridView = (GridView) itemView.findViewById(R.id.gridview_hot_city);
        }
    }

    public HotCityHeaderAdapter setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
        return this;
    }
    public interface OnCityClickListener {
        void onCityClick(int index, String name);
    }
}