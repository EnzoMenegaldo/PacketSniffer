package com.packetsniffer.emenegal.packetsniffer.util;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HTTPUtil {

    /**
     * Parses headers from the given stream.
     * @param is
     * @param charset
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public static List<Header> parseHeaders(InputStream is, String charset) throws IOException, HttpException {
        ArrayList<Header> headers = new ArrayList<>();
        String name = null;
        StringBuffer value = null;
        for (; ;) {
            String line = HttpParser.readLine(is, charset);
            if ((line == null) || (line.trim().length() < 1)) {
                break;
            }
            int colon = line.indexOf(":");
            if(colon > 0){
                name = line.substring(0, colon).trim();
                value = new StringBuffer(line.substring(colon + 1).trim());
            }else{
                name = "Request";
                value = new StringBuffer(line.trim());
            }

            headers.add(new Header(name, value.toString()));
        }

        return headers;
    }
}
