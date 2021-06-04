package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileService {

	public static byte[] getByteArray(MultipartFile file) throws IOException {
		InputStream initialStream = file.getInputStream();
		byte[] byteArray = new byte[initialStream.available()];
		initialStream.read(byteArray);
		return byteArray;
	}

	public static String getOriginalFileName(MultipartFile file) {
		return StringUtils.cleanPath(file.getOriginalFilename());
	}

}
