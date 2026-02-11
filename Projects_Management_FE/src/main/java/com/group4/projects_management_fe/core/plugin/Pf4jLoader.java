package com.group4.projects_management_fe.core.plugin;

import com.group4.common.interfaces.HostContext;
import com.group4.common.interfaces.Plugin;
import com.group4.common.interfaces.WidgetProvider;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Pf4jLoader {
    private PluginManager pluginManager;
    private final Path officialPath = Paths.get("plugins/official");
    private final Path customPath = Paths.get("plugins/custom");
    private HostContext hostContext;

    public Pf4jLoader(HostContext hostContext) {
        this.hostContext = hostContext;

        try {
            Files.createDirectories(officialPath);
            Files.createDirectories(customPath);
        } catch (IOException e) {
            System.err.println("Không thể tạo thư mục plugin: " + e.getMessage());
        }

        this.pluginManager = new DefaultPluginManager() {
            @Override
            protected List<Path> createPluginsRoot() {
                return Arrays.asList(officialPath, customPath);
            }
        };
    }

    public void loadPlugins() {
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                pluginManager.loadPlugins();
                pluginManager.startPlugins();
                List<Plugin> plugins = pluginManager.getExtensions(Plugin.class);

                for (Plugin plugin : plugins) {
                    try {
                        plugin.init(hostContext);

                        Platform.runLater(() -> {
                            List<WidgetProvider> providers = plugin.getWidgetProviders();

                            if (providers != null)
                                for (WidgetProvider widgetProvider : providers) {
                                    if (widgetProvider != null && widgetProvider.getWidget() != null)
                                        hostContext.registerWidget(widgetProvider.getWidget());
                                }
                        });
                    } catch (Exception e) {
                        System.err.println("Lỗi khi nạp plugin " + plugin.getName() + ": " + e.getMessage());
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true); // tắt thread khi đóng app
        thread.start();
    }
}
