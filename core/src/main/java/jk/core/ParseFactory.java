package jk.core;


import jk.core.csv.CsvParser;
import jk.core.excel.parse.base.DetectFileType;
import jk.core.excel.parse.base.FileType;
import jk.core.excel.parse.base.ParseInfo;
import jk.core.ex.ExcelParseException;
import jk.core.excel.parse.poi.HslfParser;
import jk.core.excel.parse.poi.XssfParser;
import org.apache.commons.lang.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 */
public final class ParseFactory {

	private ParseFactory() {
	}

	public static Excel getExcelParse(ParseInfo info) {
		File file = info.getFile();
		DetectFileType det = new DetectFileType(file);
		FileType type = det.getType();

		if (FileType.XLS_DOC.equals(type)) {
			return getHslfParse(info);
		} else if (FileType.XLSX.equals(type)) {
			return getXssfParse(info);
		}else if(FileType.CSV.equals(type) || FileType.TSV.equals(type)){
			return getCsvParse(info);
		}else {
			throw new ExcelParseException(
					"not found any parse for the file type-->"
							+ info.getFileType());
		}
	}

	private static Excel getCsvParse(ParseInfo info) {
		try {
			if(StringUtils.isEmpty(info.getCharset()) && info.isAutoDetectorCharset()){
				String charset = UniversalDetector.detectCharset(new FileInputStream(info.getFile()));
				info.setCharset(charset);
			}
			if(StringUtils.isEmpty(info.getCharset())){
				info.setCharset(Charset.defaultCharset().name());
			}
			if (!info.isInited()) {
				info.init();
			}
			return new CsvParser(info);
		} catch (Exception e) {
			throw new ExcelParseException(e.getMessage(), e);
		}
	}

	private static Excel getXssfParse(ParseInfo info) {
		try {
			if (!info.isInited()) {
				info.init();
			}
			return new XssfParser(info);
		} catch (Exception e) {
			throw new ExcelParseException(e.getMessage(), e);
		}
	}

	private static Excel getHslfParse(ParseInfo info) {
		try {
			if (!info.isInited()) {
				info.init();
			}
			return new HslfParser(info);
		} catch (FileNotFoundException e) {
			throw new ExcelParseException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ExcelParseException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new ExcelParseException(e.getMessage(), e);
		}
	}
}
