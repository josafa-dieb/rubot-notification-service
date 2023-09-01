package dev.jdieb.notification.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HistoricoRecargaDTO {

	@JsonProperty(value = "Date")
	private String Date;
	@JsonProperty(value = "Operation")
	private String Operation;
	@JsonProperty(value = "CurrentValue")
	private int CurrentValue;
	@JsonProperty(value = "RechargedValue")
	private int RechargedValue;
	@JsonProperty(value = "LastValue")
	private int LastValue;

}
