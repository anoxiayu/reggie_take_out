package com.ieheima.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UploadFileTest {
    @Test
    public void test1() {
        String fileName = "2341654aadf.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        log.info("{}",suffix);
    }
}
