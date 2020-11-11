package com.morrah77.ratpack_app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import com.morrah77.ratpack_app.DTOs.AuthResponseDTO;
import com.morrah77.ratpack_app.DTOs.BalanceDTO;
import com.morrah77.ratpack_app.DTOs.TransactionDTO;
import com.morrah77.ratpack_app.config.MainConfigModule;
import com.morrah77.ratpack_app.config.StorageConfig;
import com.morrah77.ratpack_app.domain.Currency;
import com.morrah77.ratpack_app.server.MainServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ratpack.server.ServerConfig;
import ratpack.test.embed.EmbeddedApp;
import ratpack.test.http.TestHttpClient;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MainServerIntegrationTest {
    EmbeddedApp app;
    private MainServer mainServer;
    private String connectionUri;

    @BeforeEach
    void setup() {
        mainServer = new MainServer();
        ServerConfig serverConfig = MainConfigModule.serverConfig();
        connectionUri = serverConfig.get("/server/storage", StorageConfig.class).getUri();
//        RedisClient redisClient = RedisClient.create(connectionUri);
//        StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
////        redisClient.connect().sync().select(1);
//        redisConnection.close();
    }

    @AfterEach
    void tearDown() {
        app.close();
        RedisClient redisClient = RedisClient.create(connectionUri);
        StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
        redisClient.connect().sync().flushdb();
        redisConnection.close();
    }

    // TODO split common tests to several typical scenarios, add edge cases
    @Test void generalFlowShouldWork() throws Exception {
        mainServer = new MainServer();
        app = EmbeddedApp.of(mainServer);

        ServerConfig serverConfig = MainConfigModule.serverConfig();
        connectionUri = serverConfig.get("/server/storage", StorageConfig.class).getUri();

        TestHttpClient client = app.getHttpClient();
        assertEquals("Client error 404", client.getText());
        assertEquals(404, client.getResponse().getStatus().getCode());

        ObjectMapper objectMapper = new ObjectMapper();

        AuthResponseDTO authResponseDTO = objectMapper
                .readValue(app.getHttpClient().getText("/login"),
                        new TypeReference<AuthResponseDTO>() {});
        assertNotNull(authResponseDTO.token);

        BalanceDTO balanceDTO = objectMapper
                .readValue(app.getHttpClient().requestSpec(r -> r
                                .headers(h -> h.set("Authorization", "Bearer " + authResponseDTO.token)))
                                .getText("/balance"),
                        new TypeReference<BalanceDTO>() {});

        BalanceDTO expectedBalanceDTO = buildBalanceWithDefaultCurrency(12.25d);
        assertEquals(expectedBalanceDTO, balanceDTO);

        java.util.List<TransactionDTO> transactionDTOList = objectMapper
                .readValue(app.getHttpClient().requestSpec(r -> r
                                .headers(h -> h.set("Authorization", "Bearer " + authResponseDTO.token)))
                                .getText("/transactions"),
                        new TypeReference<List<TransactionDTO>>(){});
        assertEquals(0, transactionDTOList.size());


        TransactionDTO expectedTransactionDTO1 = buildTransactionWithDefaultCurrency("Transaction 1", 1.5d);
        String jsonBodyOfTransaction1 = objectMapper
                .writeValueAsString(expectedTransactionDTO1);
        TransactionDTO transactionDTO1 = objectMapper
                .readValue(app.getHttpClient()
                                .requestSpec(requestSpec -> requestSpec.
                                        headers(headers -> headers
                                                .set("Authorization", "Bearer " + authResponseDTO.token)
                                                .set("Content-type", "application/json"))
                                        .body(body -> body.text(jsonBodyOfTransaction1)))
                                .postText("/spend"),
                        new TypeReference<TransactionDTO>() {});

        assertEquals(expectedTransactionDTO1, transactionDTO1);

        TransactionDTO expectedTransactionDTO2 = buildTransactionWithDefaultCurrency("Transaction 2", 3.5d);
        String jsonBodyOfTransaction2 = objectMapper
                .writeValueAsString(expectedTransactionDTO2);
        TransactionDTO transactionDTO2 = objectMapper
                .readValue(app.getHttpClient()
                                .requestSpec(requestSpec -> requestSpec.
                                        headers(headers -> headers
                                                .set("Authorization", "Bearer " + authResponseDTO.token)
                                                .set("Content-type", "application/json"))
                                        .body(body -> body.text(jsonBodyOfTransaction2)))
                                .postText("/spend"),
                        new TypeReference<TransactionDTO>() {});
        assertEquals(expectedTransactionDTO2, transactionDTO2);

        transactionDTOList = objectMapper
                .readValue(app.getHttpClient().requestSpec(r -> r
                                .headers(h -> h
                                        .set("Authorization", "Bearer " + authResponseDTO.token)))
                                .getText("/transactions"),
                        new TypeReference<List<TransactionDTO>>(){});
        assertEquals(2, transactionDTOList.size());

        balanceDTO = objectMapper
                .readValue(app.getHttpClient().requestSpec(r -> r
                                .headers(h -> h.set("Authorization", "Bearer " + authResponseDTO.token)))
                                .getText("/balance"),
                        new TypeReference<BalanceDTO>() {});
        expectedBalanceDTO = buildBalanceWithDefaultCurrency(7.25d);
        assertEquals(expectedBalanceDTO, balanceDTO);

        app.close();
    }

    private TransactionDTO buildTransactionWithDefaultCurrency(String description, Double amount) {
        return new TransactionDTO(new Date(), description, amount, Currency.USD.name());
    }

    private BalanceDTO buildBalanceWithDefaultCurrency(Double amount) {
        return new BalanceDTO(amount, Currency.USD.name());
    }
}
