package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    // 用户注册
    public Map<String, Object> register(User user) {
        // map是用来存储报错信息的：<报错信息类型，报错信息内容>
        Map<String, Object> map = new HashMap<>();

        // 首先对空值进行判断处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证用户名是否已经存在
        User u = userMapper.selectByName(user.getUsername());
        if (null != u) {
            map.put("usernameMsg", "该用户名已存在");
            return map;
        }

        // 验证邮箱是否已经存在
        u = userMapper.selectByEmail(user.getEmail());
        if (null != u) {
            map.put("emailMsg", "该邮箱已经被使用");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5)); // 设置加密字符串
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件：activation.html
        Context context = new Context(); // thymeleaf在spring中的内容对象
        context.setVariable("email", user.getEmail());
        // 设置url：http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "账号激活", content);

        return map;
    }

    // 激活用户的账号
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    // 用户登录
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (null == user) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }

        // 验证账号状态
        if (user.getStatus() == 0) { // 注册了但是没有激活
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    // 用户退出
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    // 通过ticket查询login_ticket表中的ticket对象
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    // 更新用户头像
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    // 修改密码
    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword){
        Map<String, Object> map = new HashMap<>();

        // 空值判断
        if(StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordMsg", "原密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg", "新密码不能为空！");
            return map;
        }

        // 判断旧密码是否正确
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            map.put("oldPasswordMsg", "原密码错误！");
            return map;
        }

        // 更新新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);

        return map;
    }

    // 验证用户的邮箱是否有效，存在则发送含有验证码的邮件
    public Map<String, Object> verifyEmail(String email){
        User user = userMapper.selectByEmail(email);
        Map<String, Object> map = new HashMap<>();
        // 验证用户是否存在，以及是否已经激活
        if(user == null || user.getStatus() == 0){
            return map;
        }
        // 用户存在
        // 发送带有验证码的邮件：
        Context context = new Context(); // thymeleaf在spring中的内容对象
        context.setVariable("email", email);
        String code = CommunityUtil.generateUUID().substring(0, 6);
        context.setVariable("code", code);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "找回密码", content);
        // 将code保存
        map.put("code", code);

        return map;
    }

    // 重制密码
    public Map<String, Object> resetPassword(String email, String password){
        Map<String, Object> map = new HashMap<>();
        // 判断密码是否为空
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不得为空！");
        }
        // 重制密码
        User user = userMapper.selectByEmail(email);
        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(user.getId(), password);

        return map;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
}
