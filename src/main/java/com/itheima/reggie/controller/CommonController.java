package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String  basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是个临时文件，需要转存
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();

        //使用uuid重新生成文件名称，防止文件名称重复造成的文件覆盖
        String fileName = UUID.randomUUID().toString()+"."+originalFilename.substring(originalFilename.lastIndexOf("."));

        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            //临时文件转存
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download( String name,HttpServletResponse response) {
        try {
            FileInputStream fis = new FileInputStream(new File(basePath + name));
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpg");

            outputStream.write(fis.readAllBytes());
            outputStream.flush();
            //关闭资源
            outputStream.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
