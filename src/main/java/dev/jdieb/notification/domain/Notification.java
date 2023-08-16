package dev.jdieb.notification.domain;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Component
@Data
public class Notification {
	
	@NotNull
	private long matricula;
	
	@NotNull
	private long chatId;
	
	@NotBlank
	private String aluno;
	
	@NotBlank
	private String cartao;
	
	@NotNull
	@Min(1)
	@Max(50)
	private int creditos;
	
	@JsonProperty(access = Access.READ_ONLY)
	private int status;
	
	@JsonProperty(access = Access.READ_ONLY)
	private String ultimaRecarga;
	

	@JsonProperty(access = Access.READ_ONLY)
	private long lastTime;

	
}
