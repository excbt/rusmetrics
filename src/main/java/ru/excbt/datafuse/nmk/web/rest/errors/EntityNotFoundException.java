package ru.excbt.datafuse.nmk.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class EntityNotFoundException extends AbstractThrowableProblem {

    public EntityNotFoundException(Class<?> clazz, Long id) {
        super(ErrorConstants.DEFAULT_TYPE, "Entity " + clazz.getSimpleName() + " with id=" +id + " is not found", Status.BAD_REQUEST);
    }

    public EntityNotFoundException(Class<?> clazz, String keyname) {
        super(ErrorConstants.DEFAULT_TYPE, "Entity " + clazz.getSimpleName() + " with keyname=" +keyname + " is not found", Status.BAD_REQUEST);
    }

}
