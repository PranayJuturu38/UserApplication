package com.example.project.serviceImpl;

import com.example.project.Messages.CustomException;
import com.example.project.Messages.Message;
import com.example.project.model.FileData;
import com.example.project.service.FileService;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class FileServiceImpl implements FileService {


    private static final String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String fileName = "modifiedFile"+dtf.format(now)+"xlsx";
    @Override
    public FileData uploadFile(MultipartFile userFile) throws IOException {
        try {
            if (TYPE.equals(userFile.getContentType())) {
                String fileName = StringUtils.cleanPath(userFile.getOriginalFilename());
                FileData FileData = new FileData(fileName, userFile.getContentType(), userFile.getBytes());
                return FileData;
            }
            else{
                throw new CustomException("File type is not supported");
            }
        } catch (Exception uploadException) {
            throw new CustomException("File type is not supported");
        }
    }

    @Override
    public MultipartFile modifyFile(MultipartFile userFile) throws IOException, InvalidFormatException {
        String message = " ";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String fileName = dtf.format(now)+"_"+"modifiedFile.xlsx";
        try {

            InputStream is = userFile.getInputStream();

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            //setting the font for new column
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(headerFont);
            int totalRows = sheet.getPhysicalNumberOfRows();


            //Column heading
            Row rowHeading = sheet.getRow(0);
            int lastCellNumber = rowHeading.getLastCellNum();
            Cell cellHeading = rowHeading.getCell(lastCellNumber);
            if (cellHeading == null) { //auto increment
                cellHeading = rowHeading.createCell(lastCellNumber);
            }
            cellHeading.setCellValue("UniqueID");
            cellHeading.setCellStyle(cellStyle);

            //Creating a column
            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(lastCellNumber);
                if (cell == null) {
                    cell = row.createCell(lastCellNumber);
                }
                else{
                    i++;
                }
                Cell name = row.getCell(0);
                Cell contactNo = row.getCell(2);
                String uniqueName = name.toString().substring(0, 2);
                String uniqueNumber = contactNo.toString().substring(0, 3);

                cell.setCellValue(uniqueName + uniqueNumber);
            }//Modified + date and time
            FileOutputStream outputStream = new FileOutputStream("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx");
            workbook.write(outputStream);;

        } catch (Exception modificationException) {
            modificationException.printStackTrace();
            throw new CustomException("File cannot be modified");
        }

        File file = new File("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("multiPartFile",
                file.getName(), TYPE, IOUtils.toByteArray(input));
        return multipartFile;
    }

    @Override
    public FileData sendFile(MultipartFile userFile) throws IOException {
        //Posting the modified file into the api
//        File file = new File("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx");
//        FileInputStream input = new FileInputStream(file);
//
//        MultipartFile multipartFile = new MockMultipartFile("file",
//                file.getName(), TYPE, IOUtils.toByteArray(input));

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", userFile.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange("http://localhost:8086/api/v1/import-order-excel", HttpMethod.POST, requestEntity, String.class);

        return new FileData(userFile.getOriginalFilename(), userFile.getContentType(), userFile.getBytes());
    }


}

