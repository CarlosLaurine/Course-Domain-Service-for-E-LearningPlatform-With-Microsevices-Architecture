package com.ead.course.clients;

import com.ead.course.dtos.ResponsePageDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class AuthUserClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UtilsService utilsService;

    @Value("${ead.api.url.authuser}")
    private String REQUEST_URI_AUTHUSER;

    public Page<UserDTO> getAllUsersByCourse(UUID courseId, Pageable pageable){
        List<UserDTO> searchResult = null;
        String requestUrl = REQUEST_URI_AUTHUSER + utilsService.generateUrlGetAllUsersByCourse(courseId, pageable);
        log.debug("Request URL: {} ", requestUrl);
        log.info("Request URL: {} ", requestUrl);
        try{
            ParameterizedTypeReference<ResponsePageDTO<UserDTO>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ResponsePageDTO<UserDTO>> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET,null, responseType);
            searchResult = responseEntity.getBody().getContent();
            log.debug("Response Number of Elements (Users): {} ", searchResult.size());
        } catch (HttpStatusCodeException exception){
            log.error("Error request /users endpoint exception {} ", exception);
        }
        log.info("Ending request /users courseId {} ", courseId);
        return new PageImpl<>(searchResult);
    }

    public ResponseEntity<UserDTO> getUserById (UUID userId){
        String requestUrl = REQUEST_URI_AUTHUSER + utilsService.generateUrlgetUserById(userId);
        return restTemplate.exchange(requestUrl, HttpMethod.GET, null, UserDTO.class);
    }
}
