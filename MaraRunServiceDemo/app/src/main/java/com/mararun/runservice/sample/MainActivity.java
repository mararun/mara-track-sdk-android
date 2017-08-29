package com.mararun.runservice.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mararun.runservice.engine.MaraTrackerManager;
import com.mararun.runservice.util.MaraLogger;

import static com.mararun.runservice.sample.RunActivity.EXTRA_AUTO_PAUSE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.v_start).setOnClickListener(this);

        prepare();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_start:
                Intent intent = new Intent(this, RunActivity.class);
                intent.putExtra(EXTRA_AUTO_PAUSE, ((CheckBox) findViewById(R.id.isAutoPauseCB)).isChecked());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void prepare() {
        // 在使用引擎之前，需要进行初始化
        // DEMO中放在activity内进行此操作。实际使用中，建议放置在Application内，进行全局控制
        TextView tvInfo = (TextView) findViewById(R.id.tv_info);
        String keyString = "";
        keyString = keyString.concat("App MaraTrack Key: ")
                .concat(Constants.TRACK_ENGINE_KEY)
                .concat("\n\n");
        tvInfo.setText(keyString);
        tvInfo.append(getString(R.string.preparing_engine));
        MaraTrackerManager.getInstance().init(
                this,
                (result, reason) -> {
                    MaraLogger.i("track engine auth result:" + result);
                    String infoString = "";
                    infoString = infoString
                            .concat("MaraTrack auth passed: ").concat(String.valueOf(result)).concat("\n")
                            .concat("MaraTrack auth detail: ").concat(reason).concat("\n");
                    tvInfo.append(infoString);

                    findViewById(R.id.v_start).setEnabled(result);
                }
        );
    }
}
