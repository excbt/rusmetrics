package ru.excbt.datafuse.nmk.web.rest;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageType;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;
import ru.excbt.datafuse.nmk.data.service.CabinetMessageService;
import ru.excbt.datafuse.nmk.data.service.support.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.util.PaginationUtil;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CabinetMessageResource {

    private static final Logger log = LoggerFactory.getLogger(CabinetMessageResource.class);

    private final CabinetMessageService cabinetMessageService;

    private final PortalUserIdsService idsService;

    @Autowired
    public CabinetMessageResource(CabinetMessageService cabinetMessageService, PortalUserIdsService portalUserIdsService) {
        this.cabinetMessageService = cabinetMessageService;
        this.idsService = portalUserIdsService;
    }


    /**
     * GET  /cabinet-messages : get all the cabinetMessages.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of cabinetMessages in body
     */
    @GetMapping("/cabinet-messages")
    public ResponseEntity<List<CabinetMessageDTO>> getAllCabinetMessageRequests(@RequestParam(name = "messageType", required = false) String messageType , Pageable pageable) {
        log.debug("REST request to get a page of CabinetMessages");
        CabinetMessageType cabinetMessageType = EnumUtils.getEnum(CabinetMessageType.class, messageType);
        if (cabinetMessageType == null) {
            cabinetMessageType = CabinetMessageType.REQUEST;
        }
        Page<CabinetMessageDTO> page = cabinetMessageService.findAllRequestToSubscriber(idsService.getCurrentIds(), cabinetMessageType, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cabinet-messages");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }



}
