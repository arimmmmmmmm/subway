package com.example.congestion.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "congestion_log",
		indexes = {
				@Index(name="idx_station_time", columnList = "stationId, measuredAt"),
				@Index(name="idx_time", columnList = "measuredAt")
		})
public class CongestionLog {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	//id 필드를 기본키로 설정, 데베가 자동으로 값을 생성하도록 함.
	private Long id;
	
	private String deviceId; //esp의 고유 식별자 
	private String stationId; //데이터 측정한 지하철역의 고유 식별자 
	
	@Enumerated(EnumType.STRING) 
	private CongestionLevel level; //열거형을 데베에 문자열로 저장하도록 설정 
	
	private LocalDateTime measuredAt; //esp가 측정한 시각
	private LocalDateTime receivedAt; //서버 수신 시각
	
	@PrePersist //이게 붙은 메서드는 엔티티가 데베에 저장되기 '전'에 자동으로 실행 
	public void onPersist() {
		if(receivedAt == null) receivedAt = LocalDateTime.now(); 
		if(measuredAt == null) measuredAt = receivedAt;
	}
}
