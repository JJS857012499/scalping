package com.exp.demo.webSocket;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/{username}")
@Component
public class WebSocket
{
    private static int onlineCount;
    public static Map<String, WebSocket> clients;
    public Session session;
    public String username;
    
    @OnOpen
    public void onOpen(@PathParam("username") final String username, final Session session) throws IOException {
        this.username = username;
        this.session = session;
        addOnlineCount();
        WebSocket.clients.put(username, this);
        System.out.println("\u5df2\u8fde\u63a5");
    }
    
    @OnClose
    public void onClose() throws IOException {
        WebSocket.clients.remove(this.username);
        subOnlineCount();
    }
    
    @OnMessage
    public void onMessage(final String message) throws IOException {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject jsonTo = jsonObject.getJSONObject(message);
        if (!jsonTo.get((Object)"To").equals("All")) {
            this.sendMessageTo("\u7ed9\u4e00\u4e2a\u4eba", jsonTo.get((Object)"To").toString());
        }
        else {
            this.sendMessageAll("\u7ed9\u6240\u6709\u4eba");
        }
    }
    
    @OnError
    public void onError(final Session session, final Throwable error) {
        error.printStackTrace();
    }
    
    public void sendMessageTo(final String message, final String To) {
        for (final WebSocket item : WebSocket.clients.values()) {
            if (item.username.equals(To)) {
                item.session.getAsyncRemote().sendText(message);
            }
        }
    }
    
    public void sendMessageAll(final String message) throws IOException {
        for (final WebSocket item : WebSocket.clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }
    
    public static synchronized int getOnlineCount() {
        return WebSocket.onlineCount;
    }
    
    public static synchronized void addOnlineCount() {
        ++WebSocket.onlineCount;
    }
    
    public static synchronized void subOnlineCount() {
        --WebSocket.onlineCount;
    }
    
    public static synchronized Map<String, WebSocket> getClients() {
        return WebSocket.clients;
    }
    
    static {
        WebSocket.onlineCount = 0;
        WebSocket.clients = new ConcurrentHashMap<String, WebSocket>();
    }
}
