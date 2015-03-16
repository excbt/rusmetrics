package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(value = "/api/u_directory")
public class UDirectoryNodeController {

	
	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryNodeController.class);
	
	@Autowired
	private UDirectoryNodeService directoryNodeService;

	@Autowired
	private UDirectoryService directoryService;

	/**
	 * 
	 * @param directoryId
	 * @return
	 */
	@RequestMapping(value = "/{id}/node", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAll(@PathVariable("id") long directoryId) {
		
		UDirectory directory = directoryService.findOne(directoryId);
		if (directory == null) {
			return ResponseEntity.notFound().build();
		}
		UDirectoryNode directoryNode = directory.getDirectoryNode();

		if (directoryNode == null) {
			return ResponseEntity.notFound().build();
		}
		
		logger.trace("DirectoryNode ID {}", directoryNode.getId());
		
		UDirectoryNode result = directoryNodeService.getRootNode(directoryNode.getId());
		return ResponseEntity.ok().body(result);
	}		
}
