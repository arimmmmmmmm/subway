package com.example.congestion.config;

import com.example.congestion.domain.*;
import com.example.congestion.repo.*;
import jakarta.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataLoader {
	private final CongestionLogRepository repo;
	
	@PostConstruct
	public void init() {
		repo.deleteAll();
		
		String[] levels= {"LOW","MEDIUM","HIGH"};
		String[] stations = {"dongguk_univ","chungmuro","myeongdong"};
		Random random= new Random();
		
		for (int day=0; day<7; day++) {
			LocalDateTime baseDate = LocalDateTime.now().minusDays(day);
			
			for(String station: stations) {
				for(int hour=6; hour<=23; hour++) {
					CongestionLog log = new CongestionLog();
					log.setStationId(station);
					log.setLevel(CongestionLevel.valueOf(levels[random.nextInt(levels.length)]));
					log.setDeviceId("esp32-mock");
					log.setMeasuredAt(baseDate.withHour(hour).withMinute(0).withSecond(0));
					repo.save(log);
				}
			}
			
		}
		System.out.println("더미데이터 삽입 완료");
	}

}
