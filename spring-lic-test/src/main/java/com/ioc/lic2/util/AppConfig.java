package com.ioc.lic2.util;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Configurable
@ComponentScan("com.ioc.lic2")
@Import(MyImportBeanDefinitionRegistrar.class)
public class AppConfig {
}
