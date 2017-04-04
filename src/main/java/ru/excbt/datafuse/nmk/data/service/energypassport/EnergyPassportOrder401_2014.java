package ru.excbt.datafuse.nmk.data.service.energypassport;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;
import ru.excbt.datafuse.nmk.passdoc.*;

/**
 * Created by kovtonyk on 03.04.2017.
 */
@Service
public class EnergyPassportOrder401_2014 {


    protected EnergyPassportSectionTemplateFactory getEnergyPassportSectionTemplateFactory(PDTable pdTable) {
        return () -> {
            EnergyPassportSectionTemplate result = new EnergyPassportSectionTemplate();
            result.setSectionKey(pdTable.getSectionKey());
            try {
                result.setSectionJson(JsonMapperUtils.objectToJson(pdTable));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return result;
        };
    }


    /**
     * page 26 of Order
     * @return
     */
    public EnergyPassportSectionTemplateFactory createSection_S_2_3() {

        final PDTable pdTable = new PDTable().viewType(PDViewType.TABLE).sectionKey("S_2.3");

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
            .and().createStaticElement("Сведения об оснащенности приборами коммерческого учета").mergedCells(7);

        /// 1.1
        pdTable.createPart(PDPartType.ROW).key("P_1.1").createStaticElement("1.1")
            .and().createStaticElement("Количество оборудованных узлами (приборами) учета точек приема (поставки), всего,\n" +
            "в том числе:\n")
            .and().createValueElements(6, PDTableCellValueDoubleAggregation.class).forEach(i -> {
            i.setValueFunction("sum()");
            i.setValueGroup("P_1.1.*");
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

        /// 1.2
        pdTable.createPart(PDPartType.ROW).key("P_1.2").createStaticElement("1.2")
            .and().createStaticElement("Количество необорудованных узлами (приборами) учета точек приема (поставки), всего,\n" +
            "в том числе:\n")
            .and().createValueElements(6, PDTableCellValueDoubleAggregation.class).forEach(i -> {
            i.setValueFunction("sum()");
            i.setValueGroup("P_2.1.*");
        });

        pdTable.createPart(PDPartType.ROW).key("P_1.2.1").createStaticElement("1.2.1")
            .and().createStaticElement("полученной от стороннего источника")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPart(PDPartType.ROW).key("P_1.2.2").createStaticElement("1.2.2")
            .and().createStaticElement("собственного производства")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPart(PDPartType.ROW).key("P_1.2.3").createStaticElement("1.2.3")
            .and().createStaticElement("потребленной на собственные нужды")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPart(PDPartType.ROW).key("P_1.2.4").createStaticElement("1.2.4")
            .and().createStaticElement("отданной субабонентам (сторонним потребителям)")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        // 1.3
        pdTable.createPart(PDPartType.ROW).key("P_1.3").createStaticElement("1.3")
            .and().createStaticElement("Количество узлов (приборов) учета с нарушенными сроками поверки")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        // 1.4
        pdTable.createPart(PDPartType.ROW).key("P_1.4").createStaticElement("1.4")
            .and().createStaticElement("Количество узлов (приборов) учета с нарушением требований к классу точности " +
            "(относительной погрешности) узла (прибора) учета")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        // 2
        pdTable.createPart(PDPartType.ROW).key("P_2").createStaticElement("2")
            .and().createStaticElement("Сведения об оснащенности узлами (приборами) технического учета").mergedCells(7);

        // 2.1
        pdTable.createPart(PDPartType.ROW).key("P_2.1").createStaticElement("2.1")
            .and().createStaticElement("Суммарное количество узлов (приборов) учета")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        return getEnergyPassportSectionTemplateFactory(pdTable);
    }

    /**
     * Main Section S_M1
     * @return
     */
    public EnergyPassportSectionTemplateFactory createSection_S_M1() {

        final PDTable pdTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_M1")
            .caption("ИНФОРМАЦИЯ\nо потреблении энергетических ресурсов организации\n" +
                "за 20__ год");

        pdTable.createPart(PDPartType.ROW).key("P_1").createStaticElement("1.").and()
            .createStaticElement("Наименование организации").and().createStringValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_1.1").createStaticElement("1.1").and()
            .createStaticElement("Организационно-правовая форма").and().createStringValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_1.2").createStaticElement("1.2").and()
            .createStaticElement("Почтовый адрес организации").and().createStringValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_1.3").createStaticElement("1.3").and()
            .createStaticElement("Место нахождения").and().createStringValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_1.4").createStaticElement("1.4").and()
            .createStaticElement("Полное  наименование  организации  (основного общества - для дочерних \n" +
                "(зависимых) обществ) ").and().createStringValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_1.5").createStaticElement("1.5").and()
            .createStaticElement("Доля государственной (муниципальной) собственности в уставном\n" +
                "капитале организации, % \n").and().createStringValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_1.6").createStaticElement("1.6").and()
            .createStaticElement("ИНН").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("ОГРН").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.ROW).key("P_1.7").createStaticElement("1.7").and()
            .createStaticElement("Код по ОКВЭД").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("код по ОКОГУ").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.ROW).key("P_1.8").createStaticElement("1.8").and()
            .createStaticElement("Ф.И.О., должность руководителя").and().createStringValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_1.9").createStaticElement("1.9").and()
            .createStaticElement(".И.О., должность, телефон, факс,  адрес\n" +
                "      электронной  почты  должностного   лица,\n" +
                "      ответственного за техническое  состояние\n" +
                "      оборудования\n").and().createStringValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_1.10").createStaticElement("1.10").and()
            .createStaticElement("Ф.И.О., должность, телефон, факс, адрес\n" +
                "      электронной  почты  должностного  лица,\n" +
                "      ответственного     за    энергетическое\n" +
                "      хозяйство\n").and().createStringValueElement();

        return getEnergyPassportSectionTemplateFactory(pdTable);
    }



}
