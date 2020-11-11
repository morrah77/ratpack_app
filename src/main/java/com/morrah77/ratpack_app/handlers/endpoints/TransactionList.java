package com.morrah77.ratpack_app.handlers.endpoints;

import com.morrah77.ratpack_app.DTOs.TransactionDTO;
import com.morrah77.ratpack_app.services.TransactionService;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.List;

@Singleton
public class TransactionList implements Handler {
    private final static Logger logger = LoggerFactory.getLogger(TransactionList.class);

    private TransactionService transactionService;

    @Inject
    public TransactionList(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String token = (String) ctx.get(UserProfile.class).getAttribute("token");
        logger.debug("Handling transactions for token {}: started at {}", token, new Date().getTime());
        List<TransactionDTO> transactionDTOList = transactionService.transactions(token);
        ctx.render(Jackson.json(transactionDTOList));
        logger.debug("Handled transactions for token {}: ended at {}", token, new Date().getTime());
    }
}
