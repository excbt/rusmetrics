package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.model.dto.ContServiceTypeDTO;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContServiceTypeRepository;
import ru.excbt.datafuse.nmk.service.mapper.ContServiceTypeMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ContServiceTypeResource {

    private final ContServiceTypeRepository contServiceTypeRepository;

    private final ContServiceTypeMapper contServiceTypeMapper;

    public ContServiceTypeResource(ContServiceTypeRepository contServiceTypeRepository, ContServiceTypeMapper contServiceTypeMapper) {
        this.contServiceTypeRepository = contServiceTypeRepository;
        this.contServiceTypeMapper = contServiceTypeMapper;
    }

    @ApiOperation("Get list of supported service types")
    @GetMapping("/contServiceTypes")
    public ResponseEntity<List<ContServiceTypeDTO>> getServiceTypes() {
        List<ContServiceTypeDTO> list = contServiceTypeRepository
            .selectAll().stream().map(i -> contServiceTypeMapper.toDto(i)).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


}
