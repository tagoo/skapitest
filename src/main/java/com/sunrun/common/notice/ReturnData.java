package com.sunrun.common.notice;

public class ReturnData extends ReturnCode {

    private boolean success;
    private Object data;

    public ReturnData() {
        super();
    }

    public ReturnData(int resultCode, String msg) {
        super(resultCode,msg);
    }
    public ReturnData(int resultCode, String msg, Object data) {
        this(resultCode,msg);
        this.data = data;
    }

    public ReturnData(int resultCode, String msg, boolean success, Object data) {
        this(resultCode,msg,data);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReturnData{" +
                "success=" + success +
                ", data=" + data +
                ", resultCode=" + resultCode +
                ", msg='" + msg + '\'' +
                '}';
    }
}
