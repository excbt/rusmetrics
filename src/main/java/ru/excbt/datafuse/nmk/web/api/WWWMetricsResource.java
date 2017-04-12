package ru.excbt.datafuse.nmk.web.api;

import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import ru.excbt.datafuse.nmk.config.Constants;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by kovtonyk on 12.04.2017.
 */
@Controller
@RequestMapping(value = "/api")
public class WWWMetricsResource implements ServletContextAware {

    private ServletContext servletContext;

    private final Environment env;

    public WWWMetricsResource(Environment env) {
        this.env = env;
    }

    public void setServletContext(ServletContext servletCtx){
        this.servletContext=servletCtx;
    }

    @RequestMapping(value = "/www-metrics", method = RequestMethod.GET, produces = "application/javascript; charset=utf-8")
    public ResponseEntity<?> getWwwMetrics() {

        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (!activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
            return ResponseEntity.ok("<script><!-- DEVELOPMENT --></script>");
        }

        String rootPath = servletContext.getRealPath("/");
        String jsPath = rootPath + "app/scripts/www-metrics.js";
        String content;
        try {
            content = FileUtils.readFileToString(new File(jsPath), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            content = "<script><!-- NOT FOUND --></script>";
        }
        return ResponseEntity.ok(content);
    }


}
