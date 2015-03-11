package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.NodeDirectory;
import ru.excbt.datafuse.nmk.data.service.NodeDirectoryService;

@Controller
@RequestMapping(value = "/api/nodeDirectory")
public class NodeDirectoryController {

	@Autowired
	private NodeDirectoryService nodeDirectoryService;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll(@PathVariable("id") long nodeDirectoryId) {
		NodeDirectory result = nodeDirectoryService.getRootNode(nodeDirectoryId); 
		return ResponseEntity.ok(result);
	}		
}
