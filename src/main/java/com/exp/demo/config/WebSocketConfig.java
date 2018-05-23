package com.exp.demo.config;

import org.springframework.web.socket.server.standard.*;
import org.springframework.context.annotation.*;

@Configuration
public class WebSocketConfig
{
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
