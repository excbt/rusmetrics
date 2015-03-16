package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.service.UDirectoryNodeService;

@Controller
@RequestMapping(value = "/api/uDirectory")
public class UDirectoryNodeController {

	@Autowired
	private UDirectoryNodeService directoryNodeService;
	
	@RequestMapping(value = "/node/{id}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll(@PathVariable("id") long nodeDirectoryId) {
		UDirectoryNode result = directoryNodeService.getRootNode(nodeDirectoryId); 
		return ResponseEntity.ok(result);
	}		
}
