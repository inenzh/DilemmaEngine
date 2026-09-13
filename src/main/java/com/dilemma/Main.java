package com.dilemma;

import com.dilemma.server.ApiHandler;
import com.dilemma.server.StaticFileHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public final class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        Path webRoot = Paths.get(System.getProperty("web.root", "web")).toAbsolutePath().normalize();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/", new ApiHandler());
        server.createContext("/", new StaticFileHandler(webRoot));
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("Dilemma Engine running at http://localhost:" + port + " (serving " + webRoot + ")");
    }
}
