package com.morrah77.ratpack_app;

import com.morrah77.ratpack_app.server.MainServer;
import ratpack.server.RatpackServer;

public class App {
    public static void main(String... args) throws Exception {
        RatpackServer.start(new MainServer());
    }
}
