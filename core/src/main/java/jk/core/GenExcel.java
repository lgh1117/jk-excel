package jk.core;

import jk.core.ex.ExportException;

/**
 * @Description
 * @Version 1.0.0
 * @Author Jack.Lee
 */
public interface GenExcel {

    /**
     * 写出excel内容到outstream或者到指定文件
     * @return
     * @throws ExportException
     */
    boolean write() throws ExportException;
}
