package com.tarena.constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CacheConf {

  /**
   * 超时时间，单位“秒”<br>
   * 默认300秒
   * 
   * @return
   */
  int expire() default 300;

  /**
   * 缓存归属组
   * 
   * @return
   */
  String group() default "";
}
