package com.jasonmaggard.smart_api.api.jobs.config;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JobRunrConfig {
    
    @Bean
    public StorageProvider storageProvider(DataSource dataSource) {
        return SqlStorageProviderFactory.using(dataSource);
    }
    
    @Bean
    public org.jobrunr.configuration.JobRunrConfiguration.JobRunrConfigurationResult initJobRunr(
            StorageProvider storageProvider, 
            ApplicationContext applicationContext,
            JobMapper jobMapper) {
        return JobRunr.configure()
                .useStorageProvider(storageProvider)
                .useJobActivator(applicationContext::getBean)
                .useBackgroundJobServer(2) // Limit to 2 concurrent workers to avoid API rate limits
                .initialize();
    }
}
