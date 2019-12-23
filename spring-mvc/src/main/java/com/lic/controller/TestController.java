package com.lic.controller;

import com.lic.entity.MyBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

	@RequestMapping("sayHello")
	@ResponseBody
	public ModelAndView sayHello(int age, String name) {
		ModelAndView mav = new ModelAndView();
		MyBean bean = new MyBean(age, name);
		System.out.println("**************** 控制层sayHello1()方法已执行 ****************");
		mav.addObject("myBean", bean);
		mav.setViewName("sayHello");
		System.out.println(bean.toString());
		return mav;
	}

	@RequestMapping("sayHello2")
	@ResponseBody
	public ModelAndView sayHello2(int age, String name) {
		ModelAndView mav = new ModelAndView();
		MyBean bean = new MyBean(age, name);
		System.out.println("**************** 控制层sayHello2()方法已执行 ****************");
		mav.addObject("myBean", bean);
		mav.setViewName("sayHello");
		System.out.println(bean.toString());
		return mav;
	}
}