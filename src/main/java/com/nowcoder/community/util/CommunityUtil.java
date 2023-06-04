package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串（作为用户收到的验证码）
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密（避免密码被明文存储）
    // MD5加密只能被加密，不能被解密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
    }
}
