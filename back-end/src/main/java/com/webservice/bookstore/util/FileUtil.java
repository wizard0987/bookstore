package com.webservice.bookstore.util;

import com.webservice.bookstore.web.dto.ItemDto;
import com.webservice.bookstore.web.dto.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;

@Slf4j
@Component
public class FileUtil<T extends ItemDto.ItemAddDto, M extends MemberDto.Modify> {

    private String prefixPath = System.getProperty("user.dir");
    private String lastSubString = prefixPath.substring(prefixPath.lastIndexOf("/"));
    private String path = null;

    public void deleteImageFile(String uploadImageName) throws IOException {
        if(lastSubString.equals("/back-end")) {
            path = prefixPath + "/src/main/resources/static/";
        } else if (lastSubString.equals("/bookstore")) {
            path = prefixPath + "/back-end/src/main/resources/static/" ;
        }

        log.info("path :  " + path);
        File file = new File(path);
        if(file.exists()) {
            File[] files = file.listFiles();
            if(files.length != 0) {
                for (File f : files) {
                    if (f.getName().equals(uploadImageName)) {
                        f.delete();
                        System.out.println(f.getName() + " 삭제 성공");
                    } else {
                        throw new FileSystemException("해당 파일이 존재하지 않아 삭제되지 않았습니다.");
                    }
                }
            } else {
                log.info("dsafasfasdfasdfdsafasd");
                throw new FileSystemException("해당 디렉토리는 비어있는 파일입니다.");
            }
        } else {
            throw new FileNotFoundException("해당 디렉토리 존재하지 않습니다");
        }

    }

    public void checkImageType(T itemDto, String contentType, BufferedImage bufferedImage) throws Exception {
        String path = checkImageFilePath(itemDto);
        String isbn = itemDto.getIsbn();
        if (contentType.contains("image/jpeg")) {
            itemDto.setUpload_image_name(isbn + ".jpg");
            ImageIO.write(bufferedImage, "jpg", new File(path + ".jpg"));
        } else if (contentType.contains("image/png")) {
            itemDto.setUpload_image_name(isbn + ".png");
            ImageIO.write(bufferedImage, "png", new File(path + ".png"));
        } else if (contentType.contains("image/gif")) {
            itemDto.setUpload_image_name(isbn + ".gif");
            ImageIO.write(bufferedImage, "gif", new File(path + ".gif"));
        }
    }

    public String checkImageFilePath(T itemDto) {
        String path = null;

        if(lastSubString.equals("/back-end")) {
            path = prefixPath + "/src/main/resources/static/" + itemDto.getIsbn();
        } else if (lastSubString.equals("/bookstore")) {
            path = prefixPath + "/back-end/src/main/resources/static/" + itemDto.getIsbn();
        }
        return path;
    }

    public void checkImageType(M memberDto, String contentType, BufferedImage bufferedImage) throws Exception {
        String path = checkImageFilePath(memberDto);
        if (contentType.contains("image/jpeg")) {
            ImageIO.write(bufferedImage, "jpg", new File(path + ".jpg"));
        } else if (contentType.contains("image/png")) {
            ImageIO.write(bufferedImage, "png", new File(path + ".png"));
        } else if (contentType.contains("image/gif")) {
            ImageIO.write(bufferedImage, "gif", new File(path + ".gif"));
        }
    }

    public String checkImageFilePath(M memberDto) {
        String path = null;

        if(lastSubString.equals("/back-end")) {
            path = prefixPath + "/src/main/resources/static/" + memberDto.getEmail();
        } else if (lastSubString.equals("/bookstore")) {
            path = prefixPath + "/back-end/src/main/resources/static/" + memberDto.getEmail();
        }
        return path;
    }



}
