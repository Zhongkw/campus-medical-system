package com.campus.medical.utils;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件处理工具类
 */
public class FileUtils {

    /**
     * 上传文件
     *
     * @param file       待上传文件
     * @param uploadPath 上传路径
     * @return 上传后的文件路径，上传失败返回null
     */
    public static String uploadFile(MultipartFile file, String uploadPath) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFileName = generateFileName(originalFilename);
            
            File destFile = new File(uploadDir, newFileName);
            file.transferTo(destFile);

            return destFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @param response HttpServletResponse
     */
    public static void downloadFile(String filePath, HttpServletResponse response) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名，如果没有扩展名则返回空字符串
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public static String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String nameWithoutExt = originalFilename.replace(extension, "");
        
        // 使用UUID和时间戳生成唯一文件名
        String uuid = UUID.randomUUID().toString().replace("-", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        
        return nameWithoutExt + "_" + uuid + "_" + timestamp + extension;
    }

    /**
     * 检查文件大小
     *
     * @param file    待检查文件
     * @param maxSize 最大允许大小（字节）
     * @return 文件大小在允许范围内返回true，否则返回false
     */
    public static boolean checkFileSize(MultipartFile file, long maxSize) {
        if (file == null) {
            return false;
        }
        return file.getSize() <= maxSize;
    }

    /**
     * 检查文件类型
     *
     * @param file         待检查文件
     * @param allowedTypes 允许的文件类型数组
     * @return 文件类型在允许范围内返回true，否则返回false
     */
    public static boolean checkFileType(MultipartFile file, String[] allowedTypes) {
        if (file == null || allowedTypes == null || allowedTypes.length == 0) {
            return false;
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }

        String extension = getFileExtension(fileName).toLowerCase();
        for (String allowedType : allowedTypes) {
            if (extension.equals("." + allowedType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}