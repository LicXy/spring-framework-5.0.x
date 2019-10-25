package com.ioc.lic2.util;

import com.ioc.lic2.dao.UserDao;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(UserDao.class);
		GenericBeanDefinition beanDefinition = (GenericBeanDefinition) builder.getBeanDefinition();
		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("UserDao");
		beanDefinition.setBeanClass(MyFactoryBean.class);

		registry.registerBeanDefinition("userDao",beanDefinition);
	}
}
