package com.scientificrat.game.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleHttpServer {

    private final int port;
    private final HttpServer httpServer;
    private ConcurrentHashMap<String, OnGetRequestListener> onGetRequestListeners = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, OnPostRequestListener> onPostRequestListeners = new ConcurrentHashMap<>();

    public SimpleHttpServer(int port) throws IOException {
        this.port = port;
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(null);  // use default
        httpServer.createContext("/", httpExchange -> {
            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();
            String protocol = httpExchange.getProtocol();
            OutputStream responseBody = httpExchange.getResponseBody();
            InputStream requestBody = httpExchange.getRequestBody();
            httpExchange.getResponseHeaders().add("Server", "SimpleRat");
            // log
            System.out.println("[REQUEST] " + protocol + " " + method + " " + path);
            int responseCode = 200;
            byte[] responseBytes = null;
            boolean methodDefined = true;
            try {
                switch (method) {
                    case "GET": {
                        OnGetRequestListener listener = onGetRequestListeners.getOrDefault(path, null);
                        if (listener != null) {
                            responseBytes = listener.doGet(requestBody).getBytes();
                        } else {
                            methodDefined = false;
                        }
                        break;
                    }
                    case "POST": {
                        OnPostRequestListener listener = onPostRequestListeners.getOrDefault(path, null);
                        if (listener != null) {
                            responseBytes = listener.doPost(requestBody).getBytes();
                        } else {
                            methodDefined = false;
                        }
                        break;
                    }
                    default:
                        // this simple server just support GET and POST method
                        methodDefined = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                send500(httpExchange, e);
                return;
            }
            if (methodDefined) {
                sendResponse(httpExchange, responseCode, responseBytes);
            } else {
                send404(httpExchange);
            }
        });
    }

    private void send404(HttpExchange httpExchange) throws IOException {
        String msg = "<html><h1>404 NOT FOUND</h1></html>";
        sendResponse(httpExchange, 404, msg.getBytes());
    }

    private void send500(HttpExchange httpExchange, Exception e) throws IOException {
        String msg = "<html><h1>500 server error</h1><p>" + e + "</p></html>";
        sendResponse(httpExchange, 500, msg.getBytes());
    }

    private void sendResponse(HttpExchange httpExchange, int code, byte[] msgBytes) throws IOException {
        httpExchange.sendResponseHeaders(code, msgBytes.length);
        httpExchange.getResponseBody().write(msgBytes);
        httpExchange.getResponseBody().flush();
        httpExchange.close();
    }


    public void start() {
        httpServer.start();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        System.out.println("Server start on port " + this.port + " [" + simpleDateFormat.format(new Date()) + "] \n");
    }

    public SimpleHttpServer get(String path, OnGetRequestListener listener) {
        onGetRequestListeners.put(path, listener);
        return this;
    }

    public SimpleHttpServer post(String path, OnPostRequestListener listener) {
        onPostRequestListeners.put(path, listener);
        return this;
    }


    public interface OnGetRequestListener {
        String doGet(InputStream body) throws Exception;
    }

    public interface OnPostRequestListener {
        String doPost(InputStream body) throws Exception;
    }
}
