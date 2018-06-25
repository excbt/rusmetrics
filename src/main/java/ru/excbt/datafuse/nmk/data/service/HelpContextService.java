package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.HelpContext;
import ru.excbt.datafuse.nmk.data.repository.HelpContextRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Сервис для работы со справкой
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.02.2016
 *
 */
@Service
public class HelpContextService implements SecuredRoles {

	private final static String HELP_CONTEXT_SETUP = "HELP_CONTEXT_SETUP";
	private final static String HELP_CONTEXT_DEFAULT = "HELP_CONTEXT_DEFAULT";

	@Autowired
	private HelpContextRepository helpContextRepository;

	@Autowired
	private SystemParamService systemParamService;

    /**
     *
     * @param anchorId
     * @return
     */
	@Transactional( readOnly = true)
	public HelpContext findByAnchorId(String anchorId) {
		List<HelpContext> preResult = helpContextRepository.findByAnchorId(anchorId);
		return preResult.isEmpty() ? null : preResult.get(0);
	}

	/**
	 *
	 * @param anchorId
	 * @return
	 */
	@Transactional
	public HelpContext createByAnchorId(String anchorId) {
		checkNotNull(anchorId);
		HelpContext newRecord = new HelpContext();
		newRecord.setAnchorId(anchorId);
		helpContextRepository.save(newRecord);
		return newRecord;
	}

	/**
	 *
	 * @param anchorId
	 * @return
	 */
	@Transactional
	public HelpContext getHelpContext(String anchorId) {
		HelpContext result = findByAnchorId(anchorId);
		if (result == null) {

			try {

				if (isHelpContextSetup()) {
					result = createByAnchorId(anchorId);
				}

			} catch (Exception e) {
				result = findByAnchorId(anchorId);
			}
		}
		return result;
	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public boolean isHelpContextSetup() {
		try {
			boolean helpContextSetup = systemParamService.getParamValueAsBoolean(HELP_CONTEXT_SETUP);
			return helpContextSetup;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public String getHelpContextDefault() {
		String helpContextSetup = systemParamService.getParamValueAsString(HELP_CONTEXT_DEFAULT);
		return helpContextSetup;
	}

	/**
	 *
	 * @param helpContext
	 * @return
	 */
	@Secured({ ROLE_ADMIN })
	@Transactional
	public HelpContext saveHelpContext(HelpContext helpContext) {

		checkNotNull(helpContext);
		checkArgument(!helpContext.isNew());

		HelpContext currHelpContext = helpContextRepository.findById(helpContext.getId())
            .orElseThrow(() -> new EntityNotFoundException(HelpContext.class, helpContext.getId()));

		currHelpContext.setHelpUrl(helpContext.getHelpUrl());
		return helpContextRepository.save(currHelpContext);
	}

}
