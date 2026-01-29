package com.example.mohago_nocar.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StackTraceExtractor {

    private final ObjectMapperUtil objectMapperUtil;

    public String extractStackTrace(Throwable throwable, int length) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(length, stackTrace.length); i++) {
            sb.append(stackTrace[i].toString()).append("\n");
        }

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("errorMessage", throwable.getMessage());
        payloadMap.put("stackTrace", sb.toString());

        return objectMapperUtil.writeValue(payloadMap);
    }

}
