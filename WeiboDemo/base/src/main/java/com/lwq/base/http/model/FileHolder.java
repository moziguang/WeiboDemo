package com.lwq.base.http.model;

import java.io.File;


/*
 * Description :
 *
 * Creation    : 2016/10/11
 * Author      : moziguang@126.com
 */
public class FileHolder {
 
	private String fileName;
	private String contentType;
	private File file;
    private byte[] data;//此参数不为null时不在使用file参数

	public FileHolder(String fileName, String contentType, File file) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.file = file;
	}

    public FileHolder(String fileName, String contentType, byte[] data) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

	public String getFileName() {
		return fileName;
	}
	public String getContentType() {
		return contentType;
	}
	public File getFile() {
		return file;
	}

    public byte[] getData() {
        return data;
    }
}
