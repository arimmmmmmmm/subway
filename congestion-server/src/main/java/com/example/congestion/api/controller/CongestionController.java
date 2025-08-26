package com.example.congestion.api.controller;

import com.example.congestion.api.dto.*;
import com.example.congestion.service.*;
import com.example.congestion.domain.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController //메서드가 반환하는 데이터를 body로 바로 보냄 
@RequestMapping("/api")
@RequiredArgsConstructor
public class CongestionController {
	private final CongestionService service;
	
	//esp가 호출
	@PostMapping("/congestion")
	//@RequestBody: 요청의 본문에 있는 데이터를 읽어 congestionrequest 객체로 자동 변환
	//responseentity: 응답의 형태를 정의하는 객체. 단순 데이터만이 아닌 응답상태와 주소를 함께 보낼 수 있음 
	public ResponseEntity<?> receive(@Valid @RequestBody CongestionRequest req) {
		Long id = service.save(req); //save 메서드는 데베에 저장 후 고유 id를 반환 
		return ResponseEntity.created(URI.create("/api/congestion/"+id)).build();
		//.created(..): http 상태 코드 201 created를 보냄
		//"/api/congestion/"+id: 응답과 함께 새로 만들어진 데이터의 주소를 알려줌
		//.build(): 지금까지 만든 응답을 최종 완성해 클라이언트에게 보냄 
	}
	
	//오늘 시간대 별 low/medium/high 건수
	@GetMapping("/stats/today")
	public Object today() {
		return service.statsToday();
	}
	
	@GetMapping("/stats/week")
	public Object week() {
		return service.statsWeekAvg();
	}
	
	@GetMapping("/{stationId}")
	public ResponseEntity<CongestionLog> getLatestCongestion(@PathVariable String stationId) {
		CongestionLog log = service.getLatestByStation(stationId);
		if(log == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(log);
	}
}
