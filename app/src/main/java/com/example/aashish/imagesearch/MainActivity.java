package com.example.aashish.imagesearch;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aashish.imagesearch.models.ApiResult;
import com.example.aashish.imagesearch.models.Page;
import com.example.aashish.imagesearch.models.Thumbnail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    private EditText mSearchEditText;
    private TextView mTvError;
    private RecyclerView mRvImages;
    private List<Page> mPages = new ArrayList<>();
    private Map<String, Page> mCachedPageList = new LinkedHashMap<>();

    private final int ADD = 0;
    private final int SUBTRACT = 1;
    private int operation = 0;

    private MyAdapter mAdapter;

    private Context mContext;
    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;

        mSearchEditText = (EditText) findViewById(R.id.edit_query);
        mSearchEditText.addTextChangedListener(this);
        mRvImages = (RecyclerView) findViewById(R.id.rv_images);
        mTvError = (TextView) findViewById(R.id.tv_error);

        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRvImages.setLayoutManager(mLayoutManager);
        mRvImages.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new MyAdapter();
        mRvImages.setAdapter(mAdapter);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkConnected()) {
            mTvError.setText(getResources().getString(R.string.error_no_internet));
            mTvError.setVisibility(View.VISIBLE);
            mSearchEditText.setEnabled(false);
        } else {
            mTvError.setVisibility(View.GONE);
            mSearchEditText.setEnabled(true);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    /***
     * Makes the API call and fetches the result.
     * @param searchQuery
     */
    private void fetchPages(final String searchQuery) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResult> api = apiInterface.getPages(ApiInterface.ACTION, ApiInterface.PROP, ApiInterface.FORMAT,
                ApiInterface.PIPROP, ApiInterface.PILIMIT, ApiInterface.GENERATOR,
                (mScreenWidth / 2), searchQuery);

        api.enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getPages().size() == 0) {
                        mTvError.setText(getResources().getString(R.string.error_no_results));
                        mTvError.setVisibility(View.VISIBLE);
                    } else {
                        mTvError.setVisibility(View.GONE);
                        for (Page page : response.body().getPages()) {
                            mCachedPageList.put(page.getTitle(), page);
                        }
                        invalidateStoredPages(searchQuery.toLowerCase());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResult> call, Throwable t) {
                Log.e("RETRO", t.toString());
            }
        });
    }

    /***
     * Avoiding redundant API calls by copying values from the CachedHashmap
     * incase user removes some character from the queryString.
     * @param searchQuery
     */
    private void invalidateStoredPages(String searchQuery) {
        synchronized (mPages) {
            int i = 0;
            while (i < mPages.size()) {
                Page page = mPages.get(i);
                if (!page.getTitle().startsWith(searchQuery)) {
                    mPages.remove(i);
                    mCachedPageList.put(page.getTitle(), page);
                    mAdapter.notifyItemRemoved(0);
                    if (!mRvImages.isAnimating()) {
                        mRvImages.smoothScrollToPosition(0);
                    }
                } else {
                    i++;
                }
            }

            Iterator<Page> it = mCachedPageList.values().iterator();
            while (it.hasNext()) {
                Page page = it.next();
                if (page.getTitle().startsWith(searchQuery)) {
                    it.remove();
                    for (i = 0; i < mPages.size(); i++) {
                        Page p = mPages.get(i);
                        if (p.getTitle().equals(page.getTitle())) {
                            mPages.remove(p);
                            mAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                    mPages.add(0, page);
                    mAdapter.notifyItemInserted(0);
                    if (!mRvImages.isAnimating()) {
                        mRvImages.smoothScrollToPosition(0);
                    }
                }
            }
        }

        if (mPages.size() == 0) {
            mTvError.setText(getResources().getString(R.string.error_no_results));
            mTvError.setVisibility(View.VISIBLE);
        } else {
            mTvError.setVisibility(View.GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (count > after) {
            operation = SUBTRACT;
        } else {
            operation = ADD;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        switch (operation) {
            case ADD:
                fetchPages(s.toString());
                break;

            case SUBTRACT:
                if (s.toString().isEmpty()) {
                    mPages.clear();
                    mCachedPageList.clear();
                    mAdapter.notifyDataSetChanged();
                } else {
                    invalidateStoredPages(s.toString().toLowerCase());
                }
                break;
        }
    }

    /***
     * Adapter for Images RecyclerView
     */
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Thumbnail thumbnail = mPages.get(position).getThumbnail();
            if (thumbnail != null) {
                holder.mImageView.setLayoutParams(new FrameLayout.LayoutParams(thumbnail.getWidth(), thumbnail.getHeight()));
                Picasso.with(mContext)
                        .load(thumbnail.getUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.mImageView);
            }
        }

        @Override
        public int getItemCount() {
            return mPages.size();
        }

        protected class MyViewHolder extends RecyclerView.ViewHolder {

            protected ImageView mImageView;
            protected CardView mCardView;

            public MyViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.page_image);
                mCardView = (CardView) view.findViewById(R.id.card);
            }
        }
    }
}
