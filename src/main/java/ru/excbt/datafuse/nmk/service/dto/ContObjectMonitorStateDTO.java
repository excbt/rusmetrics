package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.service.vm.ContObjectShortInfoVM;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ContObjectMonitorStateDTO {

    private ContObjectShortInfoVM contObjectShortInfo;

    private List<ContZPointMonitorStateDTO> contZPointMonitorState = new ArrayList<>();

}
