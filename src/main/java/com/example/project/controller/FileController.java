package com.example.project.controller;

import com.example.project.Messages.CustomException;
import com.example.project.Messages.Message;
import com.example.project.Messages.UserException;
import com.example.project.model.UserData;
import com.example.project.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController

public class FileController {

    @Autowired
    private FileService fileService;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");
    LocalDateTime now = LocalDateTime.now();
    String fileName = "modifiedFile" + dtf.format(now) + ".xlsx";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        String message = "";

        try {
            //uploading the file and returning the uploaded file as a multipart file
            MultipartFile userFile = fileService.uploadFile(file);

            try {

                //UniqueID column is created for the users
                fileService.modifyFile(userFile);

            }catch (Exception exception){
                message = exception.getMessage();
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
            }
            //sending the modified file to the api
            File modifiedFile = new File("C:/Users/Dev/Documents/project/modifiedFiles/"+fileName);
            FileSystemResource value = new FileSystemResource(modifiedFile);
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("file", value);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
            String apiUrl = "http://localhost:8086/api/v1/import-order-excel";
            ResponseEntity<Object> response = new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);

            try {

                RestTemplate restTemplate = new RestTemplate();
                response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Object.class);
                message = response.getBody().toString();

                //trimming the message from the api
                message = message.substring(1, message.length() - 1);
                    return ResponseEntity.status(HttpStatus.OK).body(message);

            } catch (Exception e) {
                HttpStatus statusCode = response.getStatusCode();

                if(statusCode.isError()){
                    message = "Service is down";
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
                }
                    message = e.getMessage();
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(message);
            }
        } catch (CustomException e) {

            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
        }
    }

  @GetMapping("/users/{username}")
  public ResponseEntity<String> retrievePassword(@PathVariable("username") String userName){
        String message = "";
      HttpHeaders headers = new HttpHeaders();
      headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      HttpEntity<?> entity = new HttpEntity<>(headers);
      String url = "http://localhost:8086/api/v1/{username}";

      String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).queryParam("username","{username}").encode().toUriString();
      Map<String, String> params = new HashMap<>();
      params.put("username",userName);
      ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.NOT_IMPLEMENTED);

      try{
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.exchange(urlTemplate,HttpMethod.GET,entity,String.class,params);
            message = response.getBody();
        return ResponseEntity.status(HttpStatus.OK).body(message);
        }catch (CustomException e){
          HttpStatus statusCode = response.getStatusCode();

          if(statusCode.isError()){
              message = "Service is down";
              return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
          }
          message = e.getMessage();
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
      }
  }
}

