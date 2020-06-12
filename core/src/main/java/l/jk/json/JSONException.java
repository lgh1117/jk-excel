package l.jk.json;

/**
 * @ClassName JSONException
 * @Description
 * @Version 1.0.0
 * @Author liguohui
 * @Since 2020/6/9 下午8:29
 */
public class JSONException extends RuntimeException {
    public JSONException() {
    }

    public JSONException(String message) {
        super(message);
    }

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONException(Throwable cause) {
        super(cause);
    }

    public JSONException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
