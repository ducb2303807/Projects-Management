package com.group4.projects_management.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ServerInfoLogger {

    private static final Logger logger = LoggerFactory.getLogger(ServerInfoLogger.class);

    @Autowired
    private Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void logServerInfo() {
        String port = env.getProperty("server.port");
        String address = env.getProperty("server.address", "localhost");

        logger.info("Server is READY on address {} port {}", address, port);
    }
}
