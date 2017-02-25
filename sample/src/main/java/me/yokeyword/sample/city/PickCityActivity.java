package me.yokeyword.sample.city;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yokeyword.indexablerv.EntityWrapper;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableHeaderAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import me.yokeyword.indexablerv.SimpleHeaderAdapter;
import me.yokeyword.sample.R;
import me.yokeyword.sample.ToastUtil;
import me.yokeyword.sample.contact.MenuEntity;
import me.yokeyword.sample.contact.PickContactActivity;

/**
 * 选择城市
 * Created by YoKey on 16/10/7.
 */
public class PickCityActivity extends AppCompatActivity {
    private List<CityEntity> mDatas;
    private SearchFragment mSearchFragment;
    private SearchView mSearchView;
    private FrameLayout mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_city);
        getSupportActionBar().setTitle("选择城市");

        mSearchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
        IndexableLayout indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);
        mSearchView = (SearchView) findViewById(R.id.searchview);
        mProgressBar = (FrameLayout) findViewById(R.id.progress);

        // setAdapter
        CityAdapter adapter = new CityAdapter(this);
        indexableLayout.setAdapter(adapter);
        // set Datas
        mDatas = initDatas();

        // 快速排序。  排序规则设置为：只按首字母  （默认全拼音排序）  效率很高，是默认的10倍左右。  按需开启～
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST);

        adapter.setDatas(mDatas, new IndexableAdapter.IndexCallback<CityEntity>() {
            @Override
            public void onFinished(List<EntityWrapper<CityEntity>> datas) {
                // 数据处理完成后回调
                mSearchFragment.bindDatas(mDatas);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        // set Center OverlayView
        indexableLayout.setOverlayStyle_Center();

        // set Listener
        adapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<CityEntity>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, CityEntity entity) {
                if (originalPosition >= 0) {
                    ToastUtil.showShort(PickCityActivity.this, "选中:" + entity.getName() + "  当前位置:" + currentPosition + "  原始所在数组位置:" + originalPosition);
                } else {
                    ToastUtil.showShort(PickCityActivity.this, "选中Header:" + entity.getName() + "  当前位置:" + currentPosition);
                }
            }
        });

        adapter.setOnItemTitleClickListener(new IndexableAdapter.OnItemTitleClickListener() {
            @Override
            public void onItemClick(View v, int currentPosition, String indexTitle) {
                ToastUtil.showShort(PickCityActivity.this, "选中:" + indexTitle + "  当前位置:" + currentPosition);
            }
        });

        // 添加 HeaderView DefaultHeaderAdapter接收一个IndexableAdapter, 使其布局以及点击事件和IndexableAdapter一致
        // 如果想自定义布局,点击事件, 可传入 new IndexableHeaderAdapter

        // 热门城市
        //indexableLayout.addHeaderAdapter(new SimpleHeaderAdapter<>(adapter, "热", "热门城市", iniyHotCityDatas()));
        indexableLayout.addHeaderAdapter(new HotCityHeaderAdapter("热", "热门城市", iniyHotCityDatas()));
        // 定位
        final List<CityEntity> gpsCity = iniyGPSCityDatas();
        final SimpleHeaderAdapter gpsHeaderAdapter = new SimpleHeaderAdapter<>(adapter, "定", "当前城市", gpsCity);
        indexableLayout.addHeaderAdapter(gpsHeaderAdapter);

        // 模拟定位
        indexableLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                gpsCity.get(0).setName("杭州市");
                gpsHeaderAdapter.notifyDataSetChanged();
            }
        }, 3000);

        // 搜索Demo
        initSearch();
    }

    /**
     * 自定义的MenuHeader
     */
    class HotCityHeaderAdapter extends IndexableHeaderAdapter<HotCityC> {
        private static final int TYPE = 1;

        public HotCityHeaderAdapter(String index, String indexTitle, List<HotCityC> datas) {
            super(index, indexTitle, datas);
        }

        @Override
        public int getItemViewType() {
            return TYPE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
            return new HotCityHeaderAdapter.VH(LayoutInflater.from(PickCityActivity.this).inflate(R.layout.cp_view_hot_city, parent, false));
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, HotCityC entity) {
            HotCityHeaderAdapter.VH vh = (HotCityHeaderAdapter.VH) holder;
            final MyHotCityGridAdapter hotCityGridAdapter = new MyHotCityGridAdapter(holder.itemView.getContext(), entity.getCity());
            vh.gridView.setAdapter(hotCityGridAdapter);
            vh.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (onCityClickListener != null) {
//                        onCityClickListener.onCityClick(hotCityGridAdapter.getItem(position));
//                    }
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
    }

    private List<CityEntity> initDatas() {
        List<CityEntity> list = new ArrayList<>();
        List<String> cityStrings = Arrays.asList(getResources().getStringArray(R.array.city_array));
        for (String item : cityStrings) {
            CityEntity cityEntity = new CityEntity();
            cityEntity.setName(item);
            list.add(cityEntity);
        }
        return list;
    }

    private List<HotCityC> iniyHotCityDatas() {
        List<HotCityC> list = new ArrayList<>();
        list.add(new HotCityC(0, getHotCity()));
        return list;
    }

    private List<String> getHotCity() {
        List<String> mCities;
        mCities = new ArrayList<>();
        mCities.add("北京");
        mCities.add("上海");
        mCities.add("广州");
        mCities.add("深圳");
        mCities.add("杭州");
        mCities.add("南京");
        mCities.add("天津");
        mCities.add("武汉");
        mCities.add("重庆");
        return mCities;
    }

//    private List<CityEntity> iniyHotCityDatas() {
//        List<CityEntity> list = new ArrayList<>();
//        list.add(new CityEntity("杭州市"));
//        list.add(new CityEntity("北京市"));
//        list.add(new CityEntity("上海市"));
//        list.add(new CityEntity("广州市"));
//        return list;
//    }

    private List<CityEntity> iniyGPSCityDatas() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("定位中..."));
        return list;
    }

    private void initSearch() {
        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 0) {
                    if (mSearchFragment.isHidden()) {
                        getSupportFragmentManager().beginTransaction().show(mSearchFragment).commit();
                    }
                } else {
                    if (!mSearchFragment.isHidden()) {
                        getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
                    }
                }

                mSearchFragment.bindQueryText(newText);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mSearchFragment.isHidden()) {
            // 隐藏 搜索
            mSearchView.setQuery(null, false);
            getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
            return;
        }
        super.onBackPressed();
    }
}
