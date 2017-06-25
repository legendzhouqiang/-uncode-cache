package cn.uncode.cache.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by KevinBlandy on 2017/2/28 14:00
 */
@SpringBootApplication
@ComponentScan({"cn.uncode.cache"})
@EnableAspectJAutoProxy
public class Application {
	public static void main(String[] agrs){
		SpringApplication.run(Application.class,agrs);
	}
}
