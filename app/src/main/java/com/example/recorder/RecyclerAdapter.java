package com.example.recorder;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<recording> recordingArrayList;
    private Context context;
    private MediaPlayer Player;
    private boolean isPlaying = false;
    private String recordingUri;
    private int last_index = -1;
    private ArrayList<recording> recordingArraylist;
    private TextView textViewNoRecordings;
    private RecyclerView recyclerViewRecordings;
    private RecyclerAdapter recordingAdapter;
    private int flag=0;


    public RecyclerAdapter(ArrayList<recording> recordingArrayList, Context context) {
        this.recordingArrayList = recordingArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.recordinglist,viewGroup,false);

        return  new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, final int i) {

        recording recording = recordingArrayList.get(i);
        try {
            holder.textView.setText(recording.getFileName());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (recording.isPlaying()) {
            holder.play.hide();
            holder.pause.show();
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.VISIBLE);
            holder.seekUpdation(holder);
        } else {
            holder.pause.hide();
            holder.play.show();
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            // holder.seekBar.setVisibility(View.GONE);
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, "Helloooo", Toast.LENGTH_LONG).show();
              //  if (flag == 0) {
                //    flag=1;
                 //   markAllPaused();
                    Toast.makeText(context, "Playing", Toast.LENGTH_SHORT).show();
                    holder.play.hide();
                    holder.pause.show();

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);


                    Player = new MediaPlayer();
                    try {
                        Toast.makeText(context, "Playing", Toast.LENGTH_SHORT).show();
                        recording record = recordingArrayList.get(i);
                        String filename=record.getFileName();
                        Player.setDataSource(filename);
                        Player.prepare();
                        Player.start();
                    } catch (IOException e) {
                        Log.e("LOG_TAG", "prepare() failed");
                    }


                    Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            holder.pause.hide();
                            holder.play.show();
                            //Toast.makeText(context,"Track Finished",Toast.LENGTH_LONG).show();
                        }
                    });

               // } else {
                    //Player.start();
          //      }
            }
            private void markAllPaused() {
                for( int i=0; i < recordingArrayList.size(); i++ ){
                    recordingArrayList.get(i).setPlaying(false);
                    recordingArrayList.set(i,recordingArrayList.get(i));
                }
                notifyDataSetChanged();
            }

        });

        holder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Paused",Toast.LENGTH_SHORT).show();
                Player.pause();
                holder.pause.hide();
                holder.play.show();
            }
        });
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

            textView =(TextView)itemView.findViewById(R.id.textview);
            play=(FloatingActionButton)itemView.findViewById(R.id.play);
            pause=(FloatingActionButton)itemView.findViewById(R.id.pause);
            seekBar=(SeekBar)itemView.findViewById(R.id.seekBar);

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
                    flag=0;
                    audio.setPlaying(false);
                    notifyItemChanged(position);
                    holder.pause.hide();
                    holder.play.show();
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

    }

}
