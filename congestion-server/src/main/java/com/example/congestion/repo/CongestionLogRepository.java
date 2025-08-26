package com.example.congestion.repo;

import com.example.congestion.domain.CongestionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CongestionLogRepository extends JpaRepository<CongestionLog, Long> {
	//CongetstionLog=엔티티 클래스, Long=엔티티의 기본 키 타입 
	List<CongestionLog> findByMeasuredAtBetween(LocalDateTime start, LocalDateTime end);
	//이 메서드는 CongestionLog 엔티티들 중에서 measuredAt 필드 값이 start~end 시간 사이에 있는 모든 데이터를 찾아 list 형태로 반환 
	Optional<CongestionLog> findTopByStationIdOrderByMeasuredAtDesc(String stationId);
	//stationId를 기준으로 데이터를 찾고, 그 결과를 measuredAt 필드의 내림차순으로 정렬 후, 가장 위에 있는 데이터 하나를 가져와라 
}
