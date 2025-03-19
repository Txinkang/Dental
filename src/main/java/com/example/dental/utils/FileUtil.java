package com.example.dental.utils;


import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class FileUtil {
    private static final LogUtil logUtil = LogUtil.getLogger(FileUtil.class);

    public static String saveFile(MultipartFile file, String path) {
        int runStep = 0;
        try {
            do {
                if (file.isEmpty()) {
                    runStep = 1;
                    break;
                }
                //生成文件名
                String originalFilename = file.getOriginalFilename();
                if (Strings.isEmpty(originalFilename)) {
                    runStep = 2;
                    break;
                }
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                if (Strings.isEmpty(fileExtension)) {
                    runStep = 3;
                    break;
                }
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                if (Strings.isEmpty(uniqueFileName)) {
                    runStep = 4;
                    break;
                }
                //生成文件保存路径
                File directory = new File(path);
                if (!directory.exists()) {
                    if (!directory.mkdirs()) {
                        runStep = 5;
                        break;
                    }
                }
                //保存文件
                File fileToSave = new File(directory, uniqueFileName);
                file.transferTo(fileToSave);
                return uniqueFileName;
            } while (false);
            logUtil.error("save file error in step :" + runStep);
            return null;
        } catch (Exception e) {
            logUtil.error("save file error :" + e);
            return null;
        }

    }


    public static void deleteFile(String fileName, String path) {
        try {
            File file = new File(path, fileName);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logUtil.error("delete file error :" + e);
        }
    }

    public static void deleteFiles(String fileNames, String path) {
        if (Strings.isEmpty(fileNames)) {
            return;
        }
        String[] fileNamesArray = fileNames.split("\\|");
        for (String fileName : fileNamesArray) {
            deleteFile(fileName, path);
        }
    }
}
