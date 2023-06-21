package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg", "注册成功， 我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 设置url：http://localhost:8080/community/activation/101/code
    @RequestMapping(value = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg", "激活成功，您的账号可以正常使用了！");
            model.addAttribute("target", "/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效操作，该账号已经完成激活！");
            model.addAttribute("target", "/index");
        }else{
            model.addAttribute("msg", "激活失败，您的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将生成的验证码存入session，这里用session的原因：1.跨请求，所以需要session或者cookie 2.session比cookie安全
        session.setAttribute("kaptcha", text);

        // 将图片直接输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }

    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    // rememberme:是提交表单时是否选择记住密码；session：是为了获得存储在 session 中的验证码；response：是为了将 ticket 存入 cookie
    public String login(Model model, String username, String password, String code,
                        boolean rememberme, HttpSession session, HttpServletResponse response){
        // 检查验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !code.equalsIgnoreCase(kaptcha)){
            model.addAttribute("codeMsg", "验证码不正确！");
            return "site/login";
        }

        // 检查账号和密码
        int expiredSeconds = rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){ // 成功了
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{ // 失败了,有错误
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

    // 获取忘记密码页面
    @RequestMapping(value = "/forget", method = RequestMethod.GET)
    public String getForgetPage(){
        return "/site/forget";
    }

    // 获取忘记密码的验证码
    @RequestMapping(value = "/forget/code", method = RequestMethod.GET)
    @ResponseBody
    public String getForgetCode(String email, HttpSession session){
        // 空值判断，不会触发，因为前端里已经做了非空判断
//        if(StringUtils.isBlank(email)){
//            return CommunityUtil.getJSONString(1, "邮箱不能为空！");
//        }
        // 判断邮箱是否有效，有效：map中含有"code"
        Map<String, Object> map = userService.verifyEmail(email);
        if(!map.containsKey("code")){
            return CommunityUtil.getJSONString(2, "用户名不存在或者用户未激活！");
        }else{
            String code = (String)map.get("code");
            // 将验证码保存于session中用于验证
            session.setAttribute("verifyCode", code);
            session.setAttribute("email", email); // 防止有人用a邮件获得了一个正确的验证码，但是之后却想改b的密码
            return CommunityUtil.getJSONString(0);
        }
    }

    @RequestMapping(value = "/forget/resetPassword", method = RequestMethod.POST)
    public String resetPassword(Model model, String email, String verifyCode, String password, HttpSession session){
        String rightCode = (String)session.getAttribute("verifyCode");
        String rightEmail = (String)session.getAttribute("email");
        // 判断验证码
        if(StringUtils.isBlank(verifyCode) || StringUtils.isBlank(rightCode) || !rightCode.equals(verifyCode)){
            model.addAttribute("codeMsg", "验证码错误！");
            return "/site/forget";
        }
        // 判断邮箱
        if(StringUtils.isBlank(email) || StringUtils.isBlank(rightEmail) || !email.equals(rightEmail)){
            model.addAttribute("emailMsg", "邮箱错误！");
            return "/site/forget";
        }
        // 修改密码
        Map<String, Object> map = userService.resetPassword(email, password);
        if(map.containsKey("passwordMsg")){
            model.addAttribute("passwordMsg", "密码不能为空！");
            return "/site/forget";
        }else{
            model.addAttribute("msg", "密码已重制成功，可以正常使用了！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        }

    }



}
