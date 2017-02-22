/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.dto;

import javax.validation.constraints.Min;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContObjectMeterPeriodSettingsDTO {
	
	@JsonInclude(value = Include.NON_NULL)
	@Min(0)
	private Long contObjectId;

	@JsonInclude(value = Include.NON_NULL)
	private List<Long> contObjectIds;
	
	private Map<String, Long> meterPeriodSettings = new HashMap<>();
	
	@JsonInclude(value = Include.NON_NULL)
	private Boolean replace;
	
	public ContObjectMeterPeriodSettingsDTO contObjectId(Long id) {
		this.contObjectId = id;
		return this;
	}

	public ContObjectMeterPeriodSettingsDTO contObjectIds(List<Long> ids) {
		if (contObjectIds == null) {
			contObjectIds = new ArrayList<>();
		} else {
			contObjectIds.clear();
		}
		this.contObjectIds.addAll(ids);
		return this;
	}
	
	public ContObjectMeterPeriodSettingsDTO putSetting(String key, Long value) {
		if (this.meterPeriodSettings == null) {
			this.meterPeriodSettings = new HashMap<>();
		}
		this.meterPeriodSettings.put(key, value);
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public boolean validateContObjectIds() {
		return isSingle() ^ isMulti();
//		(contObjectId != null && (contObjectIds == null || contObjectIds.isEmpty())) ||
//			   (contObjectId == null && contObjectIds != null && !contObjectIds.isEmpty()); 	
	}
	
	@JsonIgnore
	public boolean isSingle() {
		return contObjectId != null && (contObjectIds == null || contObjectIds.isEmpty());
	}

	@JsonIgnore
	public boolean isMulti() {
		return (contObjectId == null && contObjectIds != null && !contObjectIds.isEmpty());
	}
}
