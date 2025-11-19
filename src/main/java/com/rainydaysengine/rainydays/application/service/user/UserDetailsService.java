package com.rainydaysengine.rainydays.application.service.user;

import com.rainydaysengine.rainydays.errors.ApplicationError;
import com.rainydaysengine.rainydays.infra.postgres.entity.UsersEntity;
import com.rainydaysengine.rainydays.infra.postgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
// Direct implements it because it conflicts the class name
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);


    private final UserRepository userRepository;

    /**
     * @param identity the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String identity) throws UsernameNotFoundException {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        UsersEntity usersEntity;

        Pattern pattern = Pattern.compile(emailRegex);

        Matcher matcherValid = pattern.matcher(identity);
        boolean isEmail = matcherValid.matches();
        if (isEmail) {
            usersEntity = userRepository.findByEmailAddress(identity);
        } else {
            usersEntity = userRepository.findByUsername(identity);
        }

        if (usersEntity == null) {
            logger.info("User#loadUserByUserName(): Cannot find user. Identity: {}", identity);
            throw ApplicationError.NotFound(identity);

        }

        return new UserPrincipal(usersEntity);
    }
}
