/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.dto;

import javax.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author A.Kovtonyuk 
 * @version 1.0
 * @since 21.02.2017
 * 
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContObjectMeterPeriodSettingsDTO {
	
	@NotNull
	private Long contObjectId;
	
	private Map<String, Long> meterPeriodSettings = new HashMap<>();
	
	public void putSetting(String key, Long value) {
		if (this.meterPeriodSettings == null) {
			this.meterPeriodSettings = new HashMap<>();
		}
		this.meterPeriodSettings.put(key, value);
	}
	
}
