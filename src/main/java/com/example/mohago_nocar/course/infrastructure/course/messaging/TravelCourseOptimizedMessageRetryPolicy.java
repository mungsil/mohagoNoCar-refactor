package com.example.mohago_nocar.course.infrastructure.course.messaging;

import com.example.mohago_nocar.global.common.RetryPolicy;
import com.example.mohago_nocar.transit.infrastructure.error.exception.ODsayRouteException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletionException;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelCourseOptimizedMessageRetryPolicy implements RetryPolicy {

    @Override
    public boolean isRetryable(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException ||
                throwable instanceof ConnectException ||
                throwable instanceof HttpTimeoutException){
            return true;
        }

        if (throwable instanceof TransientDataAccessException ||
                throwable instanceof DataAccessResourceFailureException){
            return true;
        }

        if (throwable instanceof CompletionException exception) {
            if (exception.getCause() instanceof ODsayRouteException odsayException) {
                return odsayException.getErrorCode().isTooManyRequests() ||
                        odsayException.getErrorCode().isServerError();
            }
        }

        // todo Completion Exception 안에 OdsayRouteException이 있을 때 아래로 안잡히는지 테스트
        if (throwable instanceof ODsayRouteException exception){
            return exception.getErrorCode().isTooManyRequests() ||
                    exception.getErrorCode().isServerError();
        }

        return false;
    }

}
