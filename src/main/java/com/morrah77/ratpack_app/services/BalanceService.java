package com.morrah77.ratpack_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.morrah77.ratpack_app.DTOs.BalanceDTO;
import com.morrah77.ratpack_app.repositories.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public class BalanceService {
    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);
    private DataRepository repository;

    @Inject
    public BalanceService(DataRepository repository) {
        this.repository = repository;
    }

    public BalanceDTO balance(String token) throws JsonProcessingException {
        BalanceDTO balanceDTO = repository.balance(token);
        /*
        * Let's give users some initial balance
        * */
        // TODO get rid of balance initiation when another mechanism implemented
        if (Objects.isNull(balanceDTO)) {
            balanceDTO = setInitialBalance(token);
        }
        return balanceDTO;
    }

    public BalanceDTO setInitialBalance(String token) throws JsonProcessingException {
        BalanceDTO balanceDTO;
        logger.debug("Empty balance for token {}; initiating new balance...", token);
        balanceDTO = repository.setBalance(token, new BalanceDTO(12.25d, "USD"));
        logger.debug("Successfully set initial balance for toke {}: {}", token, balanceDTO.toString());
        return balanceDTO;
    }
}
