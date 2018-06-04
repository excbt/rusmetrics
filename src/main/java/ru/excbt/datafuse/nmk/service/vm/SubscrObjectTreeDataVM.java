package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class SubscrObjectTreeDataVM {
    private List<Long> contObjectIds = new ArrayList<>();

    public SubscrObjectTreeDataVM addIds(Long ... ids) {
        contObjectIds.addAll(Arrays.asList(ids));
        return this;
    }
}
