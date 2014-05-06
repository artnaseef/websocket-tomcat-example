package com.artnaseef.api;

import javax.websocket.CloseReason;
import javax.websocket.Session;

/**
 * Created by art on 5/6/14.
 */
public interface AgentWebsocket {
    void    onWebsocketOpen(Session sess);

    void    onWebsocketMessage(String msg, Session sess);

    void    onWebsocketClose(Session sess, CloseReason reason);

    void    onWebsocketError (Session sess, Throwable thrown);
}
