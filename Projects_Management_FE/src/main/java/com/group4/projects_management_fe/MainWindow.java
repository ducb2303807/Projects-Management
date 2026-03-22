package com.group4.projects_management_fe;

import com.group4.common.interfaces.HostContext;
import com.group4.projects_management_fe.core.api.base.AbstractSseManager;
import com.group4.projects_management_fe.core.api.config.ApiConfig;
import com.group4.projects_management_fe.core.exception.GlobalExceptionHandler;
import com.group4.projects_management_fe.core.navigation.AppStageManager;
import com.group4.projects_management_fe.core.plugin.HostContextRemoteImpl;
import com.group4.projects_management_fe.core.plugin.Pf4jLoader;
import com.group4.projects_management_fe.core.plugin.PluginLoader;
import com.group4.projects_management_fe.core.ui.JavaFxErrorNotifier;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {
    private PluginLoader pluginLoader;
    private final HostContext hostContext = new HostContextRemoteImpl();

    @Override
    public void start(Stage stage) throws IOException {
        // plugin
        pluginLoader = new Pf4jLoader(hostContext);
        pluginLoader.loadPlugins();

        // stage manager init
        AppStageManager.getInstance().setStage(stage);

        // exception handler
        GlobalExceptionHandler.initialize(new JavaFxErrorNotifier());
        Thread.currentThread().setUncaughtExceptionHandler((t, e) ->
                GlobalExceptionHandler.handleException(e)
        );

        AppStageManager.getInstance().navigateToLogin();
    }

    @Override
    public void stop() throws Exception {
        if (pluginLoader != null) {
            pluginLoader.unloadPlugins();
        }

        AbstractSseManager.disconnectAll();
        ApiConfig.shutdown();
        Schedulers.shutdown();
        super.stop();
    }
}
