package com.nagne.carguardplatform.util;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WarningStreamManager {

    private static final List<SseEmitter> emitters =
            new CopyOnWriteArrayList<>();

    // SSE 연결 추가
    public static void add(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
    }

    // SSE 이벤트 전송
    public static void send(
            String eventName,
            String message
    ) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(message));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}