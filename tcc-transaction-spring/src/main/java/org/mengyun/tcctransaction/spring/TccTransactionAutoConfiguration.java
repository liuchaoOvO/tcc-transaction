package org.mengyun.tcctransaction.spring;

import org.mengyun.tcctransaction.recover.TransactionRecovery;
import org.mengyun.tcctransaction.spring.recover.RecoverScheduledJob;
import org.mengyun.tcctransaction.spring.support.SpringBeanFactory;
import org.mengyun.tcctransaction.spring.support.SpringTransactionConfigurator;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TccTransactionAutoConfiguration {

    @Bean
    public SpringBeanFactory beanFactory() {
        return new SpringBeanFactory();
    }

    @Bean(initMethod = "init")
    public SpringTransactionConfigurator transactionConfigurator() {
        return new SpringTransactionConfigurator();
    }

    @Bean(initMethod = "init")
    public ConfigurableTransactionAspect compensableTransactionAspect(SpringTransactionConfigurator transactionConfigurator) {
        ConfigurableTransactionAspect configurableTransactionAspect = new ConfigurableTransactionAspect();
        configurableTransactionAspect.setTransactionConfigurator(transactionConfigurator);
        return configurableTransactionAspect;
    }

    @Bean(initMethod = "init")
    public ConfigurableCoordinatorAspect resourceCoordinatorAspect(SpringTransactionConfigurator transactionConfigurator) {
        ConfigurableCoordinatorAspect resourceAspect = new ConfigurableCoordinatorAspect();
        resourceAspect.setTransactionConfigurator(transactionConfigurator);
        return resourceAspect;
    }

    @Bean
    public TransactionRecovery transactionRecovery(TransactionConfigurator transactionConfigurator) {
        TransactionRecovery transactionRecovery = new TransactionRecovery();
        transactionRecovery.setTransactionConfigurator(transactionConfigurator);
        return transactionRecovery;
    }

    @Bean
    public SchedulerFactoryBean recoverScheduler() {
        return new SchedulerFactoryBean();
    }

    @Bean(initMethod = "init")
    public RecoverScheduledJob recoverScheduledJob(TransactionRecovery transactionRecovery,
                                                   TransactionConfigurator transactionConfigurator,
                                                   SchedulerFactoryBean recoverScheduler) {

        RecoverScheduledJob recoverScheduledJob = new RecoverScheduledJob();
        recoverScheduledJob.setScheduler(recoverScheduler.getObject());
        recoverScheduledJob.setTransactionConfigurator(transactionConfigurator);
        recoverScheduledJob.setTransactionRecovery(transactionRecovery);

        return recoverScheduledJob;

    }


}
