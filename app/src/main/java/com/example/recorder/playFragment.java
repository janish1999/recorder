package com.example.recorder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
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
import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class playFragment extends Fragment {

    private FloatingActionButton play;
    private FloatingActionButton pause;
    private SeekBar seekBar;
    private MediaPlayer Player;
    private String fileName="hiii";
    private int lastProgress=0;
    private Handler handler = new Handler();
    Runnable runnable;
    private int flag=0;
    private RecyclerView recyclerViewRecordings;
    private ArrayList<recording> recordingArraylist;
    private RecyclerAdapter recordingAdapter;
    private TextView textViewNoRecordings;
    private View view;
    Inflater inflate;
    ViewGroup group;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         view=inflater.inflate(R.layout.recyclerview,container,false);

         initViews();

         fetchRecordings();


        return view;    }


        public class RecyleViewHolder extends RecyclerView.ViewHolder{


            public RecyleViewHolder(LayoutInflater inflater,ViewGroup container){
                super(inflater.inflate(R.layout.recordinglist,container));

                play=(FloatingActionButton)view.findViewById(R.id.play);
                pause=(FloatingActionButton)view.findViewById(R.id.pause);
                seekBar=(SeekBar)view.findViewById(R.id.seekBar);

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(flag == 0){
                            Toast.makeText(getActivity(),"Playing",Toast.LENGTH_SHORT).show();
                            play.hide();
                            pause.show();

                            Player = new MediaPlayer();
                            try {
                                Player.setDataSource(fileName);
                                Player.prepare();
                                Player.start();
                            } catch (IOException e) {
                                Log.e("LOG_TAG", "prepare() failed");
                            }
                            seekBar.setProgress(lastProgress);
                            Player.seekTo(lastProgress);
                            seekBar.setMax(Player.getDuration());
                            seekUpdation();


                            Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    pause.hide();
                                    play.show();
                                    seekBar.setProgress(0);
                                }
                            });

                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    if( Player!=null && fromUser ){
                                        Player.seekTo(progress);
                                        lastProgress = progress;
                                    }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }
                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });

                        }
                        else{
                            handler.removeCallbacks(runnable);
                            Player.start();
                            handler.postDelayed(runnable, 1000);
                        }}
                });



                runnable = new Runnable() {
                    @Override
                    public void run() {
                        seekUpdation();
                    }
                };


                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(),"Paused",Toast.LENGTH_SHORT).show();
                        handler.removeCallbacks(runnable);
                        Player.pause();
                        pause.hide();
                        play.show();
                    }
                });

            }
        }
        public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyleViewHolder>{

            @NonNull
            @Override
            public RecyleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
                return new RecyleViewHolder(layoutInflater,viewGroup);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyleViewHolder recyleViewHolder, int i) {

            }

            @Override
            public int getItemCount() {
                return recordingArraylist.size();
            }
        }

    private void seekUpdation(){
        if(Player != null){
            int mCurrentPosition = Player.getCurrentPosition() ;
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        handler.postDelayed(runnable, 100);
    }

    protected void ReceivedData(String Name){
        fileName=Name;
        Toast.makeText(getContext(),Name,Toast.LENGTH_LONG).show();
    }

    public void initViews() {
        recordingArraylist = new ArrayList<recording>();

        recyclerViewRecordings = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerViewRecordings.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        recyclerViewRecordings.setHasFixedSize(true);
        recyclerViewRecordings.setAdapter(new RecyclerViewAdapter());

        textViewNoRecordings = (TextView) view.findViewById(R.id.text);

    }

    public void fetchRecordings() {

        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/VoiceRecorder/Audios";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();

        if( files!=null ){

            for (int i = 0; i < files.length; i++) {

                Log.d("Files", "FileName:" + files[i].getName());
                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + "/VoiceRecorder/Audios/" + fileName;

                recording recording = new recording(recordingUri,fileName,false);
                recordingArraylist.add(recording);
            }

            textViewNoRecordings.setVisibility(View.GONE);
            recyclerViewRecordings.setVisibility(View.VISIBLE);
            setAdaptertoRecyclerView();

        }else{
            textViewNoRecordings.setVisibility(View.VISIBLE);
            recyclerViewRecordings.setVisibility(View.GONE);
        }

    }

    private void setAdaptertoRecyclerView() {
        recordingAdapter = new RecyclerAdapter(recordingArraylist,getContext());
        recyclerViewRecordings.setAdapter(recordingAdapter);
    }

}
