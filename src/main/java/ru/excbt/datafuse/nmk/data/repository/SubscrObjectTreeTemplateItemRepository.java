package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplateItem;

public interface SubscrObjectTreeTemplateItemRepository extends CrudRepository<SubscrObjectTreeTemplateItem, Long> {

	@Query("SELECT i FROM SubscrObjectTreeTemplateItem i WHERE i.templateId = :templateId ORDER BY itemLevel NULLS LAST")
	public List<SubscrObjectTreeTemplateItem> selectTemplateItems(@Param("templateId") Long templateId);

}
