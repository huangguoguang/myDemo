/**
 * 
 */
package com.tarena.common.component.session.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class Name: SessionProvider<br>
 * Description:
 *
 * @author lilin
 * @version 1.0
 */
@Target({ ElementType.PARAMETER })//参数声明
/*用来说明该注解类的生命周期,它有以下三个参数：
RetentionPolicy.SOURCE  : 注解只保留在源文件中
RetentionPolicy.CLASS  : 注解保留在class文件中，在加载到JVM虚拟机时丢弃
RetentionPolicy.RUNTIME  : 注解保留在程序运行期间，此时可以通过反射获得定义某个类上的在所有注解。*/
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Session {
	
	/**
	 * 是否需要强制创建Session，默认强制
	 * @return
	 */
	boolean create() default true;
}
