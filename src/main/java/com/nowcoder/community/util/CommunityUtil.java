package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串（用于生成用户的激活码 以及 用于生成用户的登录凭证）
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

//    public static String getJSONString(int code, String msg, Map<String, Object> map){
//        JSONObject json = new JSONObject();
//        json.put("code", code);
//        json.put("msg", msg);
//        if(map != null){
//            for(String key : map.keySet()){
//                json.put(key, map.get(key));
//            }
//        }
//        return json.toJSONString();
//    }
//
//    public static String getJSONString(int code, String msg){
//        return getJSONString(code, msg, null);
//    }
//
//    public static String getJSONString(int code){
//        return getJSONString(code, null, null);
//    }
}
