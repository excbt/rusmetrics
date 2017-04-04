package ru.excbt.datafuse.nmk.config;

import net.sf.jasperreports.engine.design.JRAbstractJavaCompiler;
import net.sf.jasperreports.engine.fonts.FontUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by kovtonyk on 04.04.2017.
 */
@Configuration
public class ServletContextListenerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ServletContextListenerConfiguration.class);

    @Bean
    protected ServletContextListener listener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                log.info("ServletContext initialized");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                fixJasperLocalThread();
                log.info("ServletContext destroyed");
            }
        };
    }


    private void fixJasperLocalThread() {
        fixJasperLocalThread(FontUtil.class, "threadMissingFontsCache");
        fixJasperLocalThread(JRAbstractJavaCompiler.class, "classFromBytesRef");
    }


    private void fixJasperLocalThread(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.set(null, null);
            }
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            // ignorado
        }
    }

}
