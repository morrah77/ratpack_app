package com.morrah77.ratpack_app.handlers.common;

import com.morrah77.ratpack_app.services.AuthService;
import org.pac4j.core.exception.http.ForbiddenAction;
import org.pac4j.core.profile.UserProfile;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.registry.Registry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class Authenticator implements Handler {
    private AuthService authService;

    @Inject
    public Authenticator(AuthService authService) {
        this.authService = authService;
    }
    @Override
    public void handle(Context ctx) throws Exception {
        //TODO find a way of using HeadersClient / Authenticator from pack4j; don't use abandoner retpack-pack4j.
        Optional.ofNullable(ctx.getRequest().getHeaders().get("Authorization")).ifPresentOrElse(
                header -> {
                    if (!header.startsWith("Bearer ")) {
                        ctx.error(ForbiddenAction.INSTANCE);
                    }
                    String token = header.replace("Bearer ", "");
                    if (authService.isTokenValid(token)) {
                        UserProfile profile = authService.getUserProfile(token);
                        // TODO check for several profiles storage
                        ctx.next(Registry.single(profile));
                    } else {
                        ctx.error(ForbiddenAction.INSTANCE);
                    }
                },
                () -> ctx.error(ForbiddenAction.INSTANCE)
        );
    }
}
