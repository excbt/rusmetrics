package ru.excbt.datafuse.nmk.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.passdoc.PDHeaderElement;
import ru.excbt.datafuse.nmk.passdoc.PDTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kovtonyk on 22.03.2017.
 */
public class PassDocTemplateCli {

    private static final Logger log = LoggerFactory.getLogger(PassDocTemplateCli.class);

    public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Data
    public static class PPDocument {
        private Long id;
        private final Map<String,String> props = new HashMap<>();
    }


    private static String objectToJson(Object obj) {
        String jsonBody = null;
        String jsonBodyPretty = null;
        try {
            if (obj instanceof String) {
                jsonBody = (String) obj;
                jsonBodyPretty = (String) obj;

            } else {
                jsonBody = OBJECT_MAPPER.writeValueAsString(obj);
                jsonBodyPretty = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }

            log.info("Request JSON: {}", jsonBody);
            log.info("Request Pretty JSON: {}", jsonBodyPretty);

        } catch (JsonProcessingException e) {
            log.error("Can't create json: {}", e);
            e.printStackTrace();
        }

        return jsonBody;
    }

    public static void main(String[] args) throws Exception {

//        PPDocument doc = new PPDocument();
//        doc.id = 10L;
//        doc.props.put("name","node1");
//        doc.props.put("value","value1");


        PDTable pdTable = new PDTable();

        pdTable.createPDHeaderElement().caption("№ п/п").width(10);
        pdTable.createPDHeaderElement().caption("Наименование показателя").width(40);
        PDHeaderElement amount = pdTable.createPDHeaderElement().caption("Количество, шт");

        amount.createChild().caption("Электрической энергии")
            .createChild().caption("Всего").width(10)
            .createSubling().caption("В том числе в составе АИИС").width(10);

        amount.createChild().caption("Тепловой энергии")
            .createChild().caption("Всего").width(10)
            .createSubling().caption("В том числе в составе АИИС").width(10);

        amount.createChild().caption("Газа")
            .createChild().caption("Всего").width(10)
            .createSubling().caption("В том числе в составе АИИС").width(10);


        String json = objectToJson(pdTable);

//        JsonFactory factory = new JsonFactory();
//        /**
//         * Read values in json format
//         */
//        JsonParser parser = factory.createParser(objectToJson(pdTable));
//        parser.nextToken();                                     //start reading the file
//        while (parser.nextToken() != JsonToken.END_OBJECT) {    //loop until "}"
//
//            String fieldName = parser.getCurrentName();
//            if (fieldName.equals("name")) {
//                System.out.println(parser.getCurrentLocation() +" " + parser.getTokenLocation());
//                parser.nextToken();
//                System.out.println("name : " + parser.getText());
//            } else if (fieldName.equals("value")) {
//                parser.nextToken();
//                System.out.println("value : " + parser.getText());
//            }
//        }
//        parser.close();

    }
}
