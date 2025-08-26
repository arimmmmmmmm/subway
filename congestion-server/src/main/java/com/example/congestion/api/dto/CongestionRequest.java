package com.example.congestion.api.dto;

import com.example.congestion.domain.CongestionLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CongestionRequest( 
	//notblank: 필드에 공백이 아닌 문자열이 반드시 포함되어야 함을 검증 
	@NotBlank String deviceId,
	@NotBlank String stationId,
	//notnull: 필드에 null 값이 들어오면 안됨을 검증
	@NotNull CongestionLevel level,
	LocalDateTime timestamp //없으면 서버가 수신시각으로 대체
) {}
