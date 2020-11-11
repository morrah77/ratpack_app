package com.morrah77.ratpack_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.morrah77.ratpack_app.repositories.DataRepository;
import com.morrah77.ratpack_app.DTOs.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Singleton
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private DataRepository repository;

    @Inject
    public TransactionService(DataRepository repository) {
        this.repository = repository;
    }

    public TransactionDTO spend(String token, TransactionDTO transaction) throws JsonProcessingException, ExecutionException, InterruptedException {
        logger.debug("Spendings for token {}: started at {}", token, new Date().getTime());
        TransactionDTO transactionDTO = repository.spend(token, transaction);
        logger.debug("Successfully spent for token {}:: ended at {}, got {}", token, new Date().getTime(), transaction);
        return transactionDTO;
    }

    public List<TransactionDTO> transactions(String token) throws JsonProcessingException, ExecutionException, InterruptedException {
        logger.debug("Fetching transactions for token {}: started at {}", token, new Date().getTime());
        List<TransactionDTO> transactionDTOs = repository.transactions(token);
        logger.debug("Successfully fetchied transactions for token {}: ended at {}, got {}",
                token, new Date().getTime(), transactionDTOs);
        return transactionDTOs;
    }
}
