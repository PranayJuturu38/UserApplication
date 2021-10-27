package com.example.project.service;

import com.example.project.Messages.Message;
import com.example.project.model.FileData;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileService {

    MultipartFile uploadFile(MultipartFile userFile) throws IOException;

    MultipartFile modifyFile(MultipartFile userFile) throws IOException, InvalidFormatException;

   // ResponseEntity<Object> sendFile(MultipartFile userFile) throws IOException;

}
