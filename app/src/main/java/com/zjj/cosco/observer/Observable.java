package com.zjj.cosco.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 2018/7/29.
 */

public abstract class Observable<T> {
    public List<T> observableList = new ArrayList<>();

    public void registerObserver(T t){
        checkNull(t);
        observableList.add(t);
    }

    public void unRegisterObserver(T t){
        checkNull(t);
        observableList.remove(t);
    }

    private void checkNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
    }

    public abstract void notifyObservers(int id, Object... objects);
}
