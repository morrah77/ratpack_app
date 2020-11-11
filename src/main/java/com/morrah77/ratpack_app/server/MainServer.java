package com.morrah77.ratpack_app.server;

import io.netty.handler.codec.http.HttpResponseStatus;
import com.morrah77.ratpack_app.config.MainConfigModule;
import com.morrah77.ratpack_app.handlers.common.Authenticator;
import com.morrah77.ratpack_app.handlers.endpoints.Balance;
import com.morrah77.ratpack_app.handlers.endpoints.Login;
import com.morrah77.ratpack_app.handlers.endpoints.Transaction;
import com.morrah77.ratpack_app.handlers.endpoints.TransactionList;
import ratpack.func.Action;
import ratpack.guice.Guice;
import ratpack.server.RatpackServerSpec;

public class MainServer implements Action<RatpackServerSpec> {
    @Override
    public void execute(RatpackServerSpec server) throws Exception {
        server
                // TODO find a way to inject config module depending on profile
                .registry(Guice.registry(conf -> conf.module(MainConfigModule.class)))
                .serverConfig(MainConfigModule.serverConfig())
                .handlers(chain -> chain
                        // TODO add common headers like accept, CORS etc.
                        // TODO configure endpoints depending on properties
                        .get(ctx -> ctx.clientError(HttpResponseStatus.NOT_FOUND.code()))
                        .get("login", Login.class)
                        .all(Authenticator.class)
                        .get("balance", Balance.class)
                        .get("transactions", TransactionList.class)
                        .post("spend", Transaction.class)

                );
    }
}
