package jk.core.excel.parse.base;

import jk.core.ex.ExcelParseException;
import jk.core.util.RegExpUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * Title: 根据数据流检测文件类型
 * </p>
 * <p>
 * Description: [描述该类概要功能介绍]
 * </p>
 * 
 * @file: FileType.java
 * @author: Jack.Lee
 * @version: v1.0
 */
public class DetectFileType {

	private File file;

	private FileType type;

	public DetectFileType(File file) {
		this.file = file;
	}

	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	private String getHeader() {
		byte[] b = new byte[32];
		InputStream inputStream = null;
		if(file == null || !file.exists()){
			throw new ExcelParseException("not found any file or file is not exists");
		}
		try {
			inputStream = new FileInputStream(file);
			inputStream.read(b, 0, 32);
		} catch (Exception e) {
			throw new ExcelParseException(e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return bytesToHexString(b);
	}

	public FileType getType() {
		//根据文件名检测，速度快
		checkTypeByFileName();
		if(type == null) {
			String fileHead = getHeader();
			fileHead = fileHead.toUpperCase();
			if (fileHead != null && fileHead.length() > 0) {
				fileHead = fileHead.toUpperCase();
				FileType[] fileTypes = FileType.values();
				for (FileType t : fileTypes) {
					if (fileHead.startsWith(t.getValue())) {
						type = t;
						break;
					}
				}
			}
		}

		if (FileType.ZIP == type) {
			return detectMSDoc();
		}
		return type;
	}

	private void checkTypeByFileName() {
		String ext = RegExpUtil.getFileExt(file);
		for(FileType ft : FileType.values()){
			if(ft.equalsByExt(ext)){
				type = ft;
				return;
			}
		}
	}

	/**
	 * 
	 */
	private FileType detectMSDoc() {
		String filename = file.getName();
		filename = filename.toLowerCase();
		if (filename.endsWith(".xlsx")) {
			return FileType.XLSX;
		} else if (filename.endsWith(".docx")) {
			return FileType.DOCX;
		}
		return FileType.ZIP;
	}

	private void detectMSDoc2007() {
		try {
			File tmp = new File(file.getParentFile(), "f.tmp");
			tmp.createNewFile();
			FileUtils.copyFile(file, tmp);
			ZipInputStream zis = new ZipInputStream(new FileInputStream(tmp));
			ZipEntry zip = zis.getNextEntry();
			int index = 0;
			while (zip != null) {
				String name = zip.getName();
				if (name.equals("xl/workbook.xml")) {
					index++;
				} else if (name.startsWith("xl/worksheets")) {
					index++;
				} else if (name.equals("word/document.xml")) {
					index++;
				}
				zip = zis.getNextEntry();
			}
			zis.close();
			tmp.delete();
			if (index == 1) {
				type = FileType.DOCX;
			} else if (index >= 2) {
				type = FileType.XLSX;
			}
		} catch (Exception e) {
			throw new ExcelParseException("检测文件类型出错-->" + file);
		}

	}

	public static void main(String[] args) {
		File file = new File("d:/javolution-5.4.1.jar - 副本.zip");
		DetectFileType d = new DetectFileType(file);
		System.out.println(d.getType());

		Map<String, Object> m = new HashMap<>();
		System.out.println(m.values().iterator());
	}
}
