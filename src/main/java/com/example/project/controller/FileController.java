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


import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

            //UniqueID column is created for the users
            fileService.modifyFile(userFile);

            //sending the modified file to the api

            File modifiedFile = new File("C:/Users/Dev/Documents/project/modifiedFiles/"+fileName);
            FileSystemResource value = new FileSystemResource(modifiedFile);

            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("file", value);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

            try {

                RestTemplate restTemplate = new RestTemplate();

                ResponseEntity<Object> response = restTemplate.exchange("http://localhost:8086/api/v1/import-order-excel", HttpMethod.POST, requestEntity, Object.class);
                message = response.getBody().toString();
                //trimming the message from the api
                message = message.substring(1, message.length() - 1);
                return ResponseEntity.status(HttpStatus.OK).body(message);

            } catch (Exception e) {
                //Exceptions from the api
                e.printStackTrace();
                message = e.getMessage();
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(message);
            }
        } catch (CustomException | InvalidFormatException e) {
            //file format is not supported
            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
        }
    }


}

