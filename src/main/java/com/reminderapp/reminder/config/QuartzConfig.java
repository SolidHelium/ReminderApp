package com.reminderapp.reminder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJobFactory(springBeanJobFactory());
        factory.setQuartzProperties(quartzProperties());
        factory.setWaitForJobsToCompleteOnShutdown(true);
        return factory;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        return new SpringBeanJobFactory();
    }

    //TODO: Set properties in application.yml as normal people usually do

    private Properties quartzProperties() {
        Properties properties = new Properties();
        properties.put("org.quartz.scheduler.instanceName"      , "ReminderScheduler"                               );
        properties.put("org.quartz.scheduler.instanceId"        , "AUTO"                                            );
        //properties.put("org.quartz.jobStore.class"            , "org.quartz.impl.jdbcjobstore.JobStoreTX"         );
        properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate" );
        properties.put("org.quartz.jobStore.tablePrefix"        , "QRTZ_"                                           );
        properties.put("org.quartz.jobStore.isClustered"        , "false"                                           );
        properties.put("org.quartz.threadPool.threadCount"      , "10"                                              );
        return properties;
    }
}
