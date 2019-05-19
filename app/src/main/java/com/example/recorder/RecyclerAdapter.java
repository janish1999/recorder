package com.example.recorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<recording> recordingArrayList;
    private Context context;
    private MediaPlayer Player;
    private boolean isPlaying = false;
    private String recordingUri;
    private int last_index = -1;


    public RecyclerAdapter(ArrayList<recording> recordingArrayList, Context context) {
        this.recordingArrayList = recordingArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.play_fragment,viewGroup,false);

        return  new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, int i) {

        recording recording = recordingArrayList.get(i);
        holder.textView.setText(recording.getFileName());


        if( recording.isPlaying() ){
            holder.play.hide();
            holder.pause.show();
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.VISIBLE);
            holder.seekUpdation(holder);
        }else{
            holder.pause.hide();
            holder.play.show();
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder();
            }
        });


    }

    private void holder() {

        
    }

    @Override
    public int getItemCount() {
        return recordingArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SeekBar seekBar;
        TextView textView;
        private String recordingUri;
        private int lastProgress = 0;
        private Handler mHandler = new Handler();
        ViewHolder holder;
        private FloatingActionButton play,pause;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);

        }
        private void markAllPaused() {
            for( int i=0; i < recordingArrayList.size(); i++ ){
                recordingArrayList.get(i).setPlaying(false);
                recordingArrayList.set(i,recordingArrayList.get(i));
            }
            notifyDataSetChanged();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                seekUpdation(holder);
            }
        };

        private void seekUpdation(ViewHolder holder) {
            this.holder = holder;
            if(Player != null){
                int mCurrentPosition = Player.getCurrentPosition() ;
                holder.seekBar.setMax(Player.getDuration());
                holder.seekBar.setProgress(mCurrentPosition);
                lastProgress = mCurrentPosition;
            }
            mHandler.postDelayed(runnable, 100);
        }

        private void stopPlaying() {
            try{
                Player.release();
            }catch (Exception e){
                e.printStackTrace();
            }
            Player = null;
        }

        private void startPlaying(final recording audio, final int position) {
            Player = new MediaPlayer();
            try {
                Player.setDataSource(recordingUri);
                Player.prepare();
                Player.start();
            } catch (IOException e) {
                Log.e("LOG_TAG", "prepare() failed");
            }
            //showing the pause button
            seekBar.setMax(Player.getDuration());
            isPlaying = true;

            Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    audio.setPlaying(false);
                    notifyItemChanged(position);
                }
            });
        }

        void holder(){

        int position = getAdapterPosition();
        recording recording = recordingArrayList.get(position);

        recordingUri = recording.getUri();

        if( isPlaying ){
            stopPlaying();
            if( position == last_index ){
                recording.setPlaying(false);
                stopPlaying();
                notifyItemChanged(position);
            }else{
                markAllPaused();
                recording.setPlaying(true);
                notifyItemChanged(position);
                startPlaying(recording,position);
                last_index = position;
            }

        }else {

            startPlaying(recording, position);
            recording.setPlaying(true);
            seekBar.setMax(Player.getDuration());
            Log.d("isPlayin", "False");
            Log.d("isPlayin", "False");

            notifyItemChanged(position);
            last_index = position;
        }
        }

    }}
