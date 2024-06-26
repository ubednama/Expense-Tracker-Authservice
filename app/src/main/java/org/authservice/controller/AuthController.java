package org.authservice.controller;

import org.authservice.entities.RefreshToken;
import org.authservice.model.UserInfoDTO;
import org.authservice.response.JwtResponseDTO;
import org.authservice.service.JwtService;
import org.authservice.service.RefreshTokenService;
import org.authservice.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/auth/v1/signup")
    public ResponseEntity<?> SignUp(@RequestBody UserInfoDTO userInfoDTO){
        System.out.println("you reached here" + userInfoDTO.getUsername());
//        logger.info("Received signup request for user: {}", userInfoDTO.getUsername());
        try{
            Boolean isSignUped = userDetailsService.signupUser(userInfoDTO);
            if(Boolean.FALSE.equals(isSignUped)){
//                logger.warn("User already exists: {}", userInfoDTO.getUsername());
                return new ResponseEntity<>("User Already Exist", HttpStatus.BAD_REQUEST);
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDTO.getUsername());
            String jwtToken = jwtService.GenerateToken(userInfoDTO.getUsername());
//            logger.info("User signed up successfully: {}", userInfoDTO.getUsername());
            return new ResponseEntity<>(JwtResponseDTO.builder().accessToken(jwtToken).
                    token(refreshToken.getToken()).build(), HttpStatus.OK);
        }catch (Exception ex){
//            logger.error("Exception in User Service", ex);
            return new ResponseEntity<>("Exception in User Service " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    public ResponseEntity<?> SignUp(@RequestBody UserInfoDTO userInfoDTO) {
}
