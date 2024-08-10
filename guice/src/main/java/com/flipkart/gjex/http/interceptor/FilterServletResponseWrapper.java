/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.http.interceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A wrapper for HttpServletResponse that captures the output stream and writer.
 */
public class FilterServletResponseWrapper extends HttpServletResponseWrapper {

    private final ServletOutputStreamWrapper stream = new ServletOutputStreamWrapper();
    private PrintWriter pw;

    /**
     * Constructs a response object wrapping the given response.
     *
     * @param response the HttpServletResponse to be wrapped
     * @throws IllegalArgumentException if the response is null
     */
    public FilterServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    /**
     * Returns the ServletOutputStream for this response.
     *
     * @return the ServletOutputStream
     * @throws IOException if an I/O error occurs
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (pw != null) {
            pw.flush();
        }
        return stream;
    }

    /**
     * Returns a PrintWriter for this response.
     *
     * @return the PrintWriter
     * @throws IOException if an I/O error occurs
     */
    public PrintWriter getWriter() throws IOException {
        pw = new PrintWriter(stream);
        return pw;
    }

    /**
     * Returns the captured bytes from the output stream.
     *
     * @return a byte array containing the captured bytes
     */
    public byte[] getWrapperBytes() {
        return stream.getBytes();
    }

    /**
     * A wrapper for ServletOutputStream that captures written bytes.
     */
    static class ServletOutputStreamWrapper extends ServletOutputStream {

        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        /**
         * Writes the specified byte to this output stream.
         *
         * @param b the byte to be written
         * @throws IOException if an I/O error occurs
         */
        public void write(int b) throws IOException {
            out.write(b);
        }

        /**
         * Returns the captured bytes from the output stream.
         *
         * @return a byte array containing the captured bytes
         */
        public byte[] getBytes() {
            return out.toByteArray();
        }

        /**
         * Indicates whether this output stream is ready to be written to.
         *
         * @return true if the output stream is ready, false otherwise
         */
        @Override
        public boolean isReady() {
            return true;
        }

        /**
         * Sets the WriteListener for this output stream.
         *
         * @param writeListener the WriteListener to be set
         */
        @Override
        public void setWriteListener(WriteListener writeListener) {
            try {
                writeListener.onWritePossible();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
