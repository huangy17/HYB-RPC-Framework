package com.huangyb.rpc.message;

import java.io.Serializable;

/**
 * @author huangyb
 * @create 2021-11-05 22:31
 */
public class RpcResponseMessage<T> implements Serializable {

    private Integer code;
    private T data;
    private int id;


    public static final Integer SUCCESS_CODE = 200;
    public static final Integer FAIL_CODE = 500;

    public RpcResponseMessage(){

    };

    public RpcResponseMessage(Integer code, T data, int id) {
        this.code = code;
        this.data = data;
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
