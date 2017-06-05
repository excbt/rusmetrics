package ru.excbt.datafuse.nmk.web.rest.support;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
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
