package com.forgerock.elasticsearch.changes;

/*
    Copyright 2015 ForgeRock AS

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/_changes")
public class WebSocket {

    private final ESLogger log = Loggers.getLogger(WebSocket.class);
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        log.info("Connected ... " + session.getId());
        this.session = session;
        ChangeRegister.registerListener(this);
    }

    public void sendMessage(String message) {
        session.getAsyncRemote().sendText(message);
    }

    public String getId() {
        return session == null ? null : session.getId();
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("Received message: "+message);
    }

    @OnClose
    public void onClose(CloseReason reason) {
        log.info("Closing websocket: "+reason);
        ChangeRegister.unregisterListener(this);
        this.session = null;
    }

    @OnError
    public void onError(Throwable t) {
        log.error("Error on websocket "+(session == null ? null : session.getId()), t);
    }


}
