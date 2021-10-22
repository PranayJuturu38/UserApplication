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


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        String message = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy ");
        LocalDateTime now = LocalDateTime.now();
        String fileName = dtf.format(now)+"_"+"modifiedFile.xlsx";
        try {

            fileService.uploadFile(file);
            fileService.modifyFile(file);

                 File modifiedFile = new File("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx");
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
                message = message.substring(1, message.length() - 1);

            } catch (Exception e) {
                 e.printStackTrace();
                 message = e.getMessage();
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(message);
            }
        }
        catch (CustomException | InvalidFormatException e) {
            message = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
        }
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }


    @GetMapping("/modify")
    public ResponseEntity<Message> modifyData() throws IOException {
        InputStream in = new URL("http://localhost:9090/download/UserData.xlsx").openStream();
        Files.copy(in, Paths.get("UserDataToBeModified.xlsx"), StandardCopyOption.REPLACE_EXISTING);
        Path excelPath = Paths.get("UserDataToBeModified.xlsx");
        String message = "";

        try {
            FileInputStream inputStream = new FileInputStream(excelPath.toString());
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            //setting the font for new column
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(headerFont);
            int totalRows = sheet.getPhysicalNumberOfRows();


            //Column heading
            Row rowHeading = sheet.getRow(0);
            Cell cellHeading = rowHeading.getCell(4);
            if(cellHeading == null){
                cellHeading = rowHeading.createCell(4);
            }
            cellHeading.setCellValue("UniqueID");
            cellHeading.setCellStyle(cellStyle);

            //Creating a column
            for(int i=1;i<totalRows;i++){
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(4);
                if (cell == null) {
                    cell = row.createCell(4);
                }
                Cell name = row.getCell(0);
                Cell contactNo = row.getCell(3);
                String uniqueName = name.toString().substring(0,2);
                String uniqueNumber = contactNo.toString().substring(0,3);

            cell.setCellValue(uniqueName+uniqueNumber);
            }
            message="modified and sent";

            FileOutputStream outputStream = new FileOutputStream("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx");
            workbook.write(outputStream);

            //Posting the modified file into the api

            File modifiedFile = new File("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx");
            FileSystemResource value = new FileSystemResource(modifiedFile);

            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("file", value);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

            String serverUrl = "http://localhost:8086/api/v1/import-order-excel";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(serverUrl, HttpMethod.POST, requestEntity, String.class);

            return ResponseEntity.status(HttpStatus.OK).body(new Message(message));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Message(message));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Message(message));

        } catch (InvalidFormatException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Message(message));

        }
    }


    @PostMapping("/sendFile")
    public ResponseEntity<Message> sendModifiedFile(@RequestParam("file") MultipartFile file){
        String message="";
        try{
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

            FileSystemResource value = new FileSystemResource(new File("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx"));
            map.add("file", value);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange("http://localhost:8086/api/v1/import-order-excel",
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

//            Path path = Paths.get("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx");
//            String name = "modifiedFile.xlsx";
//            String originalFileName = "modifiedFile.xlsx";
//            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//            byte[] content = null;
//            try {
//                content = Files.readAllBytes(path);
//            } catch (final IOException e) {
//            }
//            MultipartFile multiPartExcel = new MockMultipartFile(file.getName(),
//                    file.getOriginalFilename(), file.getContentType(), content);

//            MultiValueMap<String, Object> body
//                    = new LinkedMultiValueMap<>();
//                        body.add("file",file.getBytes());
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//
//            HttpEntity<MultiValueMap<String, Object>> requestEntity
//                    = new HttpEntity<>(body,headers);
//
//            String serverUrl = "http://localhost:8086/api/v1/import-order-excel";
//
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<String> response = restTemplate.exchange(serverUrl,
//                    HttpMethod.POST, requestEntity, String.class);
//         //  restTemplate.postForEntity(serverUrl, requestEntity, String.class);
//           restTemplate.postForLocation(serverUrl,requestEntity);
             message="File sent";
            return ResponseEntity.status(HttpStatus.OK).body(new Message(message));
        }catch (Exception e){
            message="File not sent"+"---"+e;
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Message(message));

        }

    }
}