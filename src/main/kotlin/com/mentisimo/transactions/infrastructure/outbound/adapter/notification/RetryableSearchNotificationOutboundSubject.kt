package com.mentisimo.transactions.infrastructure.outbound.adapter.notification

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import com.mentisimo.transactions.domain.model.SearchNotification
import com.mentisimo.transactions.infrastructure.outbound.port.SearchNotificationOutboundSubject
import com.mentisimo.transactions.infrastructure.outbound.port.SearchNotificationSender
import java.util.concurrent.TimeUnit

@Component
class RetryableSearchNotificationOutboundSubject(private val searchNotificationSender: SearchNotificationSender) : SearchNotificationOutboundSubject {

    private val logger = LoggerFactory.getLogger(RetryableSearchNotificationOutboundSubject::class.java)

    private val searchNotificationSubject = PublishSubject.create<SearchNotification>()

    init {
        searchNotificationSubject.subscribe { searchNotification ->
            Observable.just(searchNotification)
                    .map {
                        searchNotificationSender.sendSearchNotification(searchNotification)
                    }
                    .retryWhen(retryWithDelay(5000, 5))
                    .subscribe({
                        logger.info("Search notification has been sent: {}", searchNotification)
                    }, {
                        logger.warn("Cannot send search notification: {}", searchNotification, it)
                    })
        }
    }

    override fun addToQueue(searchNotification: SearchNotification) {
        searchNotificationSubject.onNext(searchNotification)
    }

    private fun retryWithDelay(delayBaseInMillis: Long, times: Int): (t: Observable<Throwable>) -> ObservableSource<*> {
        var delayMultiplier = 0
        return { observable ->
            observable.flatMap { throwable ->
                if (++delayMultiplier < times) {
                    Observable.timer(delayBaseInMillis * delayMultiplier, TimeUnit.MILLISECONDS)
                } else {
                    Observable.error(throwable)
                }
            }
        }
    }
}