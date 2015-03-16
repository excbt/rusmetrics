package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.service.UDirectoryNodeService;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;

@Controller
@RequestMapping(value = "/api/uDirectory")
public class UDirectoryController {

	@Autowired
	private UDirectoryService directoryService;

	@Autowired
	private UDirectoryNodeService directoryNodeService;
	
	@RequestMapping(value = "/{id}/nodes", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listDirectoryNodes(@PathVariable("id") long directoryId) {
		UDirectory dir = directoryService.findOne(directoryId);
		checkNotNull(dir);
		
		UDirectoryNode result = dir.getDirectoryNode(); 
		return ResponseEntity.ok(result);
	}		

	@RequestMapping(value = "/{id}/nodes", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll(@PathVariable("id") long directoryId) {
		UDirectory dir = directoryService.findOne(directoryId);
		checkNotNull(dir);
		
		UDirectoryNode result = dir.getDirectoryNode(); 
		return ResponseEntity.ok(result);
	}		
	
	
	
}
