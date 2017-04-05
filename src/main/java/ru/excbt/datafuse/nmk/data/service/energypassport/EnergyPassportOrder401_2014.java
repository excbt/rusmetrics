package ru.excbt.datafuse.nmk.data.service.energypassport;

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
@Deprecated
public class EnergyPassportOrder401_2014 {




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

        return new EPSectionTemplateFactory(pdTable);
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


        return new EPSectionTemplateFactory(pdTable);
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

        return new EPSectionTemplateFactory(pdTable);
    }





}
