/*
 * Copyright 2026 by JWizard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.jwizard.jwl.netclient.websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.DataType;
import xyz.jwizard.jwl.common.util.StringUtil;
import xyz.jwizard.jwl.net.NetworkUtil;
import xyz.jwizard.jwl.netclient.TestConstants;

public class WebsocketServerIntegrationMock extends WebSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebsocketServerIntegrationMock.class);

    public WebsocketServerIntegrationMock(int port) {
        super(new InetSocketAddress(port));
    }

    public WebSocket getSession(DataType dataType) {
        LOG.debug("Searching for session with data type: {}", dataType.getCode());
        return getConnections().stream()
            .filter(c -> StringUtil.toLowerCase(dataType.getCode()).equals(c.getAttachment()))
            .findFirst().orElseThrow(() -> {
                LOG.error("Session not found for data type: {}", dataType.getCode());
                return new IllegalStateException("Session not found");
            });
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        final String resource = handshake.getResourceDescriptor();
        LOG.debug("New WebSocket connection attempt: {}", resource);
        final Map<String, String> params = NetworkUtil.getQueryParameters(resource);
        if (!params.containsKey(TestConstants.DATA_TYPE_QUERY_PARAM_NAME)) {
            LOG.warn("Connection rejected: missing {} parameter in query string",
                TestConstants.DATA_TYPE_QUERY_PARAM_NAME);
            throw new IllegalStateException("Missing required parameter: "
                + TestConstants.DATA_TYPE_QUERY_PARAM_NAME);
        }
        final String frame = params
            .getOrDefault(TestConstants.DATA_TYPE_QUERY_PARAM_NAME, DataType.BINARY.getCode());
        conn.setAttachment(StringUtil.toLowerCase(frame));
        LOG.info("Session {} connected and tagged with frame: {}", conn.getRemoteSocketAddress(),
            StringUtil.toLowerCase(frame));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LOG.debug("Connection closed: {} (code: {}, remote: {})", conn.getRemoteSocketAddress(),
            code, remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LOG.trace("Received text message from {}: {}", conn.getRemoteSocketAddress(), message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        LOG.trace("Received binary message from {}: {} bytes", conn.getRemoteSocketAddress(),
            message.remaining());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOG.error("Error occurred on connection {}: {}",
            conn != null ? conn.getRemoteSocketAddress() : "unknown", ex.getMessage(), ex);
    }

    @Override
    public void onStart() {
        LOG.info("WebSocket mock server started on port {}", getPort());
    }
}
