package com.group4;

import com.group4.common.interfaces.HostContext;
import com.group4.common.interfaces.Plugin;
import com.group4.common.interfaces.WidgetProvider;
import org.pf4j.Extension;
import org.pf4j.ExtensionPoint;

import java.util.ArrayList;
import java.util.List;

@Extension
public class HelloWorldPlugin implements Plugin, ExtensionPoint {
    private HostContext context;

    @Override
    public void init(HostContext context) {
        this.context = context;
        System.out.println(getName() + " initialized");
    }

    @Override
    public String getName() {
        return "Hello World Plugin";
    }

    @Override
    public void start() {
        if (context != null)
            System.out.println(getName() + " started with Context");
    }

    @Override
    public void stop() {
        System.out.println(getName() + " stopped");
    }

    @Override
    public Long getId() {
        return 0L;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public List<WidgetProvider> getWidgetProviders() {
        return new ArrayList<>();
    }
}
