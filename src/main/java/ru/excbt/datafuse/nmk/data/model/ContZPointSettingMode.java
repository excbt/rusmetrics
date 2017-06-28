package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Настройка параметров контроля точки учета: по режимам
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 01.04.2015
 *
 */
@Entity
@Table(name = "cont_zpoint_setting_mode")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class ContZPointSettingMode extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 5728833577792967110L;

	@OneToOne(fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "cont_zpoint_id", updatable = false)
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

	// @Column(name = "ov_delta_m_min")
	// private double ov_deltaM_min;

	// @Column(name = "wm_delta_m_max")
	// private double ov_deltaM_max;

	@Version
	private int version;

	// @Column(name="cont_zpoint_id", insertable = false, updatable = false)
	// private Long contZPointId;

}
