package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

public class CustomCast {

    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
