package org.authservice.service;

import org.authservice.entities.UserInfo;
import org.authservice.model.UserInfoDTO;
import org.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering in loadUserByUsername Method...");
        UserInfo user = userRepository.findByUsername(username);
        if(user == null){
            log.error("Username not found: {}", username);
            throw new UsernameNotFoundException("Couldn't find User..!!");
        }
        log.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExist(UserInfoDTO userInfoDTO){
        return userRepository.findByUsername(userInfoDTO.getUsername());
    }

    public Boolean signupUser(UserInfoDTO userInfoDTO){
        // ToDo add email and password validator
        //        ValidationUtil.validateUserAttributes(userInfoDTO);
        if(Objects.nonNull(checkIfUserAlreadyExist(userInfoDTO))){
            return false;
        }
        userInfoDTO.setPassword(passwordEncoder.encode(userInfoDTO.getPassword()));
        String userId = UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId, userInfoDTO.getUsername(), userInfoDTO.getPassword(),
                new HashSet<>()));
        // pushEventToQueue
        return true;
    }
}