package com.example.backend.dto.erro;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ApiError(
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
		Instant timestamp,

		String status,
		
		String error,
		
		List<String> message,
		
		String path
		) {

}
