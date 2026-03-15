package com.group4.projects_management_fe.core.api;

import com.group4.common.dto.SseNotificationDTO;
import com.group4.projects_management_fe.core.api.base.AbstractSseManager;
import com.group4.projects_management_fe.core.exception.UnauthorizedException;
import com.group4.projects_management_fe.core.session.AuthSessionProvider;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class RxSseManager extends AbstractSseManager<SseNotificationDTO> {
    private Disposable connectionDisposable;
    private final PublishSubject<SseNotificationDTO> eventPublisher = PublishSubject.create();
    private final PublishSubject<Throwable> errorPublisher = PublishSubject.create();

    public RxSseManager(AuthSessionProvider sessionProvider) {
        super(SseNotificationDTO.class, sessionProvider);
    }

    @Override
    public synchronized void connect(Runnable onUnauthorized) {
        if (connectionDisposable != null && !connectionDisposable.isDisposed()) return;

        this.connectionDisposable = this.createSseObservable()
                .subscribeOn(Schedulers.io())
                .doOnError(error -> {
                    if (!(error instanceof UnauthorizedException)) {
                        errorPublisher.onNext(error);
                    }
                })
                .retryWhen(buildRetryPolicy())
                .subscribe(
                        eventPublisher::onNext,
                        fatalError -> {
                            if (fatalError instanceof UnauthorizedException && onUnauthorized != null) {
                                onUnauthorized.run();
                            }
                        }
                );
    }

    @Override
    public Runnable subscribe(Consumer<SseNotificationDTO> onReceive, Consumer<Throwable> onError) {
        Disposable receivedSubscription = eventPublisher.observeOn(Schedulers.computation())
                .subscribe(onReceive::accept);
        Disposable errorSubscription = errorPublisher.observeOn(Schedulers.computation())
                .subscribe(onError::accept);

        return () -> {
            receivedSubscription.dispose();
            errorSubscription.dispose();
        };
    }

    @Override
    public void disconnect() {
        if (connectionDisposable != null) connectionDisposable.dispose();
    }

    @Override
    protected void onCustomShutdown() {
        if (!eventPublisher.hasComplete()) {
            eventPublisher.onComplete();
        }
        if (!errorPublisher.hasComplete()) {
            errorPublisher.onComplete();
        }
    }

    private Observable<SseNotificationDTO> createSseObservable() {
        return Observable.create(emitter -> {
            Request request = new Request.Builder()
                    .url(this.buildUrl(this.endpoint))
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
                    var dto = parseData(data, SseNotificationDTO.class);
                    emitter.onNext(dto);
                } catch (Exception e) {
                    System.err.println("Lỗi parse JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
                Throwable parsedError = parseHttpError(response, t);
                emitter.onError(parsedError);
            }
        };
    }

    // lỗi bình thường thì retry, lỗi về token thì dừng stream luôn
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
}
