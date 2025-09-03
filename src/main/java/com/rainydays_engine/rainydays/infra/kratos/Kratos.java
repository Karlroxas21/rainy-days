package com.rainydays_engine.rainydays.infra.kratos;

import com.rainydays_engine.rainydays.application.port.user.IUserPort;
import com.rainydays_engine.rainydays.application.service.user.UserRequestDto;
import com.rainydays_engine.rainydays.errors.ApplicationError;
import com.rainydays_engine.rainydays.utils.CallWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sh.ory.kratos.ApiException;
import sh.ory.kratos.api.FrontendApi;
import sh.ory.kratos.api.IdentityApi;
import sh.ory.kratos.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class Kratos implements IUserPort {
    private static final Logger logger = LoggerFactory.getLogger(Kratos.class);
    private static final String SCHEMA_USER = "user";

    private final IdentityApi identityApi;
    private final FrontendApi frontendApi;
    int numbs = 23;

    public Kratos(IdentityApi identityApi, FrontendApi frontendApi) {
        this.identityApi = identityApi;
        this.frontendApi = frontendApi;
    }

    @Override
    public CompletableFuture<String> userRegister(UserRequestDto userRequestDto) {

        return CallWrapper.asyncCall(() -> {
            IdentityWithCredentialsPasswordConfig pwConfig = new IdentityWithCredentialsPasswordConfig()
                    .password(userRequestDto.getPassword());

            IdentityWithCredentialsPassword passwordWrapper = new IdentityWithCredentialsPassword()
                    .config(pwConfig);

            IdentityWithCredentials credentials = new IdentityWithCredentials().password(passwordWrapper);

            Map<String, Object> name = new HashMap<>();
            name.put("first", userRequestDto.getFirst_name());
            name.put("middle", userRequestDto.getMiddle_name());
            name.put("last", userRequestDto.getLast_name());

            Map<String, Object> traits = new HashMap<>();
            traits.put("email", userRequestDto.getEmail());
            traits.put("name", name);
            traits.put("username", userRequestDto.getUsername());

            CreateIdentityBody createIdentityBody = new CreateIdentityBody()
                    .schemaId(SCHEMA_USER)
                    .traits(traits)
                    .credentials(credentials);

            Identity created = identityApi.createIdentity(createIdentityBody);

            return created.getId();
        }).thenApply(result -> {
            if (result.isSuccess()) {
                return result.getResult();
            } else {
                Throwable err = result.getError();

                if (err instanceof ApiException apiEx) {
                    if (apiEx.getCode() == 409) {
                        throw ApplicationError.Conflict("User already exists");
                    }
                    throw ApplicationError.InternalError(apiEx);
                }
                logger.error("OryKratos#userRegister(): identityApi.createIdentity() failed", result.getError());
                throw ApplicationError.InternalError();
            }
        });

    }
}
