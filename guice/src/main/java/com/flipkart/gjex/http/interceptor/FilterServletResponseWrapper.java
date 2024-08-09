package com.flipkart.gjex.http.interceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FilterServletResponseWrapper extends HttpServletResponseWrapper {

    ServletOutputStreamWrapper stream = new ServletOutputStreamWrapper();

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public FilterServletResponseWrapper(HttpServletResponse request) {
        super(request);
    }

    public ServletOutputStream getOutputStream() throws IOException
    {
        return stream;
    }

    public PrintWriter getWriter() throws IOException
    {
        return new PrintWriter(stream);
    }

    public byte[] getWrapperBytes()
    {
        return stream.getBytes();
    }


    static class ServletOutputStreamWrapper extends ServletOutputStream {

        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        public void write(int b) throws IOException {
            out.write(b);
        }

        public byte[] getBytes() {
            return out.toByteArray();
        }

        @Override
        public boolean isReady() {
            return true;
        }

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
