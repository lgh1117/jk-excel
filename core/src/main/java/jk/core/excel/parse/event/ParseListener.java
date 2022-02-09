package jk.core.excel.parse.event;

import jk.core.excel.parse.base.Header;

import java.util.List;
import java.util.Map;

/**
 * 监听每行解决结果，并将相应信息传递给监听器做行处理，配合解析使用；
 *
 * @author liguohui lgh1177@126.com
 */
public interface ParseListener {
    /**
     * <p>
     * Discription:与解析器接口parse成对使用；
     * 如果希望在解析过程中，数据量过大，希望一条一条的将数据传递给监听器，由监听器做分批处理时，可以用此接口；
     * 接口接收到的数据是经过RowDataHandle接口和CellDataHandle接口处理后的数据，及rowOpration接口中的参数row是被处理过的结果
     * </p>
     * @param sheetName
     * 			对应的sheet名称
     * @param rowIndex
     *            第几行
     * @param row
     *            当前行数据
     * @param headers
     *            所有列头信息
     * @param extras
     *            存放外部信息
     * @return
     */
    void rowOpration(String sheetName, int rowIndex, Map row, List<Header> headers, Map<String, Object> extras);

}
