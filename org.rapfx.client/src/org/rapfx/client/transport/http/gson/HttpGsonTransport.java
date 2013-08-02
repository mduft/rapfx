/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.transport.http.gson;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.protocol.Message;
import org.rapfx.client.protocol.types.Header;
import org.rapfx.client.protocol.types.Operation;
import org.rapfx.client.transport.Transport;
import org.rapfx.client.transport.http.HttpRequest;
import org.rapfx.client.transport.http.gson.serialization.MessageMarshaller;
import org.rapfx.client.transport.http.gson.serialization.OperationMarshaller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Implements a {@link Transport} for RAP server communication based on HTTP and GSON
 */
public class HttpGsonTransport implements Transport {

    private static final Log log = LogFactory.getLog(HttpGsonTransport.class);
    private static final int TRANSPORT_READ_TIMEOUT = 120000;
    private static final int TRANSPORT_CONNECT_TIMEOUT = 10000;
    private final URL target;
    private String sessionId;
    private String uiSessionId;
    private long requestCounter;
    private final Gson gson;
    private final String agent;

    public HttpGsonTransport(URL target, String userAgent) {
        this.target = target;
        this.agent = userAgent;

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Message.class, new MessageMarshaller());
        builder.registerTypeAdapter(Operation.class, new OperationMarshaller());

        gson = builder.create();

        log.debug("initialized " + this + " for " + target);
    }

    @Override
    public synchronized Message post(Message msg) {

        if (sessionId != null && !msg.containsHeader("rwt_initialize")) {
            // fill session ID and request counter;
            msg.addHeader(new Header("requestCounter", Long.valueOf(requestCounter)));
        }

        if (uiSessionId != null) {
            msg.addHeader(new Header("uiSessionId", uiSessionId));
        }

        HttpRequest rq = initJsonRequest(HttpRequest.post(getTargetForSession(target)))
                .contentType(HttpRequest.CONTENT_TYPE_JSON).send(gson.toJson(msg));

        return doRequest(rq);
    }

    @Override
    public synchronized Message get() {
        HttpRequest rq = initJsonRequest(HttpRequest.get(getTargetForSession(target)));
        return doRequest(rq);
    }

    /**
     * Constructs a {@link URL} that is suitable for connecting to with session information
     * attached.
     * 
     * @param original
     *            the applications target {@link URL}
     * @return the {@link URL} with session information attached
     */
    private URL getTargetForSession(URL original) {
        if (sessionId == null) {
            return original;
        }

        try {
            return new URL(original.toString() + ";jsessionid=" + sessionId);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("malformed session id!", e);
        }
    }

    /**
     * @param rq
     *            the request to send to the server
     * @return the {@link Message} the resulted from the servers response, never <code>null</code>
     */
    private Message doRequest(HttpRequest rq) {
        if (!rq.ok()) {
            throwAppropriateError(rq);
        }

        if (log.isTraceEnabled()) {
            log.trace(rq.code() + ": " + rq);
        }

        if (!rq.contentType().startsWith(HttpRequest.CONTENT_TYPE_JSON)) {
            throw new IllegalStateException("response has unsupported format " + rq.contentType());
        }

        Message msg = gson.fromJson(rq.body(), Message.class);

        for (Header hdr : msg.getHeaders()) {
            if (hdr.getName().equals("requestCounter")) {
                requestCounter = Long.valueOf(hdr.getValue().toString());
            } else if (hdr.getName().equals("uiSessionId")) {
                uiSessionId = hdr.getValue().toString();
            }
        }

        return msg;
    }

    /**
     * find the problem with the given {@link HttpRequest} and throw an {@link Exception} with a
     * message telling as precisely as possible about the "real" problem.
     * 
     * @param rq
     *            the request to analyze
     * 
     */
    private void throwAppropriateError(HttpRequest rq) {
        switch (rq.code()) {
        case 403:
            Message resp = gson.fromJson(rq.body(), Message.class);
            if (resp.containsHeader("error")) {
                String error = (String) resp.getHeaderValue("error");
                String message = (String) resp.getHeaderValue("message");

                throw new IllegalStateException(error + ": " + message);
            }
        default:
            throw new IllegalStateException("unknown error (code " + rq.code() + ": "
                    + rq.message() + ")");
        }
    }

    /**
     * Initializes the given {@link HttpRequest} with common parameters.
     * 
     * @param req
     *            the request to initialize
     * @return the initialized {@link HttpRequest}
     */
    private HttpRequest initJsonRequest(HttpRequest req) {
        return req.trustAllCerts().trustAllHosts().acceptGzipEncoding().acceptJson()
                .contentType(HttpRequest.CONTENT_TYPE_JSON, HttpRequest.CHARSET_UTF8)
                .uncompress(true).readTimeout(TRANSPORT_READ_TIMEOUT)
                .connectTimeout(TRANSPORT_CONNECT_TIMEOUT).userAgent(agent);
    }

    @Override
    public void setSessionId(String id) {
        sessionId = id.substring(id.indexOf('=') + 1);

        log.debug("updated session ID (" + sessionId + "), new target: " + target);
    }

    @Override
    public URL getContextURL(String path) {
        try {
            return target.toURI().resolve(path).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("malformed URL during context calculation", e);
        }

        return null;
    }

    @Override
    public RemoteFile getFile(URL from) {
        if (from == null) {
            return null;
        }

        HttpRequest rq = HttpRequest.get(from).acceptGzipEncoding().uncompress(true)
                .userAgent(agent).trustAllCerts().trustAllHosts();

        if (!rq.ok()) {
            throwAppropriateError(rq);
        }

        return new HttpRemoteFile(rq.contentType(), rq.charset(), rq.bytes());
    }

    /**
     * DTO for files received through HTTP get requests.
     */
    private static class HttpRemoteFile implements RemoteFile {

        private final byte[] bytes;
        private final String charset;
        private final String type;

        public HttpRemoteFile(String type, String charset, byte[] bytes) {
            this.type = type;
            this.charset = charset;
            this.bytes = bytes;
        }

        @Override
        public byte[] getContent() {
            return bytes;
        }

        @Override
        public String getMimeType() {
            return type;
        }

        @Override
        public String getCharset() {
            return charset;
        }

    }

}
