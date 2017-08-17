package com.mararun.runservice.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.v_start).setOnClickListener(this);

        Observable.timer(2000, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    String infoString = "";
                    infoString = infoString
                            .concat("App MaraTrack Key: " + Constants.TRACK_ENGINE_KEY).concat("\n")
                            .concat("MaraTrack Status: ").concat(MaraApplication.getTrackEngineAuthResult()).concat("\n");
                    ((TextView)findViewById(R.id.tv_info)).setText(infoString);
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_start:
                startActivity(new Intent(this, RunActivity.class));
                break;
            default:
                break;
        }
    }
}
