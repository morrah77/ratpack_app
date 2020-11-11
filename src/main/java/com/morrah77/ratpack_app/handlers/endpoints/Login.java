package com.morrah77.ratpack_app.handlers.endpoints;

import com.morrah77.ratpack_app.services.AuthService;
import com.morrah77.ratpack_app.DTOs.AuthResponseDTO;
import com.morrah77.ratpack_app.services.BalanceService;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Login  implements Handler {
    private AuthService authService;
    private BalanceService balanceService;

    @Inject
    public Login(AuthService authService, BalanceService balanceService) {
        this.authService = authService;
        this.balanceService = balanceService;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String token = authService.generateToken();
        /*
         * Let's give users some initial balance
         * */
        // TODO get rid of balance initiation when another mechanism implemented
        balanceService.setInitialBalance(token);
        ctx.render(Jackson.json(new AuthResponseDTO(token)));
    }
}
