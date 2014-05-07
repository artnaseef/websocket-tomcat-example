package com.artnaseef.api.impl;

import com.artnaseef.api.AgentWebsocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by art on 5/6/14.
 */
@ServerEndpoint(value = "/ws/agent")
public class SlowEchoWebsocket implements AgentWebsocket {
    private static final Logger LOG = LoggerFactory.getLogger(SlowEchoWebsocket.class);

    @Override
    @OnOpen
    public void onWebsocketOpen(Session sess) {
        LOG.debug("websocket opened; sessId={}", sess.getId());
    }

    @Override
    @OnMessage
    public void onWebsocketMessage(final String msg, final Session sess) {
        LOG.debug("websocket inbound message on sessId={}, msg={}", sess.getId(), msg);

        Thread runAfterDelayThread =
            new Thread("runAfterDelayForSess" + sess.getId()) {
                public void run () {
                    try {
                        long  delay = 1000;
                        String name = msg;
                        if ( msg.matches("^[0-9]+:..*$") ) {
                            String[] parts = msg.split(":");
                            delay = Long.parseLong(parts[0]);
                            name = parts[1];
                        }

                        Thread.sleep(delay);
                        synchronized ( sess ) {
                            sess.getBasicRemote().sendText("hello " + name + "; we are a little slow");
                        }
                        LOG.debug("completed response thread for sessId={}, msg={}", sess.getId(), msg);
                    } catch ( Exception exc ) {
                        LOG.error("error on processing request for sessId={}, msg={}", sess.getId(), msg, exc);
                    }
                }
            };

        runAfterDelayThread.start();

        LOG.debug("started response thread for sessId={}, msg={}", sess.getId(), msg);
    }

    @Override
    @OnClose
    public void onWebsocketClose(Session sess, CloseReason reason) {
        LOG.debug("closed websocket session; sessId={}, reason={}", sess.getId(), reason.toString());
    }

    @Override
    @OnError
    public void onWebsocketError(Session sess, Throwable thrown) {
        LOG.debug("error on websocket session; sessId={}", sess.getId(), thrown);
    }
}
