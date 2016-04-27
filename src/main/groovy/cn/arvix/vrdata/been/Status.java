package cn.arvix.vrdata.been;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghaiyang on 16/4/27.
 */
public class Status {
    private int code = 0;
    private List<String> message = new ArrayList<String>();

    public void addMessage(String message) {
        this.message.add(message);
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setMessage(List<String> msg) {
        this.message = msg;
    }

    public List<String> getMessage() {
        return message;
    }
}
