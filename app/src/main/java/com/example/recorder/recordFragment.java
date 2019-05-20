package com.example.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class recordFragment extends Fragment {

    private MediaRecorder myAudioRecorder;
    private String output = null;
    private Chronometer chronometer;
    private String fileName;
    SendfileName SF;
    private RecyclerView recyclerViewRecordings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.record_fragment,container,false);

        final FloatingActionButton record = (FloatingActionButton) view.findViewById(R.id.record);
        final FloatingActionButton stop=(FloatingActionButton) view.findViewById(R.id.stop);

        chronometer = (Chronometer) view.findViewById(R.id. chronometerTimer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Recording",Toast.LENGTH_SHORT).show();

                myAudioRecorder = new MediaRecorder();

                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                File root = android.os.Environment.getExternalStorageDirectory();
                File file = new File(root.getAbsolutePath() + "/VoiceRecorder/Audios");
                if (!file.exists()) {
                    file.mkdirs();
                }

                fileName =  root.getAbsolutePath() + "/VoiceRecorder/Audios/" +
                        String.valueOf(System.currentTimeMillis() + ".mp3");
                Log.d("filename",fileName);

                myAudioRecorder.setOutputFile(fileName);

                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                Toast.makeText(getContext(),fileName,Toast.LENGTH_LONG).show();

                try{
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                }
                catch (IllegalStateException e){
                    e.printStackTrace();
                }
                catch (IOException e){
                    e.printStackTrace();
                }


                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

                stop.setEnabled(true);
                record.setEnabled(false);

                record.hide();
                stop.show();


            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Finished Recording",Toast.LENGTH_SHORT).show();
                try{
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
                myAudioRecorder = null;

                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());

                record.setEnabled(true);
                stop.setEnabled(false);
                stop.hide();
                record.show();
                SF.sendData(fileName);
                try{
                FragmentManager fm = getFragmentManager();
                playFragment fragm = (playFragment) fm.findFragmentById(R.id.container);
                Toast.makeText(getContext(),"Initializing",Toast.LENGTH_LONG).show();
                fragm.initViews();
                fragm.fetchRecordings();}catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

     /*   Bundle args = new Bundle();
        args.putString("filename", "hollli");
        playFragment playFragment = new playFragment ();
        playFragment.setArguments(args);

        /*try {
            getFragmentManager().beginTransaction().add(R.id.container, playFragment).commit();
        }catch (NullPointerException e){
            e.printStackTrace();
        }*/

        return view;
    }

    interface SendfileName {
        void sendData(String name);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SF = (SendfileName) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
