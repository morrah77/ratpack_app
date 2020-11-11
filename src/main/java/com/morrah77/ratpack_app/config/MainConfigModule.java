package com.morrah77.ratpack_app.config;

import com.google.inject.AbstractModule;
import com.morrah77.ratpack_app.handlers.endpoints.Login;
import com.morrah77.ratpack_app.handlers.endpoints.Transaction;
import com.morrah77.ratpack_app.services.AuthService;
import com.morrah77.ratpack_app.services.BalanceService;
import io.lettuce.core.RedisClient;
import com.morrah77.ratpack_app.handlers.common.Authenticator;
import com.morrah77.ratpack_app.handlers.endpoints.Balance;
import com.morrah77.ratpack_app.handlers.endpoints.TransactionList;
import com.morrah77.ratpack_app.repositories.DataRepository;
import ratpack.func.Action;
import ratpack.server.ServerConfig;

public class MainConfigModule extends AbstractModule {
    public static ServerConfig serverConfig() {
        ServerConfig serverConfig = ServerConfig
                .builder()
                .onError(Action.throwException()).yaml(ClassLoader.getSystemResource("app-config.yml"))
                .onError(Action.noop()).yaml("app-config.yml")
                .sysProps()
                .env()
                .require("/storage", StorageConfig.class)
                .build();
        return serverConfig;
    }

    @Override
    protected void configure() {

        ServerConfig serverConfig = MainConfigModule.serverConfig();
        // TODO get rid of this hack using StorageConfig from contest
        String dataStorageConnectionUri = serverConfig.get("/server/storage", StorageConfig.class).getUri();
        bind(ServerConfig.class).toInstance(serverConfig);
        bind(RedisClient.class).toInstance(RedisClient.create(dataStorageConnectionUri));
        bind(AuthService.class).toInstance(new AuthService());
        bind(Authenticator.class);
        bind(DataRepository.class);
        bind(BalanceService.class);
        bind(Login.class);
        bind(Balance.class);
        bind(TransactionList.class);
        bind(Transaction.class);
    }
}
