package com.nagne.carguardplatform.controller;

import com.nagne.carguardplatform.dto.WarningDto;
import com.nagne.carguardplatform.service.WarningService;
import com.nagne.carguardplatform.util.WarningStreamManager;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.nagne.carguardplatform.dto.LocationDto;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/warning")
@RequiredArgsConstructor
public class WarningController {

    private final WarningService warningService;

    @PostMapping
    public ResponseEntity<String> issueWarning(@RequestBody WarningDto dto) {
        warningService.issueWarning(dto);
        return ResponseEntity.ok("경고 기록 완료");
    }

    @GetMapping
    public ResponseEntity<List<WarningDto>> findAll() {
        return ResponseEntity.ok(warningService.findAllWarnings());
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 유지
        WarningStreamManager.add(emitter);

        // 💡 초기 연결 시 dummy 데이터 한 번 보내기 (연결 테스트용)
        try {
            emitter.send(SseEmitter.event().name("connect").data("📡 연결됨"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @GetMapping("/filter")
    public ResponseEntity<List<WarningDto>> filterWarnings(
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(warningService.filterWarnings(plateNumber, location, date));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<String> confirmWarning(@PathVariable Long id) {
        warningService.confirmWarning(id);
        return ResponseEntity.ok("확인 처리 완료");
    }

    @PostMapping("/location")
    public ResponseEntity<String> updateLocation(
            @RequestBody LocationDto dto
    ) {

        warningService.updateTrackingLocation(
                dto.getBleId(),
                dto.getLocation()
        );

        return ResponseEntity.ok(
                "미등록 차량 위치 갱신 완료"
        );
    }
}
