package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
}
