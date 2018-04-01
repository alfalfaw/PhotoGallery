package com.limuyle.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by limuyle on 2018/4/1.
 */

public class PhotoGalleryFragment extends Fragment{
    private static final String TAG="PhotoGalleryFragment";
    private RecyclerView rv;
    private List<GalleryItem> mItems=new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    public static PhotoGalleryFragment newInstance(){

        return new PhotoGalleryFragment();
    }
    @Override
    public void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
        Handler responseHandler=new Handler();
        mThumbnailDownloader=new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownload(PhotoHolder holder, Bitmap bitmap) {
                Drawable drawable=new BitmapDrawable(getResources(),bitmap);
                holder.bindDrawable(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG,"Background thread started");
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.fragment_photo_gallery,container,false);
        rv=view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getActivity(),3));
        setupAdapter();
        return view;
    }
    @Override
    public void onDestroyView() {

        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG,"Background thread destroyed");
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>>{
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {

            return new FlickrFetchr().fetchItems();
        }
        @Override
        protected void onPostExecute(List<GalleryItem> items){
            mItems=items;
            setupAdapter();
        }
    }

    private void setupAdapter() {
        if(isAdded()){
            rv.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> items) {
            mGalleryItems=items;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           LayoutInflater inflater=LayoutInflater.from(getActivity());
           View view=inflater.inflate(R.layout.list_item_gallery,parent,false);


            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem=mGalleryItems.get(position);

            Drawable placeHolder=getResources().getDrawable(R.drawable.ic_launcher_foreground);
            holder.bindDrawable(placeHolder);

            mThumbnailDownloader.queueThumbnail(holder,galleryItem.getUrl());

        }

        @Override
        public int getItemCount() {
            return mGalleryItems!=null?mGalleryItems.size():0;
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView=itemView.findViewById(R.id.item_image_view);
        }


        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }
}
