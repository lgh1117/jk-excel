package jk.core;

import jk.core.ex.ExportException;

/**
 * @Description
 * @Version 1.0.0
 * @Author liguohui lgh1177@126.com
 */
public interface GenExcel {

    /**
     * 写出excel内容到outstream或者到指定文件
     * @return
     * @throws ExportException
     */
    boolean write() throws ExportException;

    /**
     * 关闭所有文件流
     */
    void close() throws ExportException;
}
