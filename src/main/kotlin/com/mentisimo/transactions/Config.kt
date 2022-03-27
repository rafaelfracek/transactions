package com.mentisimo.transactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableAsync
class Config : AsyncConfigurer {

    @Bean
    override fun getAsyncExecutor(): ThreadPoolTaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = Runtime.getRuntime().availableProcessors() - 1
        return threadPoolTaskScheduler
    }
}
