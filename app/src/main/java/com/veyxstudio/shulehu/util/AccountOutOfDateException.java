package com.veyxstudio.shulehu.util;

/**
 * Created by Veyx Shaw on 2016/4/2.
 * To describe account out of date exception.
 */
public class AccountOutOfDateException extends Exception{

    public AccountOutOfDateException(String detailMessage){
        super(detailMessage);
    }

}
