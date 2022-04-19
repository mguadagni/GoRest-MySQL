package com.careerdevs.gorestsql.controllers;

import com.careerdevs.gorestsql.models.User;
import com.careerdevs.gorestsql.repos.UserRepository;
import com.careerdevs.gorestsql.utils.ApiErrorHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping ("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping ("/{id}")
    public ResponseEntity<?> getUserById (@PathVariable ("id") String id) {

        try {

            if(ApiErrorHandling.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            int uID = Integer.parseInt(id);

            Optional<User> foundUser = userRepository.findById(uID);

            if (foundUser.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<?> deleteUserById (@PathVariable ("id") String id) {

        try {

            if(ApiErrorHandling.isStrNaN(id)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            int uID = Integer.parseInt(id);

            Optional<User> foundUser = userRepository.findById(uID);

            if (foundUser.isEmpty()) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, id + " is not a valid ID");

            }

            userRepository.deleteById(uID);

            return new ResponseEntity<>(foundUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadUserById (
            @PathVariable ("id") String userId,
            RestTemplate restTemplate
    ) {

        try {

            if(ApiErrorHandling.isStrNaN(userId)) {

                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, userId + " is not a valid ID");

            }

            int uID = Integer.parseInt(userId);

            String url = "https://gorest.co.in/public/v2/users/" + uID;

            User foundUser = restTemplate.getForObject(url, User.class);

            if (foundUser == null) {

                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User data was null");

            }

            User savedUser = userRepository.save(foundUser);

            return new ResponseEntity<>(savedUser, HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            return ApiErrorHandling.customApiError(e.getMessage(), e.getStatusCode());

        } catch (NumberFormatException e) {

            return new ResponseEntity<>("ID must be a number", HttpStatus.NOT_FOUND);

        } catch (Exception e){

            return ApiErrorHandling.genericApiError(e);

        }

    }

    @GetMapping ("/all")
    public ResponseEntity<?> getAllUser () {

        try {

            Iterable<User> allUsers = userRepository.findAll();
            return new ResponseEntity<>(allUsers, HttpStatus.OK);

        } catch (Exception e) {

            return ApiErrorHandling.genericApiError(e);

        }

    }

}
