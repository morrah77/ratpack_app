package com.morrah77.ratpack_app.handlers.endpoints;

import com.morrah77.ratpack_app.DTOs.TransactionDTO;
import com.morrah77.ratpack_app.services.TransactionService;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class Transaction implements Handler {
    private final static Logger logger = LoggerFactory.getLogger(Transaction.class);

    private TransactionService transactionService;

    @Inject
    public Transaction(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @Override
    public void handle(Context ctx) throws Exception {
        String token = (String) ctx.get(UserProfile.class).getAttribute("token");
        logger.debug("Handling spend for token {}: started at {}", token, new Date().getTime());
        Promise<TransactionDTO> transactionDTOPromise = ctx.parse(Jackson.fromJson(TransactionDTO.class));
        transactionDTOPromise
                .then(transactionDTO -> {
                    TransactionDTO result = transactionService.spend(token, transactionDTO);
                    ctx.render(Jackson.json(transactionDTO));
                    logger.debug("Handled spend for token {}: ended at {}", token, new Date().getTime());
                });
    }
}
