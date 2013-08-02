/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.transport;

import java.net.URL;

import org.rapfx.client.protocol.Message;

/**
 * Describes means to communicate with the server.
 */
public interface Transport {

    /**
     * Sends a message to the Server and returns the response.
     * 
     * @param msg
     *            the {@link Message} to send to the server.
     * @return the {@link Message} returned by the server.
     */
    public Message post(Message msg);

    /**
     * Sends a get request to the Server and returns the response.
     * 
     * @return the {@link Message} returned by the server.
     */
    public Message get();

    /**
     * Updates the target URL with the given session ID. The format of the given id is expected to
     * be "[idstring]=[id]" where [idstring] typically is "jsessionid".
     * 
     * @param id
     *            the session identifier.
     */
    public void setSessionId(String id);

    /**
     * Calculates the context-relative URL, assuming that the application entry point is one level
     * down the context-path. For example having <code>http://machine/context/app</code> as
     * application path, and giving <code>my/path/file.png</code> as argument will yield
     * <code>http://machine/context/my/path/file.png</code> as result.
     * 
     * @param path
     *            the context relative path
     * @return the URL pointing to the requested relative resource on the server.
     */
    public URL getContextURL(String path);

    /**
     * Loads a file from the given {@link URL}.
     * 
     * @param from
     *            the {@link URL} to load the file from
     * @return the {@link RemoteFile} representing the file in memory.
     */
    public RemoteFile getFile(URL from);

    /**
     * Represents a file that was loaded from a remote server
     */
    public interface RemoteFile {

        /**
         * @return the contents of the file as raw bytes.
         */
        public byte[] getContent();

        /**
         * @return the mimetype of the file.
         */
        public String getMimeType();

        /**
         * @return the encoding the data is encoded in if applicable.
         */
        public String getCharset();

    }

}
