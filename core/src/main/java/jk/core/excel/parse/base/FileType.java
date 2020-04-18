package jk.core.excel.parse.base;

/**
 * 定义文件类型对应的十六进制头部
 * @author: Jack lee
 * @version: v1.0
 */
public enum FileType {
	/**
	 * JEPG.
	 */
	JPEG("FFD8FF",".jpeg"),

	/**
	 * PNG.
	 */
	PNG("89504E47",".png"),

	/**
	 * GIF.
	 */
	GIF("47494638",".gif"),

	/**
	 * TIFF.
	 */
	TIFF("49492A00",".tiff"),

	/**
	 * Windows Bitmap.
	 */
	BMP("424D",".bmp"),

	/**
	 * CAD.
	 */
	DWG("41433130",".cad"),

	/**
	 * Adobe Photoshop.
	 */
	PSD("38425053",".psd"),

	/**
	 * Rich Text Format.
	 */
	RTF("7B5C727466",".rtf"),

	/**
	 * XML.
	 */
	XML("3C3F786D6C",".xml"),

	/**
	 * HTML.
	 */
	HTML("68746D6C3E",".html"),

	/**
	 * Email [thorough only].
	 */
	EML("44656C69766572792D646174653A",".eml"),

	/**
	 * Outlook Express.
	 */
	DBX("CFAD12FEC5FD746F",".dbx"),

	/**
	 * Outlook (pst).
	 */
	PST("2142444E",".pst"),

	/**
	 * MS Word/Excel.
	 */
	XLS_DOC("D0CF11E0",".xls"),

	/**
	 * MS Access.
	 */
	MDB("5374616E64617264204A",".mdb"),

	/**
	 * WordPerfect.
	 */
	WPD("FF575043",".wpd"),

	/**
	 * Postscript.
	 */
	EPS("252150532D41646F6265",".eps"),

	/**
	 * Adobe Acrobat.
	 */
	PDF("255044462D312E",".pdf"),

	/**
	 * Quicken.
	 */
	QDF("AC9EBD8F",".qdf"),

	/**
	 * Windows Password.
	 */
	PWL("E3828596",".pwl"),

	/**
	 * Wave.
	 */
	WAV("57415645",".wav"),

	/**
	 * AVI.
	 */
	AVI("41564920",".avi"),

	/**
	 * Real Audio.
	 */
	RAM("2E7261FD",".ram"),

	/**
	 * Real Media.
	 */
	RM("2E524D46",".rm"),

	/**
	 * MPEG (mpg).
	 */
	MPG("000001BA","pmg"),

	/**
	 * Quicktime.
	 */
	MOV("6D6F6F76",".mov"),

	/**
	 * Windows Media.
	 */
	ASF("3026B2758E66CF11",".asf"),

	/**
	 * MIDI.
	 */
	MID("4D546864",".mid"),
	/**
	 * excel 2007 50 4B 03 04 14 00 06 00 08 00 00 00 21 00 E7 4B
	 */
	XLSX("504B030414000600080000002100",".xlsx"),
	/***
	 * word 2007 50 4B 03 04 14 00 06 00 08 00 00 00 21 00 DD FC
	 */
	DOCX("504B030414000600080000002100",".docx"),
	/***
	 * PPTX 2007 50 4B 03 04 14 00 06 00 08 00 00 00 21 00 DD FC
	 */
	PPTX("504B030414000600080000002100",".pptx"),
	/**
	 * ZIP Archive. 504b0304140000000800
	 * 504B0304140008000800F550533F000000000000000000000000140000006A61
	 */
	ZIP("504B0304",".zip"), /**
	 * RAR Archive.
	 */
	RAR("52617221",".rar"),
	/**
	 * csv 5B75726C
	 */
	CSV("5B75726C",".csv"),
	/**
	 * tsv 5B75726C
	 */
	TSV("5B75726C",".tsv")
	;

	/**
	 * 头部二进制标记
	 */
	private String value = "";
	/**
	 * 根据文件名后缀
	 */
	private String ext = "";

	/**
	 * Constructor.
	 * 
	 * @param value
	 * @param ext
	 */
	private FileType(String value, String ext) {
		this.value = value;
		this.ext = ext;
	}

	public String getValue() {
		return value;
	}

	public String getExt(){
		return ext;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean equalsByExt(String _ext){
		if(_ext == null || ext == null){
			return  false;
		}
		return ext.equalsIgnoreCase(_ext);
	}
}
