package jk.core.excel.cellinfo;


import jk.core.ex.ExcelParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Jack lee
 */
public class ExcelInputStream {
    private File file;
    private InputStream inputStream;
    private URL url;

    public ExcelInputStream(File file){
        this.file = file;
        check();
    }
    public ExcelInputStream(InputStream inputStream){
        this.inputStream = inputStream;
        check();
    }
    public ExcelInputStream(URL url){
        this.url = url;
        check();
    }

    public File getFile() {
        return file;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public URL getUrl() {
        return url;
    }

    private void check() {
        if(file != null){
            if(!file.exists()){
                throw new ExcelParseException("文件不存在："+file);
            }
            if(file.isDirectory()){
                throw new ExcelParseException("文件必能为文件夹："+file);
            }
        }

        if(inputStream != null){
            try {
                if (inputStream.available() == 0) {
                    throw new ExcelParseException("输入流不可用");
                }
            }catch (IOException ex){
                throw new ExcelParseException("输入流异常");
            }
        }

        if(url != null){
            InputStream is = null;
            try {
                is = url.openStream();
            } catch (IOException e) {
                throw new ExcelParseException("url无效："+url);
            }finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
