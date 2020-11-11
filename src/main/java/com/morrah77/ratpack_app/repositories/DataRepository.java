package com.morrah77.ratpack_app.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import com.morrah77.ratpack_app.DTOs.BalanceDTO;
import com.morrah77.ratpack_app.DTOs.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Singleton
public class DataRepository {
    private static Logger logger = LoggerFactory.getLogger(DataRepository.class);
    private RedisClient client;

    // TODO find a connection pool either in Lettuce || in Ratpack
    private List<StatefulRedisConnection> connections = new ArrayList<>();

    @Inject
    public DataRepository(RedisClient client) {
        this.client = client;
    }

    // TODO use asynchronous connection with connect.async() && RedisFuture (awaitAll for List<RedisFuture> looks promisable)
    public List<TransactionDTO> transactions(String token) throws ExecutionException, InterruptedException {
        logger.debug("Getting transaction list for token {}", token);
        StatefulRedisConnection<String, String> redisConnection = client.connect();
        // TODO implement paginated read
        List<String> keys = redisConnection.sync().scan(
                new ScanArgs().match(token + "_transaction_*").limit(100)
        ).getKeys();
        ObjectMapper mapper = new ObjectMapper();
        return keys.stream()
                .map(key -> redisConnection.sync()
                        .get(key))
                .map(o -> {
                    try {
                        if (Objects.isNull(o) || o.isEmpty()) {
                            return null;
                        }
                        return (TransactionDTO)mapper.readValue(o, TransactionDTO.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    public BalanceDTO balance(String token) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        StatefulRedisConnection<String, String> redisConnection = client.connect();
        String result = (redisConnection.sync().get(token + "_balance"));
        if (Objects.isNull(result) || result.isEmpty()) {
            return null;
        }
        return (BalanceDTO)mapper.readValue(result, BalanceDTO.class);
    }

    public TransactionDTO spend(String token, TransactionDTO transactionDTO) throws JsonProcessingException {
        logger.debug("Spending for token {}: {}", token, transactionDTO.toString());
        StatefulRedisConnection<String, String> redisConnection = client.connect();
        BalanceDTO currentBalance = balance(token);

        redisConnection.sync()
                .set(token + "_transaction_" + new Date().toInstant().getNano(), new ObjectMapper().writeValueAsString(transactionDTO));
        currentBalance.setBalance(currentBalance.getBalance() - transactionDTO.amount);
        setBalance(token, currentBalance);
        return transactionDTO;
    }

    public BalanceDTO setBalance(String token, BalanceDTO balanceDTO) throws JsonProcessingException {
        StatefulRedisConnection<String, String> redisConnection = client.connect();
        redisConnection.sync().set(token + "_balance", new ObjectMapper().writeValueAsString(balanceDTO));
        return balanceDTO;
    }

    public List<BalanceDTO> allBalances() throws ExecutionException, InterruptedException {
        StatefulRedisConnection<String, String> redisConnection = client.connect();
        List<String> keys = redisConnection.sync().scan(
                new ScanArgs().match("")
        ).getKeys();
        return keys.stream()
                .map(key -> redisConnection.sync()
                        .get(key))
                .map(BalanceDTO::of)
                .collect(Collectors.toList());
    }

    // TODO implement balance recalculation
    private BalanceDTO recalculateBalance(String token, BalanceDTO balanceDTO) {
        logger.debug("Calling non-implemented recalculation method for {}", token);
        return balanceDTO;
    }
}
