package com.huangyb.rpc.api;

import java.io.Serializable;

/**
 * @author huangyb
 * @create 2021-11-05 13:47
 */
public class HiObject implements Serializable {
    private Integer id;
    private String content;

    public HiObject(){

    }

    public HiObject(Integer id, String content) {
        this.id = id;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
