/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.dto;

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
public class MeterPeriodSettingDTO {

	private Long id;
	
	private Long subscriberId;

	private String name;

	private String description;

	private Integer fromDay;

	private Integer toDay;

	private Integer valueCount;

	private String valueTypePrefix;

	private int version;

	@Override
	public String toString() {
		return "MeterPeriodSettingDTO [id=" + id + ", subscriberId=" + subscriberId + ", name=" + name
				+ ", description=" + description + ", fromDay=" + fromDay + ", toDay=" + toDay + ", valueCount="
				+ valueCount + ", valueTypePrefix=" + valueTypePrefix + ", version=" + version + "]";
	}	
	
}
