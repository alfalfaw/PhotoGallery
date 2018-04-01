package com.limuyle.android.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by limuyle on 2018/4/1.
 */

class FlickrFetchr {
    private static final String TAG="FlickrFetchr";
    private static final String API_KEY="4d3684989d87ceb802995d94aaecf889";
    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url=new URL(urlSpec);
        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        try{
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            InputStream inputStream=connection.getInputStream();
            if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ":with" + urlSpec);
            }
            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while ((bytesRead=inputStream.read(buffer))>0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items=new ArrayList<>();
        try{
            String url= Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method","flickr.photos.getRecent")
                    .appendQueryParameter("api_key",API_KEY)
                    .appendQueryParameter("format","json")
                    .appendQueryParameter("nojsoncallback","1")
                    .build().toString();
            String jsonString=getUrlString(url);
            Log.i(TAG,"Received json:"+jsonString);
            JSONObject jsonBody=new JSONObject(jsonString);
            parseItems(items,jsonBody);

        }catch (IOException i){
            Log.e(TAG,"Failed to fetch items.",i);
        }catch (JSONException j){
            Log.e(TAG,"Failed to parse JSON",j);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException,JSONException{
        JSONObject photosJsonObject=jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray=photosJsonObject.getJSONArray("photo");
        for(int i=0;i<photoJsonArray.length();i++){
            JSONObject photoJsonObject=photoJsonArray.getJSONObject(i);
            GalleryItem item=new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));
            if(!photoJsonObject.has("url_s")){
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);

        }
    }
}
