package com.joy.app;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.joy.app.cronJobs.Background;

/**
 * 
 * @author joy
 *
 */

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableMongoRepositories(basePackages="com.joy.app.repository")
public class SnmpApplication  implements SchedulingConfigurer{

	
	/**
	 * @param args 
	 */
	public static void main(String[] args) {
		SpringApplication.run(SnmpApplication.class, args);
		
	}
	
	/**
	 * Initialization for Scheduled Threads
	 * 
	 */

	@Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
    	
        return Executors.newScheduledThreadPool(200);
    }
	
}
