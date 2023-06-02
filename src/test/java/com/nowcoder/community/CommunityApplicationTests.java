package com.nowcoder.community;

import com.nowcoder.community.config.AlphaConfig;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.AlphaDaoHibernateImpl;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	// applicationContext其实就是Spring容器，设置这个变量用于记录Spring容器
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// 当程序启动的时候，容器会被自动创建出来（也就是形参中的applicationContext），我们将其记录下来供我们使用
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);

		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		AlphaDao alphaDaoHibernateImpl = applicationContext.getBean("alphaDaoHibernateImpl", AlphaDao.class);
		System.out.println(alphaDaoHibernateImpl.select());
	}

	@Test
	public void testBeanManagement(){
		AlphaService bean = applicationContext.getBean(AlphaService.class);
		System.out.println(bean);
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat bean = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(bean.format(new Date()));
	}

	@Autowired
	@Qualifier("alphaDaoHibernateImpl")
	private AlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;

	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
	}


}
