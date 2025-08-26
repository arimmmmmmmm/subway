package com.example.congestion.service;

import com.example.congestion.api.dto.*;
import com.example.congestion.domain.*;
import com.example.congestion.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CongestionService {
	private final CongestionLogRepository repo;
	
	public Long save(CongestionRequest req) {
		var log = CongestionLog.builder().
				deviceId(req.deviceId()).
				stationId(req.stationId()).
				level(req.level()).
				measuredAt(req.timestamp()).
				build();
		
		return repo.save(log).getId(); //save() 메서드는 저장된 객체 자체를 반환 
	}
	
	public Map<Integer,Map<String,Long>> statsToday() {
		LocalDate today = LocalDate.now();
		LocalDateTime start = today.atStartOfDay(); //오늘 날짜의 자정을 시작 시점으로 설정 
		LocalDateTime end = today.atTime(LocalTime.MAX); //오늘 날짜의 가장 마지막 시간을 끝 시점으로 설정 
		
		var list = repo.findByMeasuredAtBetween(start, end); //오늘 하루 동안 기록된 모든 혼잡도 로그를 데베에서 조회 
		
		//hour -> level -> count
		Map<Integer, Map<String, Long>> result = new TreeMap<>(); //treemap: 키를 기준으로 오름차순 정렬 
		//Integer: 시간을 나타내는 키, string: 혼잡도 레벨, long: 특정 레벨이 몇개 있는지 
		for (int h=0; h<24; h++) {
			//0시부터 23시까지 각각에 대해 초기화된 map을 미리 생성해 result에 넣어줌 
			result.put(h,  new HashMap<>(Map.of("LOW",0L,"MEDIUM",0L,"HIGH",0L)));
			//모든 레벨의 개수는 0개로. 0뒤에 붙은 L은 long타입을 의미 
		}
		list.forEach(log -> {
			int hour = log.getMeasuredAt().getHour(); //로그가 기록된 시간 가져옴 
			var bucket = result.get(hour); //result 맵에서 hour 키에 해당하는 값을 가져와 변수에 할당 
			bucket.put(log.getLevel().name(), bucket.get(log.getLevel().name()) + 1);
		});
		return result;
	}
	
	public Map<String, Double> statsWeekAvg() {
		LocalDateTime end = LocalDateTime.now();
		LocalDateTime start = end.minusDays(7);
		
		var list = repo.findByMeasuredAtBetween(start,end);
		
		Map<String, List<Integer>> acc = new LinkedHashMap<>();
		//string: 요일, list<Integer>: 그 요일에 해당하는 혼잡도 점수 리스트
		//linkedhashmap: 데이터를 삽입된 순서대로 유지하는 맵 
		 acc.put("MON", new ArrayList<>()); acc.put("TUE", new ArrayList<>());
	     acc.put("WED", new ArrayList<>()); acc.put("THU", new ArrayList<>());
	     acc.put("FRI", new ArrayList<>()); acc.put("SAT", new ArrayList<>());
	     acc.put("SUN", new ArrayList<>());
	     
	     list.forEach(log -> {
	    	 String key = log.getMeasuredAt().getDayOfWeek().name().substring(0,3);
	    	 int score = switch(log.getLevel()) {
	    	 case LOW ->1; case MEDIUM ->2; case HIGH ->3;
	    	 };
	    	 acc.get(key).add(score);
	     });
	     
	     Map<String, Double> avg = new LinkedHashMap<>();
	     acc.forEach((k,v) -> {
	    	 double a = v.isEmpty() ? 0.0 : v.stream().mapToInt(i->i).average().orElse(0.0);
	    	 //v.strea(): 점수 리스트 v를 스트림으로 만든다.
	    	 //.mapToInt(i->i): 스트림 요소들을 integer에서 int타입으로 변환
	    	 //.average(): int 스트림의 평균을 계산, .orElse(0.0): 어떤 이유로든 계산 안되면 0.0 반환 
	    	 avg.put(k, a);
	     });
	     return avg;
	}
	
	public CongestionLog getLatestByStation(String stationId) {
		return repo.findTopByStationIdOrderByMeasuredAtDesc(stationId).orElse(null);
	}
}
