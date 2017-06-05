package ru.excbt.datafuse.nmk.web.rest.support;

import ru.excbt.datafuse.nmk.data.domain.AuditableTools;
import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.web.api.WebApiHelper;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Auditable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.*;

/**
 * Базовый класс для контроллера
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.04.2015
 *
 */
public abstract class AbstractApiResource {


	@Autowired
	protected ModelMapper modelMapper;

    /**
	 *
	 * @param srcList
	 * @param destClass
	 * @return
	 */
	protected <S, M> List<M> makeModelMapper(Collection<S> srcList, Class<M> destClass) {
		checkNotNull(srcList);
		return srcList.stream().map((i) -> modelMapper.map(i, destClass)).collect(Collectors.toList());
	}

	/**
	 *
	 * @param srcStream
	 * @param destClass
	 * @return
	 */
	protected <S, M> List<M> makeModelMapper(Stream<S> srcStream, Class<M> destClass) {
		checkNotNull(srcStream);
		return srcStream.map((i) -> modelMapper.map(i, destClass)).collect(Collectors.toList());
	}

}
