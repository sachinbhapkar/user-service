package com.user.controller;

import com.user.dto.UserDto;
import com.user.model.User;
import com.user.service.UserService;
import com.user.service.impl.UserImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("secret.key")
    String secretKey;

    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        UserDto userDto = mapDtoToUser(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    private UserDto mapDtoToUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());  //using mapper interface
        return userDto;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDto userDto){

//        Object response = userService.loginApi(userDto);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = generateJwtToken(authentication);
            UserImpl principal = (UserImpl) authentication.getPrincipal();
            Map authDetlsMap = new HashMap();
            authDetlsMap.put("id", principal.getId());
            authDetlsMap.put("username", principal.getUsername());
            authDetlsMap.put("token", jwtToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(authDetlsMap);
        }
        catch (Exception e){
            throw new RuntimeException("invalid details");
        }
    }

    private String generateJwtToken(Authentication authentication) {
        UserImpl principal = (UserImpl)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+ 3600000))
                .signWith(SignatureAlgorithm.HS512,secretKey)
                .compact();
    }

    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam String userName){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByUsername(userName));
    }


}
