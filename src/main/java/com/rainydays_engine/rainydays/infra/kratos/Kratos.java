package com.rainydays_engine.rainydays.infra.kratos;

import com.rainydays_engine.rainydays.domain.port.auth.Session;
import com.rainydays_engine.rainydays.domain.port.user.IUserPort;
import com.rainydays_engine.rainydays.domain.service.user.UserRequestDto;
import com.rainydays_engine.rainydays.errors.ApplicationError;
import com.rainydays_engine.rainydays.utils.CallResult;
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
import java.util.Optional;
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

    @Override
    public Session userLogin(String identifier, String password) {
        CallResult<LoginFlow> loginFlow = CallWrapper.syncCall(() ->
                this.frontendApi.createNativeLoginFlow(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)
        );

        if (loginFlow.isFailure()) {
            logger.error("OryKratos#userLogin(): frontendApi.createNativeLoginFlow() failed", loginFlow.getError());
            throw ApplicationError.InternalError(loginFlow.getError());
        }

        UpdateLoginFlowWithPasswordMethod updateLoginFlowWithPasswordMethod = new UpdateLoginFlowWithPasswordMethod()
                .method("password")
                .password(password)
                .identifier(identifier);

        UpdateLoginFlowBody body = new UpdateLoginFlowBody(updateLoginFlowWithPasswordMethod);

        CallResult<SuccessfulNativeLogin> updateLoginFlow = CallWrapper.syncCall(() ->
                this.frontendApi.updateLoginFlow(loginFlow.getResult().getId(), body, null, null)
        );

        if(updateLoginFlow.isFailure()) {
            logger.error("OryKratos#userLogin(): frontendApi.updateLoginFlow() failed", updateLoginFlow.getError());
            throw ApplicationError.InternalError(updateLoginFlow.getError());
        }

        SuccessfulNativeLogin loginResult = updateLoginFlow.getResult();

        // Fetch full identity traits
        CallResult<sh.ory.kratos.model.Session> sessionResult = CallWrapper.syncCall(() ->
                this.frontendApi.toSession(loginResult.getSessionToken(), null, null)
        );

        if(sessionResult.isFailure()) {
            logger.error("OryKratos#userLogin(): sessionResult() failed", sessionResult.getError());
            throw ApplicationError.InternalError(sessionResult.getError());
        }

        sh.ory.kratos.model.Session userSession = sessionResult.getResult();
        Identity identity = userSession.getIdentity();

        // Extract traits as a Map<String, Object>
        Map<String, Object> traits = (Map<String, Object>) identity.getTraits();

        Session session = new Session(
                loginResult.getSession().getId(),
                loginResult.getSessionToken(),
                loginResult.getSession().getExpiresAt(),
                loginResult.getSession().getDevices(),
                null,
                traits
                );

        return session;
    }

    @Override
    public Session getSessionFromToken(String sessionToken) {

        CallResult<sh.ory.kratos.model.Session> session = CallWrapper.syncCall(() ->
                this.frontendApi.toSession(sessionToken, null, null));

        if(session.isFailure()){
            logger.error("OryKratos#getSessionFromToken(): toSession() failed", session.getError());
            throw ApplicationError.InternalError(session.getError());
        }

        Object toTraits = session.getResult().getIdentity().getTraits();
        logger.info("TO TRAITS: {}", toTraits);

        sh.ory.kratos.model.Session userSession = session.getResult();
        Identity identity = userSession.getIdentity();

        // Extract traits as a Map<String, Object>
        Map<String, Object> traits = (Map<String, Object>) identity.getTraits();

        return new Session(
                session.getResult().getId(),
                sessionToken,
                session.getResult().getExpiresAt(),
                session.getResult().getDevices(),
                Optional.ofNullable(session.getResult().getIdentity()),
                traits
        );
    }

    @Override
    public void resetPassword(String id, String password) {
        CallResult<Identity> userIdentity = CallWrapper.syncCall(() ->
                this.identityApi.getIdentity(id, null));

        if(userIdentity.isFailure()) {
            logger.error("OryKratos#resetPassword(): this.identityApi.getIdentity() failed", userIdentity.getError());
            throw ApplicationError.InternalError(userIdentity.getError());
        }

        Identity identity = userIdentity.getResult();

        // Build password credential config
        IdentityWithCredentialsPasswordConfig identityWithCredentialsPasswordConfig = new IdentityWithCredentialsPasswordConfig()
                .password(password);

        // Wrap credentials
        IdentityWithCredentialsPassword passwordWrapper = new IdentityWithCredentialsPassword()
                .config(identityWithCredentialsPasswordConfig);

        IdentityWithCredentials identityWithCredentials = new IdentityWithCredentials()
                .password(passwordWrapper);

        // Build update body
        UpdateIdentityBody updateIdentityBody = new UpdateIdentityBody()
                .schemaId(identity.getSchemaId())
                .traits(identity.getTraits())
                .credentials(identityWithCredentials);


        CallResult<Identity> updateIdentity = CallWrapper.syncCall(() ->
                this.identityApi.updateIdentity(id, updateIdentityBody));



        if(updateIdentity.isFailure()){
            logger.error("OryKratos#resetPassword(): this.identityApi.udpateIdentity() failed", updateIdentity.getError());
            if(updateIdentity.getError() instanceof ApiException) {
                if(updateIdentity.getError().getMessage().equals("Not Found")){
                    logger.warn("OryKratos#resetPassword(): Not Found", updateIdentity.getError());
                    throw ApplicationError.NotFound(id);
                }
                throw ApplicationError.InternalError(updateIdentity.getError());
            }
            throw ApplicationError.InternalError(updateIdentity.getError());
        }

        logger.info("User {} successfully reset password ", id);

    }
}
