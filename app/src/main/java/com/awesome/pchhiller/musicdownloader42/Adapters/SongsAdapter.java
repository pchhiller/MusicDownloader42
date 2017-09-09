package com.awesome.pchhiller.musicdownloader42.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.awesome.pchhiller.musicdownloader42.MainActivity;
import com.awesome.pchhiller.musicdownloader42.R;
import com.awesome.pchhiller.musicdownloader42.SongsDataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.name;
import static android.R.attr.onClick;
import static android.content.ContentValues.TAG;
import static com.awesome.pchhiller.musicdownloader42.R.id.song_name;

/**
 * Created by Daljit on 06-07-2017.
 */

public class SongsAdapter  extends RecyclerView.Adapter<SongsAdapter.SongHolder>  {
    private List<SongsDataModel> mData;
    private Context mContext;
    public String TAG = "SongsAdapter";
    public SongsAdapter(List<SongsDataModel> list, Context context)
    {
        mData=list;
        mContext=context;
    }
    @Override
    public SongsAdapter.SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, parent, false);
        return new SongHolder(view,mContext,mData);
    }

    @Override
    public void onBindViewHolder(SongsAdapter.SongHolder holder, int position) {
        if(position==0)
            holder.bind("Download from Youtube");
        else
            holder.bind(mData.get(position).getSongName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public static class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public String TAG = "SongsAdapter";
        private Context mContext;
        private List<SongsDataModel> mData;

        private TextView songNameTextView;
        public  SongHolder(View v, Context context, List<SongsDataModel> list)
        {super(v);
            songNameTextView= (TextView) v.findViewById(song_name);
            songNameTextView.setOnClickListener(this);
            mContext=context;
            mData=list;

        }
        @Override
        public void onClick(View v) {
            int val = getAdapterPosition();
            Log.v(TAG, String.valueOf(val));
            if (val == 0)
            {
                ((MainActivity)mContext).alternateDownloadClicked(mData.get(val).getSongName());
            }
            else
            ((MainActivity)mContext).onClickCalled(mData.get(val).getSongUrl(),mData.get(val).getSongName());

        }



        public void bind(String item)
        {
            songNameTextView.setText(item);
        }


        //3

        //4
        }


}
