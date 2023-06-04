package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }

    @RequestMapping("data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

//  Get请求
    // http://localhost:8080/alpha/students?current=2&limit=30
    @RequestMapping(value = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current",required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit",required = false, defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "students; get parameters 1";
    }

    // http://localhost:8080/alpha/student/20
    @RequestMapping(value = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "student; get parameters 2";
    }

// Post请求
    @RequestMapping(value = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

// 响应HTML数据的两种方法
    @RequestMapping("/teacher")
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "zhangsan");
        modelAndView.addObject("age", 30);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(value = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name", "beijingdaxue");
        model.addAttribute("age", "120");
        return "/demo/view";
    }

// 响应JSON数据（异步请求）
// Java对象 -> JSON字符串 -> JS对象（或者任何语言的对象）；在跨语言环境下，JSON是一种非常常用的一种形式
// @ResponseBody其实返回的就是JSON字符串

    @RequestMapping(value = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "zhangsan");
        emp.put("age", 23);
        emp.put("salary", 8000.0);
        return emp;
    }

    @RequestMapping(value = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> emps = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "zhangsan");
        emp.put("age", 23);
        emp.put("salary", 8000.0);
        emps.add(emp);

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("name", "lisi");
        emp2.put("age", 24);
        emp2.put("salary", 8500.0);
        emps.add(emp2);

        return emps;
    }

    @RequestMapping(value = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    // 测试cooikie 服务器->浏览器
    public String setCookie(HttpServletResponse response){
        // 创建cookie
        Cookie cookie = new Cookie("cookie", CommunityUtil.generateUUID());
        // 设置cookie的生效范围，单位是秒
        cookie.setPath("/community/alpha");
        // 设置cookie的生效时间
        cookie.setMaxAge(60*10);
        // 发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(value = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    // 测试cooikie 浏览器->服务器
    public String getCookie(@CookieValue("cookie") String cookie){
        System.out.println(cookie);
        return "get cookie";
    }

    @RequestMapping(value = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    // 测试session set
    public String setSession(HttpSession session){
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @RequestMapping(value = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    // 测试session get
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }


}
