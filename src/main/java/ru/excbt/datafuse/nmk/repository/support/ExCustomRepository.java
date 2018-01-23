package ru.excbt.datafuse.nmk.repository.support;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface ExCustomRepository <T, P extends EntityPathBase<T>, ID extends Serializable>
    extends JpaRepository<T, ID>, QueryDslPredicateExecutor<T>, QuerydslBinderCustomizer<P> {

    @Override
    default void customize(QuerydslBindings bindings, P root) {
    }

}
