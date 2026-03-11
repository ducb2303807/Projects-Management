package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.SseNotificationDTO;
import com.group4.projects_management_fe.core.api.base.AbstractSseManager;
import com.group4.projects_management_fe.core.error.UnauthorizedException;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class RxSseManager extends AbstractSseManager<SseNotificationDTO> {
    private Disposable sseSubscription;

    public RxSseManager(AuthSessionProvider sessionProvider) {
        super(SseNotificationDTO.class, sessionProvider);
    }

    @Override
    public void startListening(Consumer<SseNotificationDTO> onDataReceived, Consumer<Throwable> onError, Runnable onUnauthorized) {
        stopListening();

        this.sseSubscription = this.createSseObservable()
                .subscribeOn(Schedulers.io())
                .doOnError(error -> this.notifyErrorToUI(error, onError))
                .retryWhen(buildRetryPolicy())
                .subscribe(sseNotification -> this.notifySuccessToUI(sseNotification, onDataReceived)
                        , fatalError -> handleFatalError(fatalError, onUnauthorized)
                );
    }

    @Override
    public void stopListening() {
        if (sseSubscription != null && !sseSubscription.isDisposed()) {
            sseSubscription.dispose();
            sseSubscription = null;
        }
    }

    private Observable<SseNotificationDTO> createSseObservable() {
        return Observable.<SseNotificationDTO>create(emitter -> {
            Request request = new Request.Builder()
                    .url(this.getUrl())
                    .build();

            EventSourceListener listener = createSseListener(emitter);

            EventSource eventSource = EventSources
                    .createFactory(client)
                    .newEventSource(request, listener);

            emitter.setCancellable(eventSource::cancel);
        });
    }

    private EventSourceListener createSseListener(ObservableEmitter<SseNotificationDTO> emitter) {
        return new EventSourceListener() {
            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                System.out.println("Kết nối SSE thành công!");
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
                if ("INIT".equals(type)) {
                    System.out.println("Server xác nhận: " + data);
                    return;
                }

                try {
                    var dto = parseData(data);
                    emitter.onNext(dto);
                } catch (Exception e) {
                    System.err.println("Lỗi parse JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
                emitter.onError(new RuntimeException("Mất kết nối SSE", t));
            }
        };
    }

    private Function<Observable<Throwable>, ObservableSource<?>> buildRetryPolicy() {
        return errors -> errors
                .flatMap(error -> {
                    if (error instanceof UnauthorizedException) {
                        return Observable.error(error);
                    }
                    return Observable.just(error);
                })
                .scan(0, (count, err) -> ++count)
                .flatMap(retryCount -> {
                    int delay = (int) Math.min(Math.pow(2, retryCount), MAX_RETRY_DELAY_MILLISECOND);
                    return Observable.timer(delay, TimeUnit.MILLISECONDS);
                });
    }

    private void notifyErrorToUI(Throwable error, Consumer<Throwable> onError) {
        if (onError != null && !(error instanceof UnauthorizedException)) {
            onError.accept(error);
        }
    }

    private void notifySuccessToUI(SseNotificationDTO notification, Consumer<SseNotificationDTO> onDataReceived) {
        if (onDataReceived != null) {
            onDataReceived.accept(notification);
        }
    }

    private void handleFatalError(Throwable fatalError, Runnable onUnauthorized) {
        if (fatalError instanceof UnauthorizedException) {
            System.out.println("Token hết hạn, đá user ra trang Login.");
            if (onUnauthorized != null) onUnauthorized.run();
        } else {
            System.err.println("Mất kết nối hoàn toàn: " + fatalError.getMessage());
        }
    }
}
