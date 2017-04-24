package ru.excbt.datafuse.nmk.data.energypassport;

import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.passdoc.PDInnerTable;
import ru.excbt.datafuse.nmk.passdoc.PDPartType;
import ru.excbt.datafuse.nmk.passdoc.PDTable;
import ru.excbt.datafuse.nmk.passdoc.PDViewType;

/**
 * Created by kovtonyk on 05.04.2017.
 */
@Service
public class EnergyPassport401_2014 {

    public final static String ENERGY_DECLARATION = "ENERGY_DECLARATION";

    /*

     */
    public EnergyPassportSectionTemplateFactory section_1_2() {


        final PDTable pdTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_1.2")
            .caption("ИНФОРМАЦИЯ\nо потреблении энергетических ресурсов организации\n" +
                "за 20__ год")
            .shortCaption("1.2")
            .sectionNr("1.2")
            .sectionHeader("ИНФОРМАЦИЯ\n" +
                "              о потреблении энергетических ресурсов в здании\n" +
                "                        (строении, сооружении) <*>\n" +
                "                                за 20__ год");


        createSectionPartsM1(pdTable);

        /// Inner 2
        pdTable.createPartLine("P_2","")
            .and().createStaticElement("Общие сведения о потреблении энергетических ресурсов в отчетном году");
        createSectionPartInnerM2(pdTable);


        pdTable.createPartLine("P_3","")
            .and().createStaticElement("Наличие собственного источника выработки энергии (есть/нет)")
            .and().createBooleanValueElement();

        // Inner 3
        createSectionPartInnerM31(pdTable);

        pdTable.createPartLine().and().createStaticElement().and().createStaticElement("Вид вырабатываемой энергии:");

        createSectionPartInnerM32(pdTable);

        pdTable.createPartLine("P_3_comb","")
            .and().createStaticElement("Комбинированная выработка (да/нет):")
            .and().createBooleanValueElement();

        pdTable.createPartLine("P_3_mode","")
            .and().createStaticElement("Режим управления работой котлов (автоматический/ручное управление)")
            .and().createBooleanValueElement();


        pdTable.createPartLine()
            .and().createStaticElement()
            .and().createStaticElement("Общие сведения об оплате за энергоресурсы");

        createSectionPartInnerM4(pdTable);

        pdTable.createPartLine("P_5","")
            .and().createStaticElement("Среднесписочная численность (чел.) Всех работников")
            .and().createIntegerValueElement();

        pdTable.createPartLine("P_6","")
            .and().createStaticElement("Программа энергосбережения и повышения энергетической эффективности\n" +
            "Наличие утвержденной программы энергосбережения\n" +
            "(есть/нет)").and().createBooleanValueElement();

        pdTable.createPartLine("P_7_build","")
            .and().createStaticElement("Количество зданий обследуемой организации").and().createIntegerValueElement();

        return new EPSectionTemplateFactory(pdTable);

    }


    private PDTable createSectionPartsM1(PDTable pdTable) {
        pdTable.createPartLine("1.").and()
            .createStaticElement("Наименование организации").and().createStringValueElement();

        pdTable.createPartLine("1.1").and()
            .createStaticElement("Организационно-правовая форма").and().createStringValueElement();

        pdTable.createPartLine("1.2").and()
            .createStaticElement("Почтовый адрес организации").and().createStringValueElement();

        pdTable.createPartLine("1.3").and()
            .createStaticElement("Место нахождения").and().createStringValueElement();

        pdTable.createPartLine("1.4").and()
            .createStaticElement("Полное  наименование  организации  (основного общества - для дочерних \n" +
                "(зависимых) обществ) ").and().createStringValueElement();

        pdTable.createPartLine("1.5").and()
            .createStaticElement("Доля государственной (муниципальной) собственности в уставном\n" +
                "капитале организации, % \n").and().createStringValueElement();

        pdTable.createPartLine("1.6").and()
            .createStaticElement("ИНН").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("ОГРН").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2);

        pdTable.createPartLine("1.7").and()
            .createStaticElement("Код по ОКВЭД").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("код по ОКОГУ").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2);

        pdTable.createPartLine("1.8").and()
            .createStaticElement("Ф.И.О., должность руководителя").and().createStringValueElement();

        pdTable.createPartLine("1.9").and()
            .createStaticElement(".И.О., должность, телефон, факс,  адрес\n" +
                "      электронной  почты  должностного   лица,\n" +
                "      ответственного за техническое  состояние\n" +
                "      оборудования\n").and().createStringValueElement();

        pdTable.createPartLine("1.10").and()
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

        innerTable.createPartRowInner(".heat")
            .createStaticElement("Тепловая энергия").and()
            .createStaticElement("(Гкал/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Тепловая энергия").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPartRowInner(".electricity")
            .createStaticElement("Электрическая энергия").and()
            .createStaticElement("(кВт·ч/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Электрическая энергия").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPartRowInner(".gas")
            .createStaticElement("Газ").and()
            .createStaticElement("(тыс. куб. м/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Газ").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPartRowInner(".liquid")
            .createStaticElement("Жидкое топливо").and()
            .createStaticElement("(т/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Жидкое топливо").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPartRowInner(".solid")
            .createStaticElement("Твердое топливо").and()
            .createStaticElement("(т/год)").keyValueIdx(1).and().createDoubleValueElement().keyValueIdx(1)
            .and()
            .createStaticElement("Твердое топливо").and()
            .createStaticElement("(т у.т./год)").keyValueIdx(2).and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPartRowInner(".motor")
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

        innerTable.createPartRowInner("_gas").createStaticElement("Газ").keyValueIdx(1)
            .and().createBooleanValueElement().keyValueIdx(1).and().createStaticElement("годовой расход").keyValueIdx(2)
            .and().createStaticElement("(тыс. куб. м/год)")
            .and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPartRowInner("_liquid").createStaticElement("Жидкое топливо").keyValueIdx(1)
            .and().createBooleanValueElement().keyValueIdx(1).and().createStaticElement("годовой расход").keyValueIdx(2)
            .and().createStaticElement("(т/год)")
            .and().createDoubleValueElement().keyValueIdx(2);

        innerTable.createPartRowInner("_solid").createStaticElement("Твердое топливо").keyValueIdx(1)
            .and().createBooleanValueElement().keyValueIdx(1).and().createStaticElement("годовой расход").keyValueIdx(2)
            .and().createStaticElement("(т/год)")
            .and().createDoubleValueElement().keyValueIdx(2);
        return innerTable;
    }

    /**
     *
     * @param pdTable
     * @return
     */
    private PDTable createSectionPartInnerM32(PDTable pdTable) {
        PDInnerTable innerTable = pdTable.createPart(PDPartType.INNER_TABLE).key("P_3_prod").createInnerTable();

        innerTable.createPartRowInner("_row")
            .createStaticElement("Электрическая").keyValueIdx(1).and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Тепловая: пар").keyValueIdx(2).and().createStringValueElement().keyValueIdx(2)
            .and().createStaticElement("Тепловая:").keyValueIdx(3).and().createStringValueElement().keyValueIdx(3);

        innerTable.createPartRowInner("_row_yn")
            .createStaticElement("(да/нет)").keyValueIdx(1).and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("(да/нет)").keyValueIdx(2).and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("(да/нет)").keyValueIdx(3).and().createBooleanValueElement().keyValueIdx(3);
        return innerTable;
    }

    /**
     *
     * @param pdTable
     * @return
     */
    private PDTable createSectionPartInnerM4(PDTable pdTable) {
        PDInnerTable innerTable = pdTable.createPart(PDPartType.INNER_TABLE).key("P_4").createInnerTable();

        innerTable.createPartRowInner("_heat")
            .createStaticElement("Тепловая энергия").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        innerTable.createPartRowInner("_electricity")
            .createStaticElement("Электрическая энергия").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        innerTable.createPartRowInner("_gas")
            .createStaticElement("Газ").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        innerTable.createPartRowInner("_liquid")
            .createStaticElement("Жидкое топливо").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        innerTable.createPartRowInner("_solid")
            .createStaticElement("Твердое топливо").keyValueIdx(1).and().createStaticElement("(руб./год)")
            .and().createDoubleValueElement().keyValueIdx(1);

        return innerTable;
    }


}
