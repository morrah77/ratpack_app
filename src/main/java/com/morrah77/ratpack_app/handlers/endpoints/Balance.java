package com.morrah77.ratpack_app.handlers.endpoints;

import com.morrah77.ratpack_app.services.BalanceService;
import com.morrah77.ratpack_app.DTOs.BalanceDTO;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Balance implements Handler {
    private final static Logger logger = LoggerFactory.getLogger(Balance.class);
    private BalanceService service;

    @Inject
    public Balance(BalanceService service) {
        this.service = service;
    }
    @Override
    public void handle(Context ctx) throws Exception {
        logger.debug("Handling balances for {}", ctx.getRequest().getUri());
        UserProfile profile = ctx.get(UserProfile.class);
        String token = (String) profile.getAttribute("token");
        logger.debug("Successfully got user profile for token {}", token);
        BalanceDTO balanceDTO = service.balance(token);
        logger.debug("Successfully got balance for token {}: {}", token, balanceDTO.toString());
        ctx.render(Jackson.json(balanceDTO));
    }
}



