package com.example.iotapp.controller;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequestMapping("/app")
@RestController
public class AppCotroller {
    @PostMapping("/device/status")
    public void handler(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("device status is changed:");
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println(header + "=" + req.getHeader(header));
        }
        String none = req.getHeader("none");
        String timestamp = req.getHeader("timestamp");
        String signature = req.getHeader("signature");
        boolean checkSignature = checkSignature(none, timestamp, signature, "123456");
        if (!checkSignature) {
            System.err.println("checkSignature error.");
        } else {
            System.out.println("checkSignature successful.");
        }
        ServletInputStream inputStream = req.getInputStream();
        byte[] buf = new byte[1024];
        StringBuilder message = new StringBuilder();
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            message.append(new String(buf, 0, len));
        }
        System.out.println(message);
        System.out.println("=====================================");
    }


    private boolean checkSignature(String nonce, String timestamp, String signature, String token) {
        List<String> list = new ArrayList<>();
        list.add(token);
        if (!StringUtils.isEmpty(nonce)) {
            list.add(nonce);
        }
        if (!StringUtils.isEmpty(timestamp)) {
            list.add(timestamp);
        }
        Collections.sort(list);
        StringBuilder signatureBuilder = new StringBuilder();
        for (String s : list) {
            signatureBuilder.append(s);
        }
        String serverSignature = DigestUtils.sha256Hex(signatureBuilder.toString());
        if (!StringUtils.isEmpty(serverSignature) && serverSignature.equals(signature)) {
            return true;
        }
        return false;
    }
}
