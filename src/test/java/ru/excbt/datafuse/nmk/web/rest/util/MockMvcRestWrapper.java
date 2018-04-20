package ru.excbt.datafuse.nmk.web.rest.util;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.excbt.datafuse.nmk.web.rest.TestUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MockMvcRestWrapper {

    private static final Logger log = LoggerFactory.getLogger(MockMvcRestWrapper.class);

    private MockMvc mockMvc;
    private Logger logger = log;

    public class RestRequest {

        private final MockMvcRestWrapper wrapper;

        private Optional<Consumer<MockHttpServletRequestBuilder>> builder = Optional.empty();

        private String urlTemplate;

        private Object[] uriVars;

        private List<ResultActions> resultActionsList = new ArrayList<>();

        private boolean noPrintRequest = false;

        private boolean noJsonOutput = false;

        private ResultHandler jsonLogger = (i) -> logger.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i));

        private RestRequest(MockMvcRestWrapper wrapper, String urlTemplate, Object[] uriVars) {
            this.wrapper = wrapper;
            this.urlTemplate = urlTemplate;
            this.uriVars = uriVars;
        }

        public RestRequest requestBuilder(Consumer<MockHttpServletRequestBuilder> arg) {
            this.builder = Optional.ofNullable(arg);
            return this;
        }

        public RestRequest noPrintRequest() {
            this.noPrintRequest = true;
            return this;
        }

        public RestRequest noJsonOutput() {
            this.noJsonOutput = true;
            return this;
        }

        /**
         *
         * @param requestBuilder
         * @return
         * @throws Exception
         */
        private ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
            ResultActions resultActions = mockMvc.perform(
                requestBuilder);
            if (!noPrintRequest) {
                resultActions.andDo(MockMvcResultHandlers.print());
            }
            resultActions.andExpect(status().isOk());
            if (!noJsonOutput) {
                resultActions.andDo(jsonLogger);
            }
            return resultActions;
        }

        /**
         *
         * @return
         * @throws Exception
         */
        public ResultActions testGetAndReturn() throws Exception {

            MockHttpServletRequestBuilder requestBuilder = get(urlTemplate, uriVars);
            builder.ifPresent(b -> b.accept(requestBuilder));

            ResultActions resultActions = perform(requestBuilder);
            this.resultActionsList.add(resultActions);
            return resultActions;
        }

        public RestRequest testGet() throws Exception {
            testGetAndReturn();
            return this;
        }

        /**
         *
         * @param obj
         * @return
         * @throws Exception
         */
        public ResultActions testPutAndReturn(Object obj) throws Exception {

            MockHttpServletRequestBuilder requestBuilder = put(urlTemplate, uriVars)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(obj));
            builder.ifPresent(b -> b.accept(requestBuilder));

            ResultActions resultActions = perform(requestBuilder);
            this.resultActionsList.add(resultActions);

            return resultActions;
        }

        public RestRequest testPut(Object obj) throws Exception {
            testPutAndReturn(obj);
            return this;
        }

        /**
         *
         * @param obj
         * @return
         * @throws Exception
         */
        public ResultActions testPostAndReturn(Object obj) throws Exception {

            MockHttpServletRequestBuilder requestBuilder = post(urlTemplate, uriVars)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(obj));
            builder.ifPresent(b -> b.accept(requestBuilder));

            ResultActions resultActions = perform(requestBuilder);
            this.resultActionsList.add(resultActions);

            return resultActions;
        }

        public RestRequest testPost(Object obj) throws Exception {
            testPostAndReturn(obj);
            return this;
        }

        public ResultActions testDeleteAndReturn() throws Exception {

            MockHttpServletRequestBuilder requestBuilder = delete(urlTemplate, uriVars);

            builder.ifPresent(b -> b.accept(requestBuilder));

            ResultActions resultActions = perform(requestBuilder);
            this.resultActionsList.add(resultActions);

            return resultActions;
        }

        public RestRequest testDelete() throws Exception {
            testDeleteAndReturn();
            return this;
        }

        public List<ResultActions> getResultActionsList() {
            return new ArrayList<>(this.resultActionsList);
        }

        public Optional<ResultActions> getLatestResult() {
            if (resultActionsList.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(resultActionsList.get(resultActionsList.size()-1));
        }

        public MockMvcRestWrapper and() {
            return wrapper;
        }

        public RestRequest doWithLastId(Consumer<Long> consumer) {
            try {
                Long id = getLastId();
                consumer.accept(id);
            } catch (UnsupportedEncodingException e) {
                log.error("Skip doWithLastId action");
            }
            return this;
        }

        /**
         * Warning: Internal type of id is Integer. JsonPath.read specific behavior
         * @return
         * @throws UnsupportedEncodingException
         */
        public Long getLastId() throws UnsupportedEncodingException {
            Integer result = getLatestResult().map(a -> {
                try {
                    String content = a.andReturn().getResponse().getContentAsString();
                    Integer id = JsonPath.read(content, "$.id");
                    return id;
                } catch (UnsupportedEncodingException e) {
                    log.error("getLastId error: {}", e);
                    return Integer.MIN_VALUE;
                }
            }).orElse(null);

            if (result == Integer.MIN_VALUE) {
                throw new UnsupportedEncodingException();
            }
            return Long.valueOf(result);
        }
    }

    public MockMvcRestWrapper(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public MockMvcRestWrapper logger(Logger arg) {
        this.logger = arg;
        return this;
    }

    public RestRequest restRequest(String urlTemplate, Object ... args) throws Exception {
        RestRequest restRequest = new RestRequest(this, urlTemplate, args);
        return restRequest;
    }
}
