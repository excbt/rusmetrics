package ru.excbt.datafuse.nmk.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.passdoc.*;

import java.util.HashMap;
import java.util.List;
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

            log.info("Pretty JSON: \n{}", jsonBodyPretty);

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

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п").width(10);
        partHeader.createStaticElement().caption("Наименование показателя").width(40);
        PDTableCellStatic amount = partHeader.createStaticElement().caption("Количество, шт");

        int keyValueIdx = 1;
        amount.createChild().caption("Электрической энергии")
            .createChild().caption("Всего").width(10).keyValueIdx(keyValueIdx++)
            .createSibling().caption("В том числе в составе АИИС").width(10).keyValueIdx(keyValueIdx++);

        amount.createChild().caption("Тепловой энергии")
            .createChild().caption("Всего").width(10).keyValueIdx(keyValueIdx++)
            .createSibling().caption("В том числе в составе АИИС").width(10).keyValueIdx(keyValueIdx++);

        amount.createChild().caption("Газа")
            .createChild().caption("Всего").width(10).keyValueIdx(keyValueIdx++)
            .createSibling().caption("В том числе в составе АИИС").width(10).keyValueIdx(keyValueIdx++);


        pdTable.createPart(PDPartType.ROW).key("P_1").createStaticElement("1")
            .and().createStaticElement("Сведения об оснащенности приборами коммерческого учета").merged();

        pdTable.createPart(PDPartType.ROW).key("P_1.1").createStaticElement("1.1")
            .and().createStaticElement("Количество оборудованных узлами (приборами) учета точек приема (поставки), всего,\n" +
            "в том числе:\n")
            .and().createValueElements(6, PDTableCellValueDoubleAggregation.class).forEach(i -> {
                i.setFunction("sum()");
                i.setGroup("P_1.1.*");
        });

        pdTable.createPart(PDPartType.ROW).key("P_1.1.1").createStaticElement("1.1.1")
            .and().createStaticElement("полученной от стороннего источника")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPart(PDPartType.ROW).key("P_1.1.2").createStaticElement("1.1.2")
            .and().createStaticElement("собственного производства")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPart(PDPartType.ROW).key("P_1.1.3").createStaticElement("1.1.3")
            .and().createStaticElement("потребленной на собственные нужды")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPart(PDPartType.ROW).key("P_1.1.4").createStaticElement("1.1.4")
            .and().createStaticElement("отданной субабонентам (сторонним потребителям)")
            .and().createValueElements(6, PDTableCellValueDouble.class);

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


        PDTable pdTable1 = OBJECT_MAPPER.readValue(json,PDTable.class);

        pdTable1.linkInternalRefs();

        System.out.println("======================================");
        String json1 = objectToJson(pdTable1);

        if (!json.equals(json1)) {
            System.out.println("DESERIALIZATION IS NOT EQUALS");
        }

        System.out.println("======================================");
        List<PDTableCell> valueCells = pdTable1.extractCellValues();

        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(valueCells);

        valueCells.forEach(i -> {
            log.info("Class: {}",i.getClass().getSimpleName());
        });

        objectToJson(valueCells);

    }
}
