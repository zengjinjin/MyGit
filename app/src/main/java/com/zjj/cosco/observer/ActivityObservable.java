package com.zjj.cosco.observer;

/**
 * Created by administrator on 2018/7/29.
 */

public class ActivityObservable extends Observable<Observer> {
    private static ActivityObservable mActivityObservable = null;

    public static ActivityObservable getInstance() {
        if (mActivityObservable == null) {
            mActivityObservable = new ActivityObservable();
        }

        return mActivityObservable;
    }

    @Override
    public void notifyObservers(int id, Object... objects) {
        for (Observer observer : observableList){
            if (observer != null){
                observer.action(objects);
            }
        }
    }
}
