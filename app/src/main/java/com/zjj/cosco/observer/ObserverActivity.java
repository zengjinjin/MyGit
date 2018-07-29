package com.zjj.cosco.observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.zjj.cosco.R;
import com.zjj.cosco.eventbus.DataBean;
import com.zjj.cosco.eventbus.EventLine;
import com.zjj.cosco.eventbus.Process;

/**
 * Created by administrator on 2018/7/29.
 */

public class ObserverActivity extends Activity implements Observer{
    private TextView tv_observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observer);
        tv_observer = findViewById(R.id.tv_observer);
        ActivityObservable.getInstance().registerObserver(this);

        EventLine.getInstance().add(this);

    }

    public void jump(View view) {
        Intent intent = new Intent(this, ObservableAcitvity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityObservable.getInstance().unRegisterObserver(this);

        EventLine.getInstance().remove(this);
    }

    @Process(EventLine.SubThread)
    public void receive(DataBean dataBean){
        tv_observer.setText("在主线程MainActivity接收到了"+dataBean.data);
    }

    @Override
    public void action(Object... objects) {
        tv_observer.setText("ObservableAcitvity："+objects[0].toString());
    }
}
