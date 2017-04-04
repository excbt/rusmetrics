package ru.excbt.datafuse.nmk.data.service.energypassport;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;
import ru.excbt.datafuse.nmk.passdoc.*;
import ru.excbt.datafuse.nmk.passdoc.dto.PDTableValueCellsDTO;

/**
 * Created by kovtonyk on 03.04.2017.
 */
@Service
public class EnergyPassportOrder401_2014 {


    protected EnergyPassportSectionTemplateFactory getEnergyPassportSectionTemplateFactory(final PDTable pdTable) {

        return new EnergyPassportSectionTemplateFactory () {

            private final PDTable savedPDTable = pdTable;

            @Override
            public EnergyPassportSectionTemplate createSectionTemplate() {
                EnergyPassportSectionTemplate result = new EnergyPassportSectionTemplate();
                result.setSectionKey(pdTable.getSectionKey());
                result.setSectionJson(JsonMapperUtils.objectToJson(pdTable, true));
                return result;
            }

            @Override
            public PDTable getPDTable() {
                return pdTable;
            }

            @Override
            public String createValuesJson() {
                PDTableValueCellsDTO valueCellsDTO = new PDTableValueCellsDTO();
                valueCellsDTO.addValueCells(savedPDTable.extractCellValues());
                return JsonMapperUtils.objectToJson(valueCellsDTO);
            }
        } ;

    }


    /**
     * page 26 of Order
     * @return
     */
    public EnergyPassportSectionTemplateFactory createSection_2_3() {

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

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1").createStaticElement("1.").and()
            .createStaticElement("Наименование организации").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.1").createStaticElement("1.1").and()
            .createStaticElement("Организационно-правовая форма").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.2").createStaticElement("1.2").and()
            .createStaticElement("Почтовый адрес организации").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.3").createStaticElement("1.3").and()
            .createStaticElement("Место нахождения").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.4").createStaticElement("1.4").and()
            .createStaticElement("Полное  наименование  организации  (основного общества - для дочерних \n" +
                "(зависимых) обществ) ").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.5").createStaticElement("1.5").and()
            .createStaticElement("Доля государственной (муниципальной) собственности в уставном\n" +
                "капитале организации, % \n").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.6").createStaticElement("1.6").and()
            .createStaticElement("ИНН").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("ОГРН").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.7").createStaticElement("1.7").and()
            .createStaticElement("Код по ОКВЭД").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("код по ОКОГУ").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.8").createStaticElement("1.8").and()
            .createStaticElement("Ф.И.О., должность руководителя").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.9").createStaticElement("1.9").and()
            .createStaticElement(".И.О., должность, телефон, факс,  адрес\n" +
                "      электронной  почты  должностного   лица,\n" +
                "      ответственного за техническое  состояние\n" +
                "      оборудования\n").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.10").createStaticElement("1.10").and()
            .createStaticElement("Ф.И.О., должность, телефон, факс, адрес\n" +
                "      электронной  почты  должностного  лица,\n" +
                "      ответственного     за    энергетическое\n" +
                "      хозяйство\n").and().createStringValueElement();

        return getEnergyPassportSectionTemplateFactory(pdTable);
    }


    /**
     * Main Section S_M2
     * @return
     */
    public EnergyPassportSectionTemplateFactory createSection_S_M2() {

        final PDTable pdTable = new PDTable().viewType(PDViewType.TABLE).sectionKey("S_M2")
            .caption("Общие сведения о потреблении энергетических ресурсов в отчетном году");

        pdTable.createPart(PDPartType.ROW).key("P_2.1")
            .createStaticElement("Тепловая энергия").and()
            .createStaticElement("(Гкал/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Тепловая энергия").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.ROW).key("P_2.2")
            .createStaticElement("Электрическая энергия").and()
            .createStaticElement("(кВт·ч/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Электрическая энергия").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.ROW).key("P_2.3")
            .createStaticElement("Газ").and()
            .createStaticElement("(тыс. куб. м/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Газ").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.ROW).key("P_2.4")
            .createStaticElement("Жидкое топливо").and()
            .createStaticElement("(т/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Жидкое топливо").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.ROW).key("P_2.5")
            .createStaticElement("Твердое топливо").and()
            .createStaticElement("(т/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Твердое топливо").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.ROW).key("P_2.6")
            .createStaticElement("Моторное топливо").and()
            .createStaticElement("(т/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Моторное топливо").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);


        return getEnergyPassportSectionTemplateFactory(pdTable);
    }


    /**
     * Main Section S_M3
     * @return
     */
    public EnergyPassportSectionTemplateFactory createSection_S_M3() {
        final PDTable pdTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_M3");

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_3").createStaticElement("Наличие собственного источника выработки энергии (есть/нет")
            .and().createBooleanValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_3_gas").createStaticElement("Газ")
            .and().createBooleanValueElement().and().createStaticElement("годовой расход")
            .and().createStaticElement("(тыс. куб. м/год)")
            .and().createDoubleValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_3_liquid").createStaticElement("Жидкое топливо")
            .and().createBooleanValueElement().and().createStaticElement("годовой расход")
            .and().createStaticElement("(т/год)")
            .and().createDoubleValueElement();

        pdTable.createPart(PDPartType.ROW).key("P_3_solid").createStaticElement("Твердое топливо")
            .and().createBooleanValueElement().and().createStaticElement("годовой расход")
            .and().createStaticElement("(т/год)")
            .and().createDoubleValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).createStaticElement("Вид вырабатываемой энергии:");

        pdTable.createPart(PDPartType.ROW).key("P_3_type")
            .createStaticElement("Электрическая").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Тепловая: пар").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2)
            .and().createStaticElement("Тепловая:").keyValueIdx(3).and().createStringValueElement().keyValueIdx(3);

        pdTable.createPart(PDPartType.ROW).key("P_3_type_yn")
            .createStaticElement("(да/нет)").keyValueIdx(1).and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("(да/нет)").keyValueIdx(2).and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("(да/нет)").keyValueIdx(3).and().createBooleanValueElement().keyValueIdx(3);

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_3_comb").createStaticElement("Комбинированная выработка (да/нет):")
            .and().createBooleanValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_3_comb").createStaticElement("Режим управления работой котлов\n" +
            "(автоматический/ручное управление) \n")
            .and().createBooleanValueElement();

        return getEnergyPassportSectionTemplateFactory(pdTable);
    }


    private PDTable createSectionPartsM1(PDTable pdTable) {
        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1").createStaticElement("1.").and()
            .createStaticElement("Наименование организации").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.1").createStaticElement("1.1").and()
            .createStaticElement("Организационно-правовая форма").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.2").createStaticElement("1.2").and()
            .createStaticElement("Почтовый адрес организации").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.3").createStaticElement("1.3").and()
            .createStaticElement("Место нахождения").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.4").createStaticElement("1.4").and()
            .createStaticElement("Полное  наименование  организации  (основного общества - для дочерних \n" +
                "(зависимых) обществ) ").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.5").createStaticElement("1.5").and()
            .createStaticElement("Доля государственной (муниципальной) собственности в уставном\n" +
                "капитале организации, % \n").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.6").createStaticElement("1.6").and()
            .createStaticElement("ИНН").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("ОГРН").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.7").createStaticElement("1.7").and()
            .createStaticElement("Код по ОКВЭД").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("код по ОКОГУ").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2);

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.8").createStaticElement("1.8").and()
            .createStaticElement("Ф.И.О., должность руководителя").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.9").createStaticElement("1.9").and()
            .createStaticElement(".И.О., должность, телефон, факс,  адрес\n" +
                "      электронной  почты  должностного   лица,\n" +
                "      ответственного за техническое  состояние\n" +
                "      оборудования\n").and().createStringValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_1.10").createStaticElement("1.10").and()
            .createStaticElement("Ф.И.О., должность, телефон, факс, адрес\n" +
                "      электронной  почты  должностного  лица,\n" +
                "      ответственного     за    энергетическое\n" +
                "      хозяйство\n").and().createStringValueElement();

        return pdTable;
    }

    /*

     */
    private PDTable createSectionPartInnerM2(PDTable pdTable){

        PDInnerTable innerTable = pdTable.createPart(PDPartType.INNER_TABLE).key("P_2_T").createInnerTable();

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + ".heat")
            .createStaticElement("Тепловая энергия").and()
            .createStaticElement("(Гкал/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Тепловая энергия").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + ".electricity")
            .createStaticElement("Электрическая энергия").and()
            .createStaticElement("(кВт·ч/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Электрическая энергия").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + ".gas")
            .createStaticElement("Газ").and()
            .createStaticElement("(тыс. куб. м/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Газ").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + ".liquid")
            .createStaticElement("Жидкое топливо").and()
            .createStaticElement("(т/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Жидкое топливо").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + ".solid")
            .createStaticElement("Твердое топливо").and()
            .createStaticElement("(т/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Твердое топливо").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + ".motor")
            .createStaticElement("Моторное топливо").and()
            .createStaticElement("(т/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Моторное топливо").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        return innerTable;

    }


    /**
     *
     * @param pdTable
     * @return
     */
    private PDTable createSectionPartInnerM31(PDTable pdTable) {
        PDInnerTable innerTable = pdTable.createPart(PDPartType.INNER_TABLE).key("P_3_prod").createInnerTable();

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_gas").createStaticElement("Газ").keyValueIdx(1)
            .and().createBooleanValueElement().keyValueIdx(1).and().createStaticElement("годовой расход").keyValueIdx(2)
            .and().createStaticElement("(тыс. куб. м/год)")
            .and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_liquid").createStaticElement("Жидкое топливо").keyValueIdx(1)
            .and().createBooleanValueElement().keyValueIdx(1).and().createStaticElement("годовой расход").keyValueIdx(2)
            .and().createStaticElement("(т/год)")
            .and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_solid").createStaticElement("Твердое топливо").keyValueIdx(1)
            .and().createBooleanValueElement().keyValueIdx(1).and().createStaticElement("годовой расход").keyValueIdx(2)
            .and().createStaticElement("(т/год)")
            .and().createDoubleValueElement().keyValueIdx(2);
        return innerTable;
    }

    /*

     */
    private PDTable createSectionPartInnerM32(PDTable pdTable) {
        PDInnerTable innerTable = pdTable.createPart(PDPartType.INNER_TABLE).key("P_3_prod").createInnerTable();

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_row")
            .createStaticElement("Электрическая").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Тепловая: пар").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2)
            .and().createStaticElement("Тепловая:").keyValueIdx(3).and().createStringValueElement().keyValueIdx(3);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_row_yn")
            .createStaticElement("(да/нет)").keyValueIdx(1).and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("(да/нет)").keyValueIdx(2).and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("(да/нет)").keyValueIdx(3).and().createBooleanValueElement().keyValueIdx(3);
        return innerTable;
    }

    private PDTable createSectionPartInnerM4(PDTable pdTable) {
        PDInnerTable innerTable = pdTable.createPart(PDPartType.INNER_TABLE).key("P_4").createInnerTable();

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_heat")
            .createStaticElement("Тепловая энергия").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_electricity")
            .createStaticElement("Электрическая энергия").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_gas")
            .createStaticElement("Газ").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_liquid")
            .createStaticElement("Жидкое топливо").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        innerTable.createPart(PDPartType.ROW).key(innerTable.getParentPartKey() + "_solid")
            .createStaticElement("Твердое топливо").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        return innerTable;
    }

    /*

     */
    public EnergyPassportSectionTemplateFactory createSection_Main() {


        final PDTable pdTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_M")
            .caption("ИНФОРМАЦИЯ\nо потреблении энергетических ресурсов организации\n" +
                "за 20__ год");

        createSectionPartsM1(pdTable);

        /// Inner 2
        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_2").createStaticElement("Общие сведения о потреблении энергетических ресурсов в отчетном году");
        createSectionPartInnerM2(pdTable);


        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_3").createStaticElement("Наличие собственного источника выработки энергии (есть/нет)")
            .and()
            .createBooleanValueElement();

        // Inner 3
        createSectionPartInnerM31(pdTable);

        pdTable.createPart(PDPartType.SIMPLE_LINE).createStaticElement("Вид вырабатываемой энергии:");

        createSectionPartInnerM32(pdTable);

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_3_comb").createStaticElement("Комбинированная выработка (да/нет):")
            .and().createBooleanValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_3_mode").createStaticElement("Режим управления работой котлов\n" +
            "(автоматический/ручное управление) \n")
            .and().createBooleanValueElement();


        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_4").createStaticElement("Общие сведения об оплате за энергоресурсы");


        createSectionPartInnerM4(pdTable);

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_5").createStaticElement("Среднесписочная численность (чел.)\n" +
            "Всех работников").and().createIntegerValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_6").createStaticElement("Программа энергосбережения и повышения энергетической эффективности\n" +
            "Наличие утвержденной программы энергосбережения\n" +
            "(есть/нет)").and().createBooleanValueElement();

        pdTable.createPart(PDPartType.SIMPLE_LINE).key("P_7_build").createStaticElement("Количество зданий обследуемой организации").and().createIntegerValueElement();

        return getEnergyPassportSectionTemplateFactory(pdTable);

    }


    public EnergyPassportSectionTemplateFactory createSection_2_10(){
        final PDTable pdTable = new PDTable().viewType(PDViewType.TABLE).sectionKey("S_M")
            .caption("Краткая характеристика зданий (строений, сооружений)");

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);
        partHeader
            .createStaticElement("№ п/п").keyValueIdx(1)
            .and().createStaticElement("Наименование здания, строения, сооружения").keyValueIdx(2)
            .and().createStaticElement("Год ввода в эксплуатацию").keyValueIdx(3)
            .and().createStaticElement("Ограждающие конструкции").keyValueIdx(4)
                .createChild().caption("наименование конструкции")
                .createSibling().caption("краткая характеристика")
            .and().createStaticElement("Общая площадь, здания, строения, сооружения, кв. м").keyValueIdx(5)
            .and().createStaticElement("Отапливаемая площадь, здания, строения, сооружения, кв. м").keyValueIdx(6)
            .and().createStaticElement("Отапливаемый объем здания, строения, сооружения, куб. м").keyValueIdx(7)
            .and().createStaticElement("Износ здания, строения, сооружения, %").keyValueIdx(8);

        pdTable.createPart(PDPartType.ROW).key("MAIN_D").dynamic()
            .createValueElement(PDTableCellValueCounter.class).keyValueIdx(1)// #
            .and().createStringValueElement().keyValueIdx(2) // name
            .and().createStringValueElement().keyValueIdx(3) // year
            .and().createStaticElement().keyValueIdx(4)
                .createChild("Стены").keyValueIdx(4).packValueIdx(1)
                .createSibling("Окна").keyValueIdx(4).packValueIdx(2)
                .createSibling("Крыша").keyValueIdx(4).packValueIdx(3)
            .and().createPackValueElement().keyValueIdx(5)
                .createChildValue(PDTableCellValueDouble.class).keyValueIdx(5).packValueIdx(1)
                .createSiblingValue(PDTableCellValueDouble.class).keyValueIdx(5).packValueIdx(2)
                .createSiblingValue(PDTableCellValueDouble.class).keyValueIdx(5).packValueIdx(3)
            .and().createDoubleValueElement().keyValueIdx(6)
            .and().createDoubleValueElement().keyValueIdx(7)
            .and().createDoubleValueElement().keyValueIdx(8);

        return getEnergyPassportSectionTemplateFactory(pdTable);
    }


}
