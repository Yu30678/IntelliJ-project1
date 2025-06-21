package util;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import java.io.IOException;

public class CORSWrapperHandler implements HttpHandler {
    private final HttpHandler wrapped;
    private final String allowOrigin;

    public CORSWrapperHandler(HttpHandler wrapped) {
        this(wrapped, "http://localhost:3000");
    }

    public CORSWrapperHandler(HttpHandler wrapped, String allowOrigin) {
        this.wrapped = wrapped;
        this.allowOrigin = allowOrigin;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "allowOrigin");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        headers.add("Access-Control-Max-Age", "3600");


        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // 預檢成功無內容
            return;
        }

        wrapped.handle(exchange);
    }
}