package com.example.recorder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.security.acl.LastOwnerException;

public class playFragment extends Fragment {

    private FloatingActionButton play;
    private FloatingActionButton pause;
    private SeekBar seekBar;
    private MediaPlayer Player;
    private String fileName="hiii";
    private int lastProgress=0;
    private Handler handler = new Handler();
    Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.play_fragment,container,false);

        play=(FloatingActionButton)view.findViewById(R.id.play);
        pause=(FloatingActionButton)view.findViewById(R.id.pause);
        seekBar=(SeekBar)view.findViewById(R.id.seekBar);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Playing",Toast.LENGTH_SHORT).show();
                play.hide();
                pause.show();
                Toast.makeText(getContext(),fileName, Toast.LENGTH_LONG).show();

                Player = new MediaPlayer();
             /*   try {
                    Player.setDataSource(fileName);
                    Player.prepare();
                    Player.start();
                } catch (IOException e) {
                    Log.e("LOG_TAG", "prepare() failed");
                }*/
                seekBar.setProgress(lastProgress);
                Player.seekTo(lastProgress);
                seekBar.setMax(Player.getDuration());
                seekUpdation();


                Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        pause.hide();
                        play.show();
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
                Toast.makeText(getActivity(),"Recording",Toast.LENGTH_SHORT).show();
                pause.hide();
                play.show();
            }
        });

        return view;    }

    private void seekUpdation(){
        if(Player != null){
            int mCurrentPosition = Player.getCurrentPosition() ;
            seekBar.setProgress(mCurrentPosition);
            lastProgress = mCurrentPosition;
        }
        handler.postDelayed(runnable, 100);
    }

    protected void ReceivedData(String Name){
        fileName=Name.toString();
    }
}
