package com.group4.projects_management_fe.core.extension;

import com.group4.projects_management_fe.core.api.base.SseClientManager;
import io.reactivex.rxjava3.core.Observable;

public class SseRxBridge {
    public static <T> Observable<T> toObservable(SseClientManager<T> manager) {
        return Observable.create(emitter -> {
            Runnable subscription = manager.subscribe(
                    data -> {
                        if (!emitter.isDisposed())
                            emitter.onNext(data);
                    },
                    error -> {
                        if (!emitter.isDisposed())
                            emitter.onError(error);
                    });
            emitter.setCancellable(subscription::run);
        });
    }
}
