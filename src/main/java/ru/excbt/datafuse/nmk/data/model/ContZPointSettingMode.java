package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "cont_zpoint_setting_mode")
@EntityListeners({AuditingEntityListener.class})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContZPointSettingMode extends
		AbstractAuditableEntity<AuditUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5728833577792967110L;

	@OneToOne(fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "cont_zpoint_id")
	@JsonIgnore
	@NotNull
	private ContZPoint contZPoint;

	@Column(name = "is_primary")
	private boolean primary;

	@Column(name = "setting_mode")
	private String settingMode;

	@Column(name = "md_v_min_chk")
	private boolean md_V_minChk = true;

	@Column(name = "md_v_max_chk")
	private boolean md_V_maxChk = true;

	@Column(name = "md_t_min_chk")
	private boolean md_T_minChk = true;

	@Column(name = "md_t_max_chk")
	private boolean md_T_maxChk = true;

	@Column(name = "md_p_min_chk")
	private boolean md_P_minChk = true;

	@Column(name = "md_p_max_chk")
	private boolean md_P_maxChk = true;

	@Column(name = "wm_m1_chk")
	private boolean wm_M1_chk;

	@Column(name = "wm_m1_min")
	private double wm_M1_min;

	@Column(name = "wm_m1_max")
	private double wm_M1_max;

	@Column(name = "wm_m2_chk")
	private boolean wm_M2_chk;

	@Column(name = "wm_m2_min")
	private double wm_M2_min;

	@Column(name = "wm_m2_max")
	private double wm_M2_max;

	@Column(name = "wm_t1_chk")
	private boolean wm_T1_chk;

	@Column(name = "wm_t1_min")
	private double wm_T1_min;

	@Column(name = "wm_t1_max")
	private double wm_T1_max;

	@Column(name = "wm_t2_chk")
	private boolean wm_T2_chk;

	@Column(name = "wm_t2_min")
	private double wm_T2_min;

	@Column(name = "wm_t2_max")
	private double wm_T2_max;

	@Column(name = "wm_p1_chk")
	private boolean wm_P1_chk;

	@Column(name = "wm_p1_min")
	private double wm_P1_min;

	@Column(name = "wm_p1_max")
	private double wm_P1_max;

	@Column(name = "wm_p2_chk")
	private boolean wm_P2_chk;

	@Column(name = "wm_p2_min")
	private double wm_P2_min;

	@Column(name = "wm_p2_max")
	private double wm_P2_max;

	@Column(name = "wm_delta_q_chk")
	private boolean wm_deltaQ_chk;

	@Column(name = "wm_delta_q_min")
	private double wm_deltaQ_min;

	@Column(name = "wm_delta_q_max")
	private double wm_deltaQ_max;

	@Column(name = "wm_delta_t_chk")
	private boolean wm_deltaT_chk;
	
	@Column(name = "wm_delta_t_min")
	private double wm_deltaT_min;
	
	@Column(name = "wm_delta_t_max")
	private double wm_deltaT_max;

	@Column(name = "leak_night_chk")
	private boolean leak_Night_chk;

	@Column(name = "leak_night")
	private double leak_Night;

	@Column(name = "leak_gush_chk")
	private boolean leak_Gush_chk;

	@Column(name = "leak_gush")
	private double leak_Gush;

	@Column(name = "ov_worktime_chk")
	private boolean ov_Worktime_chk;

	@Column(name = "ov_worktime")
	private String ov_Worktime;

	@Column(name = "ov_overheat_cool_chk")
	private boolean ov_OverheatCool_chk;

	@Column(name = "ov_balance_m_ctrl_chk")
	private boolean ov_BalanceM_ctrl_chk;

	@Column(name = "ov_balance_m_ctrl")
	private double ov_BalanceM_ctrl;

	@Column(name = "ov_delta_m_chk")
	private boolean ov_deltaM_chk;

	//@Column(name = "ov_delta_m_min")
	//private double ov_deltaM_min;
	
	//@Column(name = "wm_delta_m_max")
	//private double ov_deltaM_max;

	
	
	@Version
	private int version;

//	@Column(name="cont_zpoint_id", insertable = false, updatable = false)
//	private Long contZPointId;
	
	public ContZPoint getContZPoint() {
		return contZPoint;
	}

	public void setContZPoint(ContZPoint contZPoint) {
		this.contZPoint = contZPoint;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isMd_V_minChk() {
		return md_V_minChk;
	}

	public void setMd_V_minChk(boolean md_V_minChk) {
		this.md_V_minChk = md_V_minChk;
	}

	public boolean isMd_V_maxChk() {
		return md_V_maxChk;
	}

	public void setMd_V_maxChk(boolean md_V_maxChk) {
		this.md_V_maxChk = md_V_maxChk;
	}

	public boolean isMd_T_minChk() {
		return md_T_minChk;
	}

	public void setMd_T_minChk(boolean md_T_minChk) {
		this.md_T_minChk = md_T_minChk;
	}

	public boolean isMd_T_maxChk() {
		return md_T_maxChk;
	}

	public void setMd_T_maxChk(boolean md_T_maxChk) {
		this.md_T_maxChk = md_T_maxChk;
	}

	public boolean isMd_P_minChk() {
		return md_P_minChk;
	}

	public void setMd_P_minChk(boolean md_P_minChk) {
		this.md_P_minChk = md_P_minChk;
	}

	public boolean isMd_P_maxChk() {
		return md_P_maxChk;
	}

	public void setMd_P_maxChk(boolean md_P_maxChk) {
		this.md_P_maxChk = md_P_maxChk;
	}

	public boolean isWm_M1_chk() {
		return wm_M1_chk;
	}

	public void setWm_M1_chk(boolean wm_M1_chk) {
		this.wm_M1_chk = wm_M1_chk;
	}

	public double getWm_M1_min() {
		return wm_M1_min;
	}

	public void setWm_M1_min(double wm_M1_min) {
		this.wm_M1_min = wm_M1_min;
	}

	public double getWm_M1_max() {
		return wm_M1_max;
	}

	public void setWm_M1_max(double wm_M1_max) {
		this.wm_M1_max = wm_M1_max;
	}

	public boolean isWm_M2_chk() {
		return wm_M2_chk;
	}

	public void setWm_M2_chk(boolean wm_M2_chk) {
		this.wm_M2_chk = wm_M2_chk;
	}

	public double getWm_M2_min() {
		return wm_M2_min;
	}

	public void setWm_M2_min(double wm_M2_min) {
		this.wm_M2_min = wm_M2_min;
	}

	public double getWm_M2_max() {
		return wm_M2_max;
	}

	public void setWm_M2_max(double wm_M2_max) {
		this.wm_M2_max = wm_M2_max;
	}

	public boolean isWm_T1_chk() {
		return wm_T1_chk;
	}

	public void setWm_T1_chk(boolean wm_T1_chk) {
		this.wm_T1_chk = wm_T1_chk;
	}

	public double getWm_T1_min() {
		return wm_T1_min;
	}

	public void setWm_T1_min(double wm_T1_min) {
		this.wm_T1_min = wm_T1_min;
	}

	public double getWm_T1_max() {
		return wm_T1_max;
	}

	public void setWm_T1_max(double wm_T1_max) {
		this.wm_T1_max = wm_T1_max;
	}

	public boolean isWm_T2_chk() {
		return wm_T2_chk;
	}

	public void setWm_T2_chk(boolean wm_T2_chk) {
		this.wm_T2_chk = wm_T2_chk;
	}

	public double getWm_T2_min() {
		return wm_T2_min;
	}

	public void setWm_T2_min(double wm_T2_min) {
		this.wm_T2_min = wm_T2_min;
	}

	public double getWm_T2_max() {
		return wm_T2_max;
	}

	public void setWm_T2_max(double wm_T2_max) {
		this.wm_T2_max = wm_T2_max;
	}

	public boolean isWm_P1_chk() {
		return wm_P1_chk;
	}

	public void setWm_P1_chk(boolean wm_P1_chk) {
		this.wm_P1_chk = wm_P1_chk;
	}

	public double getWm_P1_min() {
		return wm_P1_min;
	}

	public void setWm_P1_min(double wm_P1_min) {
		this.wm_P1_min = wm_P1_min;
	}

	public double getWm_P1_max() {
		return wm_P1_max;
	}

	public void setWm_P1_max(double wm_P1_max) {
		this.wm_P1_max = wm_P1_max;
	}

	public boolean isWm_P2_chk() {
		return wm_P2_chk;
	}

	public void setWm_P2_chk(boolean wm_P2_chk) {
		this.wm_P2_chk = wm_P2_chk;
	}

	public double getWm_P2_min() {
		return wm_P2_min;
	}

	public void setWm_P2_min(double wm_P2_min) {
		this.wm_P2_min = wm_P2_min;
	}

	public double getWm_P2_max() {
		return wm_P2_max;
	}

	public void setWm_P2_max(double wm_P2_max) {
		this.wm_P2_max = wm_P2_max;
	}

	public boolean isWm_deltaQ_chk() {
		return wm_deltaQ_chk;
	}

	public void setWm_deltaQ_chk(boolean wm_deltaQ_chk) {
		this.wm_deltaQ_chk = wm_deltaQ_chk;
	}

	public double getWm_deltaQ_min() {
		return wm_deltaQ_min;
	}

	public void setWm_deltaQ_min(double wm_deltaQ_min) {
		this.wm_deltaQ_min = wm_deltaQ_min;
	}

	public double getWm_deltaQ_max() {
		return wm_deltaQ_max;
	}

	public void setWm_deltaQ_max(double wm_deltaQ_max) {
		this.wm_deltaQ_max = wm_deltaQ_max;
	}

	public boolean isLeak_Night_chk() {
		return leak_Night_chk;
	}

	public void setLeak_Night_chk(boolean leak_Night_chk) {
		this.leak_Night_chk = leak_Night_chk;
	}

	public double getLeak_Night() {
		return leak_Night;
	}

	public void setLeak_Night(double leak_Night) {
		this.leak_Night = leak_Night;
	}

	public boolean isLeak_Gush_chk() {
		return leak_Gush_chk;
	}

	public void setLeak_Gush_chk(boolean leak_Gush_chk) {
		this.leak_Gush_chk = leak_Gush_chk;
	}

	public double getLeak_Gush() {
		return leak_Gush;
	}

	public void setLeak_Gush(double leak_Gush) {
		this.leak_Gush = leak_Gush;
	}

	public boolean isOv_Worktime_chk() {
		return ov_Worktime_chk;
	}

	public void setOv_Worktime_chk(boolean ov_Worktime_chk) {
		this.ov_Worktime_chk = ov_Worktime_chk;
	}

	public String getOv_Worktime() {
		return ov_Worktime;
	}

	public void setOv_Worktime(String ov_Worktime) {
		this.ov_Worktime = ov_Worktime;
	}

	public boolean isOv_OverheatCool_chk() {
		return ov_OverheatCool_chk;
	}

	public void setOv_OverheatCool_chk(boolean ov_OverheatCool_chk) {
		this.ov_OverheatCool_chk = ov_OverheatCool_chk;
	}

	public boolean isOv_BalanceM_ctrl_chk() {
		return ov_BalanceM_ctrl_chk;
	}

	public void setOv_BalanceM_ctrl_chk(boolean ov_BalanceM_ctrl_chk) {
		this.ov_BalanceM_ctrl_chk = ov_BalanceM_ctrl_chk;
	}

	public double getOv_BalanceM_ctrl() {
		return ov_BalanceM_ctrl;
	}

	public void setOv_BalanceM_ctrl(double ov_BalanceM_ctrl) {
		this.ov_BalanceM_ctrl = ov_BalanceM_ctrl;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSettingMode() {
		return settingMode;
	}

	public void setSettingMode(String settingMode) {
		this.settingMode = settingMode;
	}

	public boolean isWm_deltaT_chk() {
		return wm_deltaT_chk;
	}

	public void setWm_deltaT_chk(boolean wm_deltaT_chk) {
		this.wm_deltaT_chk = wm_deltaT_chk;
	}

	public double getWm_deltaT_min() {
		return wm_deltaT_min;
	}

	public void setWm_deltaT_min(double wm_deltaT_min) {
		this.wm_deltaT_min = wm_deltaT_min;
	}

	public double getWm_deltaT_max() {
		return wm_deltaT_max;
	}

	public void setWm_deltaT_max(double wm_deltaT_max) {
		this.wm_deltaT_max = wm_deltaT_max;
	}

	public boolean isOv_deltaM_chk() {
		return ov_deltaM_chk;
	}

	public void setOv_deltaM_chk(boolean ov_deltaM_chk) {
		this.ov_deltaM_chk = ov_deltaM_chk;
	}

//	public Long getContZPointId() {
//		return contZPointId;
//	}
//
//	public void setContZPointId(Long contZPointId) {
//		this.contZPointId = contZPointId;
//	}

}
