package com.zjj.cosco.observer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zjj.cosco.R;
import com.zjj.cosco.eventbus.DataBean;
import com.zjj.cosco.eventbus.EventLine;

/**
 * Created by administrator on 2018/7/29.
 */

public class ObservableAcitvity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observable);
    }

    public void notify(View view) {
        ActivityObservable.getInstance().notifyObservers(0, "ObserverActivity，你该更新了");

        DataBean dataBean = new DataBean();
        dataBean.data = "来自ThreeActivity的消息";
        EventLine.getInstance().postData(dataBean);
        finish();
    }
}
