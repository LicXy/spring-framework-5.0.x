package com.lic3.util;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Configurable
@ComponentScan("com.lic3")
@Import(MyImportSelector.class)
public class AppConfig {
}
