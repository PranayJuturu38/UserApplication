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
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm");
    LocalDateTime now = LocalDateTime.now();
    String fileName = "modifiedFile" + dtf.format(now) + ".xlsx";

    @Override
    public MultipartFile uploadFile(MultipartFile userFile) throws IOException {
        try {
            if (TYPE.equals(userFile.getContentType())) {
                String fileName = StringUtils.cleanPath(userFile.getOriginalFilename());
                FileData FileData = new FileData(fileName, userFile.getContentType(), userFile.getBytes());
                return userFile;
            } else {
                throw new CustomException("File type is not supported");
            }
        } catch (Exception uploadException) {
            throw uploadException ;
        }
    }

    @Override
    public MultipartFile modifyFile(MultipartFile userFile) throws IOException, InvalidFormatException {
        String message = " ";
        try {

            InputStream is = userFile.getInputStream();

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            if(sheet.getPhysicalNumberOfRows() == 0){
                throw new Exception("File is empty");
            }

            //setting the font for new column
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(headerFont);


            //Column heading
            Row rowHeading = sheet.getRow(0);
            int lastCellNumber = rowHeading.getLastCellNum();
            Cell cellHeading = rowHeading.getCell(lastCellNumber);
            if (cellHeading == null) {

                cellHeading = rowHeading.createCell(lastCellNumber);

            }
            cellHeading.setCellValue("UniqueID");
            cellHeading.setCellStyle(cellStyle);

            //getting the column number of username
            int userNameColumn = -1;
            int contactNumberColumn = -1;
            Row userNameRow = sheet.getRow(0);
            for (int cn=0; cn<userNameRow.getLastCellNum(); cn++) {
                Cell c = userNameRow.getCell(cn);
                if (c == null) {
                    // Can't be this cell - it's empty
                    continue;
                }
                String text =c.getStringCellValue();
                if(text.equals("UserName")) {
                    userNameColumn = cn;
                   //x break;
                }else if(text.equals("Contact No")){
                    contactNumberColumn = cn;
                    break;
                }
            }
            if (userNameColumn== -1 ||contactNumberColumn==-1 ) {
                throw new Exception("None of the cells in the first row were UserName or Contact No");
            }

            //Creating a column
            int totalRows = sheet.getPhysicalNumberOfRows();
            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(lastCellNumber);
                if (cell == null) {
                    cell = row.createCell(lastCellNumber);
                } else {
                    i++;
                }

                //Filling the column with uniqueID
                Cell name = row.getCell(userNameColumn);
                Cell contactNo = row.getCell(contactNumberColumn);
                String uniqueName = name.toString().substring(0, 2);
                double contactNumber = contactNo.getNumericCellValue();
                int intPart = (int)contactNumber;
                int uniqueNumber = Integer.parseInt(Integer.toString(intPart));

                cell.setCellValue(uniqueName + uniqueNumber);
            }

            FileOutputStream outputStream = new FileOutputStream("C:/Users/Dev/Documents/project/modifiedFiles/"+fileName);
            workbook.write(outputStream);


        } catch (Exception modificationException) {
            modificationException.printStackTrace();
            throw new CustomException("File cannot be modified: " + modificationException.getMessage());
        }

        File file = new File("C:/Users/Dev/Documents/project/modifiedFiles/"+fileName);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("multiPartFile",
                file.getName(), TYPE, IOUtils.toByteArray(input));
        return multipartFile;
    }



   /* @Override
    public ResponseEntity<Object> sendFile(MultipartFile userFile) throws IOException {
        //Posting the modified file into the api

        File file = new File("C:/Users/Dev/Documents/Kpi Stuff/main/modifiedFile.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
               file.getName(), TYPE, IOUtils.toByteArray(input));

        FileSystemResource resource = new FileSystemResource(new File(filePath));
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", userFile.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.exchange("http://localhost:8086/api/v1/import-order-excel", HttpMethod.POST, requestEntity, Object.class);

        return response;
    }
*/

}

