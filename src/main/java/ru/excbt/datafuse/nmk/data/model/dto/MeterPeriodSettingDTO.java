/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.dto;

import ru.excbt.datafuse.nmk.data.domain.ModelIdable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author A.Kovtonyuk 
 * @version 1.0
 * @since 20.02.2017
 * 
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeterPeriodSettingDTO implements ModelIdable<Long> {

	private Long id;
	
//	private Long subscriberId;

	private String name;

	private String description;

	private Integer fromDay;

	private Integer toDay;

	private Integer valueCount;

	private String valueTypePrefix;

	private int version;

	public MeterPeriodSettingDTO(MeterPeriodSettingDTO src) {
		this.id = src.id;
		this.name = src.name;
		this.description = src.description;
		this.fromDay = src.fromDay;
		this.toDay = src.toDay;
		this.valueCount = src.valueCount;
		this.valueCount = src.valueCount;
		this.valueTypePrefix = src.valueTypePrefix;
	}
	
	@Override
	public String toString() {
		return "MeterPeriodSettingDTO [id=" + id + ", name=" + name
				+ ", description=" + description + ", fromDay=" + fromDay + ", toDay=" + toDay + ", valueCount="
				+ valueCount + ", valueTypePrefix=" + valueTypePrefix + ", version=" + version + "]";
	}	
	
}
