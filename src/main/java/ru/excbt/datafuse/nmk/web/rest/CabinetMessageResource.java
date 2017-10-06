package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageType;
import ru.excbt.datafuse.nmk.data.model.dto.CabinetMessageDTO;
import ru.excbt.datafuse.nmk.data.service.CabinetMessageService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.util.PaginationUtil;

import javax.validation.Valid;
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
    @ApiOperation("Get all cabinet messages to current subscriber")
    @GetMapping("/cabinet-messages")
    public ResponseEntity<List<CabinetMessageDTO>> getAllCabinetMessageRequests(@ApiParam("message type") @RequestParam(name = "messageType", required = false) @Valid CabinetMessageType cabinetMessageType , Pageable pageable) {
        log.debug("REST request to get a page of CabinetMessages");
//        CabinetMessageType cabinetMessageType = EnumUtils.getEnum(CabinetMessageType.class, messageType);
//        if (cabinetMessageType == null) {
//            cabinetMessageType = CabinetMessageType.REQUEST;
//        }
        Page<CabinetMessageDTO> page = cabinetMessageService.findAllRequestToSubscriber(idsService.getCurrentIds(), cabinetMessageType, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cabinet-messages");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @ApiOperation("Get chain of messages by master message id")
    @GetMapping("/cabinet-messages/{id}/chain")
    public ResponseEntity<List<CabinetMessageDTO>> getCabinetMessageChain(@ApiParam("id of master message") @PathVariable("id") Long masterMessageId) {
        log.debug("REST request to get a page of CabinetMessages");
        List<CabinetMessageDTO> list = cabinetMessageService.findMessageChain(masterMessageId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation("Update review datetime of chain of messages by master message id")
    @PutMapping("/cabinet-messages/{id}/chainReview")
    public ResponseEntity<?> updateMessageChainReview(@ApiParam("id of master  message") @PathVariable("id") Long masterMessageId,
                                                      @RequestParam(name = "resetReviews", required = false) @ApiParam() Boolean resetReviews) {
        log.debug("REST request to get a page of CabinetMessages");
        cabinetMessageService.updateMessageChainReview(masterMessageId, idsService.getCurrentIds(), Boolean.TRUE.equals(resetReviews));
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
