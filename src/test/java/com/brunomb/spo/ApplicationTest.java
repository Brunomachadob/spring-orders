package com.brunomb.spo;

import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application.yml", factory = YamlPropertyLoaderFactory.class)
public class ApplicationTest {
}
