package ru.excbt.datafuse.nmk.web;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public interface ResultActionsTester {

    ResultActionsTester TEST_SUCCESSFULL = (resultActions) -> {
        resultActions.andDo(MockMvcResultHandlers.print());
        resultActions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    };

    void testResultActions(ResultActions resultActions) throws Exception;
}
