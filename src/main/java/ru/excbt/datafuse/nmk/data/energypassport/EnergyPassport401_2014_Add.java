package ru.excbt.datafuse.nmk.data.energypassport;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.passdoc.*;
import ru.excbt.datafuse.nmk.passdoc.PDCellStyle.HAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by kovtonyk on 05.04.2017.
 */
@Service
public class EnergyPassport401_2014_Add {


    public final static String ENERGY_DECLARATION = "ENERGY_DECLARATION";


    public EnergyPassportSectionTemplateFactory section_1_1() {


        final PDTable pdTable = new PDTable();
        pdTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("1.1", "ИНФОРМАЦИЯ\n" +
            "ОБ ЭНЕРГОСБЕРЕЖЕНИИ И О ПОВЫШЕНИИ\n" +
            "ЭНЕРГЕТИЧЕСКОЙ ЭФФЕКТИВНОСТИ\n")));

        headerTool(pdTable, "h1-1", "ИНФОРМАЦИЯ\n" +
            "ОБ ЭНЕРГОСБЕРЕЖЕНИИ И О ПОВЫШЕНИИ\n" +
            "ЭНЕРГЕТИЧЕСКОЙ ЭФФЕКТИВНОСТИ\n" +
            "органов государственной власти, органов местного\n" +
            "самоуправления, наделенных правами юридических лиц,\n" +
            "организаций с участием государства\n" +
            "или муниципального образования");

        pdTable.createPartLine("1", "")
            .and().createStringValueElement().cellStyle(PDCellStyle._makeHAligmentCenter().header1());

        headerTool(pdTable, "h1-2", "(полное наименование органа государственной власти,\n" +
            "органа местного самоуправления, юридического лица)\n");

        headerTool(pdTable, "h1-3", "\n" +
            "\"{DD}\" {MONTH1} {YYYY} г.\n" +
            "(число, месяц, год составления) \n");

        return new EPSectionTemplateFactory(pdTable);

    }


    /*

     */
    public EnergyPassportSectionTemplateFactory section_1_2() {


        final PDTable pdTable = new PDTable();
        pdTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("1.2", "ИНФОРМАЦИЯ о потреблении энергетических ресурсов организации")));

        headerTool(pdTable, "h1-", "ИНФОРМАЦИЯ\n" +
            "о потреблении энергетических ресурсов организации\n" +
            "за {YYYY} год");


        createS_1_2PartsM1(pdTable);

        /// Inner 2
        pdTable.createPartLine("P_2","")
            .and().createStaticElement("Общие сведения о потреблении энергетических ресурсов в отчетном году");
        createS_1_2PartInnerM2(pdTable);


        pdTable.createPartLine("P_3","")
            .and().createStaticElement("Наличие собственного источника выработки энергии (есть/нет)")
            .and().createBooleanValueElement();

        // Inner 3
        createS_1_2PartInnerM31(pdTable);

        pdTable.createPartLine().and().createStaticElement().and().createStaticElement("Вид вырабатываемой энергии:");

        createS_1_2PartInnerM32(pdTable);

        pdTable.createPartLine("P_3_comb","")
            .and().createStaticElement("Комбинированная выработка (да/нет):")
            .and().createBooleanValueElement();

        pdTable.createPartLine("P_3_mode","")
            .and().createStaticElement("Режим управления работой котлов (автоматический/ручное управление)")
            .and().createBooleanValueElement();


        pdTable.createPartLine()
            .and().createStaticElement()
            .and().createStaticElement("Общие сведения об оплате за энергоресурсы");

        createS_1_2PartInnerM4(pdTable);

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


    private PDTable createS_1_2PartsM1(PDTable pdTable) {
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
    private PDTable createS_1_2PartInnerM2(PDTable pdTable){

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
    private PDTable createS_1_2PartInnerM31(PDTable pdTable) {
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
    private PDTable createS_1_2PartInnerM32(PDTable pdTable) {
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
    private PDTable createS_1_2PartInnerM4(PDTable pdTable) {
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



    public EnergyPassportSectionTemplateFactory section_1_3() {

        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("1.3", "ИНФОРМАЦИЯ\n" +
            "о потреблении энергетических ресурсов в здании\n" +
            "(строении, сооружении)")));

        headerTool(topTable, "h1-", "ИНФОРМАЦИЯ\n" +
            "о потреблении энергетических ресурсов в здании\n" +
            "(строении, сооружении)\n" +
            "за {YYYY} год \n");


        topTable.createPartLine("1").createStaticElement("Место нахождения")
            .and().createStringValueElement();

        topTable.createPartLine("2").createStaticElement("Тип здания (строения, сооружения) и функциональное назначение:");


        topTable.createPartLine("2a","")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("ОБЩЕСТВЕННОЕ")//.cellStyle(PDCellStyle._makeHAligmentCenter())
                .keyValueIdx(1)
            .and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("ЖИЛОЕ")//.cellStyle(PDCellStyle._makeHAligmentCenter())
                .keyValueIdx(2)
            .and().createBooleanValueElement().keyValueIdx(3)
            .and().createStaticElement("СТРОЕНИЕ, СООРУЖЕНИЕ")//.cellStyle(PDCellStyle._makeHAligmentCenter())
                .keyValueIdx(3);

        topTable.createPartLine("2b", "")
            .and().createStaticElement("Здравоохранение").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(1)

            .and().createStaticElement("Жилой дом").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(2)

            .and().createStringValueElement().keyValueIdx(3)
            .and().createBooleanValueElement().keyValueIdx(4);

        topTable.createPartLine("2c", "")
            .and().createStaticElement("Воспитание и обучение").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(1)

            .and().createStaticElement("Общежитие").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(2)

            .and().createStringValueElement().keyValueIdx(3)
            .and().createBooleanValueElement().keyValueIdx(4);


        topTable.createPartLine("2d", "")
            .and().createStaticElement("Наука").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(1)

            .and().createStaticElement("Гостиница").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(2)

            .and().createStringValueElement().keyValueIdx(3)
            .and().createBooleanValueElement().keyValueIdx(4);


        topTable.createPartLine("2e", "")
            .and().createStaticElement("Управленческо- административное").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(1)

            .and().createStaticElement("Другое").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(2)

            .and().createStringValueElement().keyValueIdx(3)
            .and().createBooleanValueElement().keyValueIdx(4);

        topTable.createPartLine("2f", "")
            .and().createStaticElement("Другое").cellStyle(PDCellStyle._makeHAligmentCenter())
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStringValueElement().keyValueIdx(2);

        topTable.createPartLine("3")
            .and().createStaticElement("Техническое описание объекта (да/нет/значение показателя)");

        topTable.createPartLine("3.1")
            .and().createStaticElement("Общая площадь")
            .and().createStaticElement(EPConstants.SQW_M)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.2")
            .and().createStaticElement("Этажность")
            .and().createStaticElement(EPConstants.PCS)
            .and().createIntegerValueElement();

        topTable.createPartLine("3.2.1")
            .and().createStaticElement("Количество лифтов, год установки/   (шт.)\n" +
            "замены")
            .and().createStaticElement(EPConstants.PCS)
            .and().createIntegerValueElement();


        topTable.createPartLine("3.3")
            .and().createStaticElement("Отапливаемая площадь")
            .and().createStaticElement(EPConstants.SQW_M)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.4")
            .and().createStaticElement("Полезная площадь")
            .and().createStaticElement(EPConstants.SQW_M)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.5")
            .and().createStaticElement("Общий объем")
            .and().createStaticElement(EPConstants.CUB_M)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.6")
            .and().createStaticElement("Год ввода в эксплуатацию")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("3.7")
            .and().createStaticElement("Фактический износ")
            .and().createStaticElement(EPConstants.PERC)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.8")
            .and().createStaticElement("Год проведения последнего капитального ремонта")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("3.8.1")
            .and().createStaticElement("Год проведения последнего текущего ремонта")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.8.2")
            .and().createStaticElement("Объем инвестиций на капитальный ремонт")
            .and().createStaticElement(EPConstants.KRUB)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.8.2a","")
            .and().createStaticElement("В том числе из внебюджетных источников")
            .and().createStaticElement(EPConstants.KRUB)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.9")
            .and().createStaticElement("Планируется ли проведение капитального ремонта")
            .and().createStaticElement(EPConstants.YN)
            .and().createBooleanValueElement();

        topTable.createPartLine("3.9.1")
            .and().createStaticElement("Планируемый объем инвестиций")
            .and().createStaticElement(EPConstants.KRUB)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.9.1a","")
            .and().createStaticElement("В том числе из внебюджетных источников")
            .and().createStaticElement(EPConstants.KRUB)
            .and().createDoubleValueElement();

        topTable.createPartLine("3.9.2")
            .and().createStaticElement("Основные цели капитального ремонта");

        topTable.createPartLine("3.9.2.1")
            .and().createStaticElement("Замена изношенных конструктивных элементов" +
            " и улучшение эксплуатационных характеристик")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.9.2.2")
            .and().createStaticElement("Повышение энергоэффективности")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.9.2.3").createStaticElement("Достижение нормативных показателей " +
            "энергопотребления")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.9.3").createStaticElement("Ожидаемый эффект снижения потребления топливно-энергетических ресурсов" +
            " (далее - ТЭР)")
            .and().createStaticElement(EPConstants.TUT_YEAR)
            .and().createStringValueElement();

        topTable.createPartLine("3.10").createStaticElement("Наружные стены");

        topTable.createPartLine()
            .and().createStaticElement()
            .and().createStaticElement("Материал наружных стен (есть/нет)");

        topTable.createPartLine("3.10a","")
            .and().createStaticElement("Кирпич")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Бетон")
            .and().createStringValueElement().keyValueIdx(2);

        topTable.createPartLine("3.10b","")
            .and().createStaticElement("Прочий каменный")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Деревянный")
            .and().createStringValueElement().keyValueIdx(2);

        topTable.createPartLine("3.10c","")
            .and().createStaticElement("Деревянно-каменный")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Металлический")
            .and().createStringValueElement().keyValueIdx(2);

        topTable.createPartLine("3.10d","")
            .and().createStaticElement("Деревянно-каменный")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Металлический")
            .and().createStringValueElement().keyValueIdx(2);

        topTable.createPartLine();

        topTable.createPartLine("3.10e","")
            .and().createStaticElement("Прочий")
            .and().createStringValueElement();

        topTable.createPartLine("3.10f","")
            .and().createStaticElement("С теплоизолированным (утепленным) фасадом")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Без утепления фасадов теплоизоляционным материалом")
            .and().createStringValueElement().keyValueIdx(2);

        section_helper1(topTable,"3.11",
            "Окна",
            new String[]{"Тип оконных блоков",
                "Деревянные рамы:",
                "- одинарные",
                "- двойные",
                "Энергосберегающие стеклопакеты:",
                "- однокамерные",
                "- двухкамерные (многокамерные)",
                "- двухкамерные (многокамерные) с напылением",
                "Другие (указать, какие)",
                "Остекление энергосберегающими стеклопакетами (% от общего остекления)",
            },
            null,
            PDTableCellValueString.class);


        section_helper1(topTable,"3.12",
            "Входные двери (есть/нет/количество)",
            new String[]{"Одинарные",
                "Двойные",
                "Количество входов",
                "Из них оборудованы:",
                "- тамбуром",
                "- доводчиком",
                "- тепловой завесой в рабочем состоянии",
                "- тепловой завесой с регулированием включения и отключения",
                "- автоматизацией отключения тепловой завесы"
            },
            null,
            PDTableCellValueString.class);

        section_helper1(topTable,"3.13.",
            "Крыша (есть/нет)",
            new String[]{"Без чердачного помещения",
                "С чердачным помещением",
                "В том числе:",
                "- с холодным чердаком",
                "- с утепленным чердаком",
                "Утепление крыши",
                "Плоская (мягкая) кровля:",
                "- с однослойной системой теплоизоляции типовое решение)",
                "- наличие технического этажа",
                "- с двухслойной системой теплоизоляции",
                "- наличие технического этажа",
                "Металлическая:",
                "- без утепления крыши изнутри",
                "- без утепления чердачного помещения",
                "- с утеплением крыши изнутри",
                "- с утеплением чердачного помещения",
                "Наличие протечек (конденсата) на потолке верхнего этажа",
                "Отсутствие протечек (конденсата) на потолке верхнего этажа",
                "Отсутствие наледи на крыше (во время отопительного сезона)",
                "Наличие наледи на крыше (во время отопительного сезона)"
            },
            null,
            PDTableCellValueBoolean.class);


        section_helper1(topTable,"3.14.",
            "Подвальные помещения (есть/нет)",
            new String[]{"Без подвального помещения",
            "С холодным подвалом",
            "С теплым подвалом",
            "Сырые",
            "В сухом состоянии",
            "Стены не промерзают",
            "Стены промерзают",
            "Имеется остекление"},
            null,
            PDTableCellValueBoolean.class);

        topTable.createPartLine("3.15")
            .and().createStaticElement("Подключение к сетям инженерно-технического обеспечения (есть/нет)");

        topTable.createPartLine("3.15a","")
            .and().createStaticElement("ЭЛЕКТРИЧЕСТВО")
            //.and().createStaticElement()
            .and().createStaticElement("ГАЗОСНАБЖЕНИЕ");

        topTable.createPartLine("3.15b","")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("Центральное")
            .and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("Центральное");

        topTable.createPartLine("3.15c","")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("Автономное")
            .and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("Автономное");

        topTable.createPartLine("3.15d","")
            .and().createStaticElement("ТЕПЛОСНАБЖЕНИЕ")
            //.and().createStaticElement()
            .and().createStaticElement("ПРОЧЕЕ");

        topTable.createPartLine("3.15e","")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("Центральное")
            .and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("Центральное");

        topTable.createPartLine("3.15f","")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("Автономное")
            .and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("Автономное");


        section_helper1(topTable,"3.16.",
            "Присоединение к магистральной тепловой сети (при отсутствии собственного источника) (есть/нет)",
            new String[]{"Групповое (центральный тепловой пункт)(далее - ЦТП)",
            "Групповое (центральный тепловой пункт)(далее - ЦТП)",
            "Индивидуальное с автоматизацией отопленияи горячего водоснабжения (далее - АИТП)",
            "Присоединение системы отопления:",
            "- зависимое",
            "- независимое"},
            null,
            PDTableCellValueBoolean.class);


        topTable.createPartLine("4")
            .and().createStaticElement("Сведения о потреблении энергоресурсов в базовом году");

        topTable.createPartLine("4a","")
            .and().createStaticElement("Тепловая энергия")
            .and().createStaticElement(EPConstants.GCAL_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4b","")
            .and().createStaticElement("Электрическая энергия")
            .and().createStaticElement(EPConstants.KWH_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4c","")
            .and().createStaticElement("Газ")
            .and().createStaticElement(EPConstants.KVM_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4d","")
            .and().createStaticElement("Жидкое топливо")
            .and().createStaticElement(EPConstants.T_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4e","")
            .and().createStaticElement("Твердое топливо")
            .and().createStaticElement(EPConstants.T_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4f","")
            .and().createStaticElement("Моторное топливо")
            .and().createStaticElement(EPConstants.L_YEAR)
            .and().createDoubleValueElement();


        topTable.createPartLine("5")
            .and().createStaticElement("Тарифы на оплату энергетических ресурсов");

        topTable.createPartLine("5a","")
            .and().createStaticElement("Тепловая энергия")
            .and().createStaticElement(EPConstants.RUB_GCAL)
            .and().createDoubleValueElement();

        topTable.createPartLine()
            .and().createStaticElement()
            .and().createStaticElement("Электрическая энергия:");

        topTable.createPartLine("5b","")
            .and().createStaticElement("- одноставочный тариф")
            .and().createStaticElement(EPConstants.RUB_KWH)
            .and().createDoubleValueElement();

        topTable.createPartLine("5c","")
            .and().createStaticElement("- двуставочный тариф")
            .and().createStaticElement(EPConstants.RUB_KWH)
            .and().createDoubleValueElement();

        topTable.createPartLine("5d","")
            .and().createStaticElement()
            .and().createStaticElement(EPConstants.RUB_KWH)
            .and().createDoubleValueElement();

        topTable.createPartLine("5e","")
            .and().createStaticElement("Газ")
            .and().createStaticElement(EPConstants.KVM_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("5f","")
            .and().createStaticElement("Твердое топливо")
            .and().createStaticElement(EPConstants.RUB_T)
            .and().createDoubleValueElement();

        topTable.createPartLine("5g","")
            .and().createStaticElement("Моторное топливо")
            .and().createStaticElement(EPConstants.RUB_T)
            .and().createDoubleValueElement();


        Consumer<PDTablePart> createStaticRub = (p) -> p.createStaticElement(EPConstants.RUB_YEAR);
        section_helper1(topTable, "6.",
            new String[] {"Оплата энергетических ресурсов"},
            create1EmptyStaticF,
            new String[]{"Тепловая энергия",
                "Электрическая энергия",
                "Газ",
                "Жидкое топливо",
                "Твердое топливо",
                "Моторное топливо"
            },
            createStaticRub,
            null,
            PDTableCellValueDouble.class);


        topTable.createPartLine("7")
            .and().createStaticElement("Сведения об оснащенности приборами учета");

        section_helper1(topTable, "7.1.",
            "Коммерческий учет",
            new String[]{"Количество вводов тепловой энергии:",
                "- количество вводов, оборудованных узлами коммерческого учета",
            "- в составе автоматизированной информационной системы (далее - АИС)",
            "из них в составе:",
            "- индивидуального учета (на здание)",
            "- в составе группового учета (на несколько зданий)",
            "Количество вводов электрической энергии:",
            "- количество вводов, оборудованных узлами коммерческого учета",
            "- в составе АИС",
            "из них в составе:",
            "- индивидуального учета (на здание)",
            "- группового учета (на несколько зданий)",
            "Количество вводов по газу:",
            "- количество вводов, оборудованных узлами коммерческого учета",
            "- в составе АИС",
            "из них в составе:",
            "- индивидуального учета (на здание)",
            "- группового учета (на несколько зданий)",
            "из них в составе:",
            "- индивидуального учета (на здание)",
            "- группового учета (на несколько зданий)"
            },
            null,
            PDTableCellValueInteger.class);



        section_helper1(topTable,"7.2.", "Технический учет",
            new String[]{"Суммарное количество узлов технического учета:",
                        "- по тепловой энергии",
                "- по электрической энергии",
                "- по газу"},
            PDTableCellValueBoolean.class,
            PDTableCellValueInteger.class);



        topTable.createPartLine("8.")
            .and().createStaticElement("Система теплопотребления");

        section_helper1(topTable,"8.1.", "Способ присоединения системы горячего водоснабжения:",
            new String[]{"- открытый", "- закрытый"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);


        section_helper1(topTable,"8.2.", "Схема разводки трубопроводов системы отопления:",
            new String[]{"- однотрубная", "- двухтрубная"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "8.3.", "Регулирование отопительной нагрузки в тепловом пункте",
            new String[]{"- элеваторный узел", "- узел автоматизированного устройства управления", "- ИТП"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "8.4.", "Отопительные приборы:",
            new String[]{"- чугунные", "- биметаллические", "- с термостатическим регулированием расхода"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "8.5.", "Температурный режим в помещениях:",
            new String[]{"- соответствует санитарно-эпидемиологическим требованиям",
                "с возможностью индивидуального регулирования", "без возможности индивидуального",
                "- не соответствует санитарно-эпидемиологическим требованиям", "с возможностью использования дополнительных электронагревателей"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "8.6.",
            new String[]{"Централизованная приточно-вытяжная вентиляция", EPConstants.YN},
            new String[]{"- в работающем состоянии",
                "- с регулированием включения и отключения"},
            PDTableCellValueBoolean.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "8.7.",
            new String[]{"Система регулирования горячего водоснабжения (далее - ГВС)", EPConstants.YN},
            new String[]{"- с регулированием расхода",
                "- с циркуляционным контуром горячей воды"},
            PDTableCellValueBoolean.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "8.8.",
            "Состояние распределительных тепловых коммуникаций:",
            new String[]{"- с теплоизоляцией труб в подвальных помещениях",
                "- теплоизоляция труб в подвальных помещениях отсутствует",
                "- с теплоизоляцией труб чердачного помещения",
                "- теплоизоляция труб чердачного помещения отсутствует"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);


        topTable.createPartLine("9.")
            .and().createStaticElement("Система электропотребления ")
            .and().createStaticElement("(да (нет)/количество)");

        topTable.createPartLine("9.1.")
            .and().createStaticElement("Внутреннее освещение")
            .and().createStaticElement()
            .and().createStringValueElement();


        section_helper1(topTable, "9.1.1.",
            "Используемые источники света:",
            new String[]{"- лампы накаливания (шт.)",
                "- люминесцентные лампы (шт.)",
                "- светодиодные лампы (шт.)"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "9.1.2.",
            "Управление внутренним освещением:",
            new String[]{"- централизованное включение/отключение",
                "- датчики движения",
                "- датчики освещенности",
                "- ручное"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "9.1.3.",
            "Уровень освещенности:",
            new String[]{"- соответствует санитарно-эпидемиологическим требованиям",
                "- не соответствует санитарно-эпидемиологическим требованиям"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        topTable.createPartLine("9.2.")
            .and().createStaticElement("Освещение - общие характеристики")
            .and().createStaticElement()
            .and().createStringValueElement();

        section_helper1(topTable, "9.2.1.",
            "Лампы накаливания в местах общего пользования:",
            new String[]{"- доля ламп накаливания более 50%",
                "- доля ламп накаливания 50% и менее"},
            PDTableCellValueString.class,
            PDTableCellValueDouble.class);

        section_helper1(topTable, "9.2.2.",
            "Люминесцентные лампы:",
            new String[]{"- светильниками с зеркальными отражателями оснащено 90% люминесцентных ламп и более",
                "- светильниками с зеркальными отражателями оснащено до 90% люминесцентных ламп",
                "- светильниками с зеркальными отражателями оснащено до 50% люминесцентных ламп",
                "- светильниками с зеркальными отражателями оснащено менее 20% люминесцентных ламп"},
            PDTableCellValueString.class,
            PDTableCellValueDouble.class);

        section_helper1(topTable, "9.2.3.",
            "Светодиодные светильники:",
            new String[]{"- отсутствуют",
                "- 20% от всех ламп и более"},
            PDTableCellValueString.class,
            PDTableCellValueDouble.class);

        topTable.createPartLine("9.3.")
            .and().createStaticElement("Наружное освещение")
            .and().createStaticElement()
            .and().createStringValueElement();

        section_helper1(topTable, "9.3.1.",
            "Используемые источники света (шт.):",
            new String[]{"- лампы накаливания (шт.)",
                "- люминесцентные лампы (шт.)",
                "- светодиодные лампы (шт.)",
                "- дуговые ртутные люминесцентные лампы (лампы типа ДРЛ) (шт.)"},
            PDTableCellValueString.class,
            PDTableCellValueInteger.class);

        section_helper1(topTable, "9.3.2.",
            "Управление наружным освещением:",
            new String[]{"- централизованное включение/отключение",
                "- датчики движения",
                "- датчики освещенности",
                "- ручное"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "9.4.",
            "Вентиляция принудительная (есть/нет)",
            new String[]{"Год установки",
                "Число часов работы в неделю",
                "Год ввода в эксплуатацию",
                "Год проведения ремонта",
                "Управление таймером",
                "Автоматизированное управление"},
            PDTableCellValueBoolean.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "9.5.",
            "Система кондиционирования воздуха (есть/нет/количество)",
            new String[]{"централизованная",
                "сплит-системы",
                "- количество сплит-систем"},
            PDTableCellValueBoolean.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "9.6.",
            "Кухонное оборудование (есть/нет)",
            new String[]{"индукционные плиты",
                "другие плиты",
                "Пароконвектоматы",
                "Другой разогрев пищи"},
            PDTableCellValueBoolean.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "9.7.",
            "Насосное оборудование (холодного водоснабжения, горячего водоснабжения) (есть/нет)",
            new String[]{"Регулируемый привод",
                "Нерегулируемый привод"},
            PDTableCellValueBoolean.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "9.8.",
            new String[] {"Офисная, бытовая и специальная техника (по профилю объекта), класс энергетической эффективности (есть/нет)"},
            create1EmptyStaticF,
            new String[]{"A", "B", "C", "D", "E", "F", "G"},
            create1EmptyStaticF,
            PDTableCellValueBoolean.class,
            PDTableCellValueBoolean.class);

        itemCreator(topTable, "9.8.",
            create1EmptyStaticF,
            new String[]{"Отсутствие техники с классом энергоэффективности A+ и A++",
                "Количество техники с классом энергоэффективности A+ или A++ менее 50%",
                "Количество техники с классом энергоэффективности A+ или A++ от 50% до 70%",
                "Вся техника с классом энергоэффективности A+ и A++"},
            create1EmptyStaticF,
            PDTableCellValueString.class,
            7,
            null);

        // Next L

        topTable.createPartLine("9.8l", false)
            .and().createStaticElement()
            .and().createStaticElement("электрические чайники (да/нет)")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("количество")
            .and().createIntegerValueElement().keyValueIdx(2);

        topTable.createPartLine("9.8m", false)
            .and().createStaticElement()
            .and().createStaticElement("электрические обогреватели (да/нет)")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("количество")
            .and().createIntegerValueElement().keyValueIdx(2);

        topTable.createPartLine("9.8n", false)
            .and().createStaticElement()
            .and().createStaticElement("специальное оборудование (по профилю объекта)")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("количество")
            .and().createIntegerValueElement().keyValueIdx(2);


        section_helper1(topTable, "9.9.",
            "Лифты (есть/нет):",
            new String[]{"- год установки до 1980 г.",
                "- год установки с 1980 г. до 2000 г.",
                "- год установки с 2000 г. до 2005 г.",
                "- год установки с 2005 г. по настоящее время"},
            PDTableCellValueBoolean.class,
            PDTableCellValueBoolean.class);

        topTable.createPartLine("10.")
            .and().createStaticElement("Холодное водоснабжение")
            .and().createStaticElement()
            .and().createStringValueElement();

        section_helper1(topTable, "10.1.",
            "Сантехническое оборудование:",
            new String[]{"- отсутствие унитазов с экономным сливом воды",
                "- наличие унитазов с экономным сливом воды"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "10.1.1.",
            "Состояние сантехнического оборудования:",
            new String[]{"- водяные клапаны унитазов пропускают воду",
                "- водяные клапаны унитазов не пропускают воду"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "10.2.",
            "Состояние сантехнической арматуры:",
            new String[]{"- наличие шаровых кранов вместо вентильных",
                "- краны для мытья рук полностью перекрывают воду",
            "- краны для мытья рук не полностью перекрывают воду",
            "- наличие регуляторов подачи воды для мытья рук"},
            PDTableCellValueString.class,
            PDTableCellValueString.class);

        section_helper1(topTable, "11.",
            "Сведения об использовании вторичных " +
                "энергетических ресурсов, альтернативных" +
                "(местных) топлив и возобновляемых источников" +
                "энергии (есть/нет)",
            new String[]{"Источник вторичного (теплового) энергетического ресурса",
                "- тепла отходящих газов (воздуха), воды",
                "Альтернативный (местный) вид ТЭР",
                "Возобновляемый источник энергии",
                "- ветро-, гидроэнергетика, геотермальные установки"},
            PDTableCellValueBoolean.class,
            PDTableCellValueBoolean.class);

        section_helper1(topTable, "12.",
            "Экология материалов и оборудования",
            new String[]{"Материалы и конструкции имеют экологические сертификаты",
            "Наличие экологических сертификатов бытового оборудования и оргтехники"},
            null,
            PDTableCellValueBoolean.class);

        section_helper1(topTable, "13.",
            "Среднесписочная численность (человек)",
            new String[]{"- всех сотрудников"},
            null,
            PDTableCellValueInteger.class);

        section_helper1(topTable, "14.",
            "Внедрение энергосберегающих мероприятий по программе энергосбережения (да/нет)",
            new String[]{"Были ли внедрены мероприятия в отчетном году",
            "Планируется ли внедрение мероприятий в будущем году"},
            null,
            PDTableCellValueBoolean.class);

        topTable.createPartLine("15.")
            .and().createStaticElement("Качество контроля и управления комфортностью здания")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("15.1.")
            .and().createStaticElement("Централизованная система диспетчеризации с возможностью индивидуального" +
            " (зонального) регулирования")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("15.2.")
            .and().createStaticElement("Локальные системы автоматизации систем" +
            " инженерного обеспечения")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        return new EPSectionTemplateFactory(topTable);
    }


    private String removeLastPoint (String nr) {
        return (nr.length() > 0 && nr.charAt(nr.length() - 1) == '.' ? nr.substring(0, nr.length() - 1) : nr);
    }
    private final static Consumer<PDTablePart> create1EmptyStaticF = (p) -> p.createStaticElement();
    private final static Consumer<PDTablePart> create2EmptyStaticF = (p) -> p.createStaticElement().and().createStaticElement();
    private final static TriConsumer<PDTable, String, String> tableCreateLineHeader = (t, nr, s) ->
        t.createPartLine(nr, "")
            .and().createStaticElement(s);


    /**
     *
     * @param pdTable
     * @param masterNr
     * @param beforeItem
     * @param points
     * @param afterItem
     * @param valueType
     * @param valueInitializer
     * @param startIdx
     * @param <V>
     */
    private <V extends PDTableCell<V>> void itemCreator (PDTable pdTable,
                                                         String masterNr,
                                                         Consumer<PDTablePart> beforeItem,
                                                         String[] points,
                                                         Consumer<PDTablePart> afterItem,
                                                         final Class<V> valueType,
                                                         int startIdx,
                                                         Consumer<V> valueInitializer) {
        String alfa = "abcdefghijklmnopqrstuwxyz";

        Preconditions.checkState(points.length < alfa.length());
        Preconditions.checkState(startIdx < alfa.length());

        int idx = startIdx;
        for (String s : points) {
            PDTablePart itemPart = pdTable.createPartLine(removeLastPoint(masterNr) + alfa.charAt(idx++), false);
            if (beforeItem != null) beforeItem.accept(itemPart);
            itemPart.createStaticElement(s);
            if (afterItem != null) afterItem.accept(itemPart);
            V value = itemPart.createValueElement(valueType);
            if (valueInitializer != null) valueInitializer.accept(value);
        }
    }


    private <V extends PDTableCell<V>> void itemCreator (PDTable pdTable,
                                                         String masterNr,
                                                         Consumer<PDTablePart> beforeItem,
                                                         String[] points,
                                                         Consumer<PDTablePart> afterItem,
                                                         final Class<V> valueType) {
        itemCreator (pdTable,masterNr,beforeItem,points,afterItem,valueType,0, null);
    }

    private <M extends PDTableCell<M>, V extends PDTableCell<V>> void section_helper1(PDTable pdTable,
                                                                                      String masterNr,
                                                                                      String[] partHeader,
                                                                                      Consumer<PDTablePart> beforeItem,
                                                                                      String[] points,
                                                                                      Consumer<PDTablePart> afterItem,
                                                                                      final Class<M> masterValueType,
                                                                                      final Class<V> valueType) {
        {
            PDTablePart masterPart =
                pdTable.createPartLine(removeLastPoint(masterNr))
                    .and().createStaticElement(partHeader[0]).and();

            if (partHeader.length > 1) {
                masterPart.createStaticElement(partHeader[1]).and();
            } else {
                masterPart.createStaticElement();
            }

            if (masterValueType != null)
                masterPart.createValueElement(valueType);

        }

        itemCreator(pdTable, masterNr, beforeItem, points, afterItem, valueType, 0, null);

    }

    private <M extends PDTableCell<M>, V extends PDTableCell<V>> void section_helper1(PDTable pdTable,
                                                                                      String masterNr,
                                                                                      String[] partHeaders,
                                                                                      String[] points,
                                                                                      final Class<M> masterValueType,
                                                                                      final Class<V> valueType) {

        section_helper1(pdTable, masterNr, partHeaders, create1EmptyStaticF, points, create1EmptyStaticF, masterValueType, valueType);
    }


    private <M extends PDTableCell<M>, V extends PDTableCell<V>> void section_helper1(PDTable pdTable,
                                                                                      String masterNr,
                                                                                      String partHeader,
                                                                                      String[] points,
                                                                                      final Class<M> masterValueType,
                                                                                      final Class<V> valueType) {
        section_helper1(pdTable, masterNr, new String[]{partHeader}, create1EmptyStaticF, points, create1EmptyStaticF, masterValueType, valueType);
    }


    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_1_4() {

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("1.4", "ИНФОРМАЦИЯ\n" +
            "о потреблении энергетических ресурсов на производство " +
            "продукции (работ, услуг)")));

        headerTool(topTable, "h1-", "ИНФОРМАЦИЯ\n" +
            "о потреблении энергетических ресурсов на производство " +
            "продукции (работ, услуг)\n" +
            "за {YYYY} год\n");


        topTable.createPartLine("1.")
            .and().createStaticElement("Промышленное производство (цех, участок)")
            .and().createStringValueElement();

        topTable.createPartLine("1.1.")
            .and().createStaticElement("Отраслевая принадлежность")
            .and().createStringValueElement();

        topTable.createPartLine("1.2.")
            .and().createStaticElement("Основные виды продукции")
            .and().createStringValueElement();

        topTable.createPartLine("P_1_2b","")
            .and().createStaticElement()
            .and().createStaticElement("Код основной продукции (работ, услуг) по ОКП")
            .and().createStringValueElement();

        topTable.createPartLine()
            .and().createStaticElement("2.")
            .and().createStaticElement("Сведения о потреблении энергоресурсов по номенклатуре основной продукции\n" +
                "(работам, услугам)\n");

        {
            final PDInnerTable innerTable = topTable.createPartInnerTable().key("P_2").createInnerTable();

            innerTable.createPartRowInner("_heat")
                .createStaticElement("Тепловая энергия").keyValueIdx(1).and().createStaticElement(EPConstants.GCAL_YEAR)
                .and().createDoubleValueElement().keyValueIdx(1)
                .and().createStaticElement("Тепловая энергия").keyValueIdx(2).and().createStaticElement(EPConstants.TUT_YEAR)
                .and().createDoubleValueElement().keyValueIdx(2)
                .and().widthsOfElements(30, 20, 10, 30, 20, 10);

            innerTable.createPartRowInner("_electricity")
                .createStaticElement("Электрическая энергия").keyValueIdx(1).and().createStaticElement(EPConstants.KWH_YEAR)
                .and().createDoubleValueElement().keyValueIdx(1)
                .and().createStaticElement("Электрическая энергия").keyValueIdx(2).and().createStaticElement(EPConstants.TUT_YEAR)
                .and().createDoubleValueElement().keyValueIdx(2);

            innerTable.createPartRowInner("_gas")
                .and().createStaticElement("Газ").keyValueIdx(1).and().createStaticElement(EPConstants.KVM_YEAR)
                .and().createDoubleValueElement().keyValueIdx(1)
                .and().createStaticElement("Газ").keyValueIdx(2).and().createStaticElement(EPConstants.TUT_YEAR)
                .and().createDoubleValueElement().keyValueIdx(2);

            innerTable.createPartRowInner("_liquid")
                .and().createStaticElement("Жидкое топливо").keyValueIdx(1).and().createStaticElement(EPConstants.T_YEAR)
                .and().createDoubleValueElement().keyValueIdx(1)
                .and().createStaticElement("Жидкое топливо").keyValueIdx(2).and().createStaticElement(EPConstants.TUT_YEAR)
                .and().createDoubleValueElement().keyValueIdx(2);

            innerTable.createPartRowInner("_solid")
                .and().createStaticElement("Твердое топливо").keyValueIdx(1).and().createStaticElement(EPConstants.T_YEAR)
                .and().createDoubleValueElement().keyValueIdx(1)
                .and().createStaticElement("Твердое топливо").keyValueIdx(2).and().createStaticElement(EPConstants.TUT_YEAR)
                .and().createDoubleValueElement().keyValueIdx(2);

            innerTable.createPartRowInner("_motor")
                .and().createStaticElement("Моторное топливо").keyValueIdx(1).and().createStaticElement(EPConstants.L_YEAR)
                .and().createDoubleValueElement().keyValueIdx(1)
                .and().createStaticElement("Моторное топливо").keyValueIdx(2).and().createStaticElement(EPConstants.TUT_YEAR)
                .and().createDoubleValueElement().keyValueIdx(2);

        }

        topTable.createPartLine().and().createStaticElement("3.")
            .and().createStaticElement("Объем производства продукции (работ, услуг) в натуральном выражении");

        {
            final PDInnerTable innerTable = topTable.createPartInnerTable().key("P_3").createInnerTable();

            PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);
            partHeader
                .createStaticElement("Вид продукции").keyValueIdx(1)
                .and().createStaticElement()
                .and().createStaticElement("Объем производства").keyValueIdx(2)
                .and().createStaticElement()
                .and().createStaticElement("Ед. изм.").keyValueIdx(3)
                .and().createStaticElement()
                .and().widthsOfElements(30,30,30,30,30,30);

            innerTable.createPartRowInner("_data")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createStaticElement()
                .and().createDoubleValueElement().keyValueIdx(2)
                .and().createStaticElement()
                .and().createStringValueElement().keyValueIdx(3)
                .and().createIntegerValueElement();

        }

        topTable.createPartLine().and().createStaticElement("4.")
            .and().createStaticElement("Объем производства продукции (работ, услуг) в натуральном выражении");

        {
            final PDInnerTable innerTable = topTable.createPartInnerTable().key("P_4").createInnerTable();

            PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);
            partHeader
                .createStaticElement("Вид продукции").keyValueIdx(1)
                .and().createStaticElement()
                .and().createStaticElement("Объем производства").keyValueIdx(2)
                .and().createStaticElement()
                .and().widthsOfElements(30,20,30,20);

            innerTable.createPartRowInner("_data")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createStaticElement()
                .and().createDoubleValueElement().keyValueIdx(2)
                .and().createStaticElement();
        }

        topTable.createPartLine().and().createStaticElement("5.")
            .and().createStaticElement("Объем производства продукции (работ, услуг) в стоимостном выражении");

        {
            final PDInnerTable innerTable = topTable.createPartInnerTable().key("P_5").createInnerTable();

            PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);
            partHeader
                .createStaticElement("Вид продукции").keyValueIdx(1)
                .and().createStaticElement()
                .and().createStaticElement("Объем производства (тыс. руб.) ").keyValueIdx(2)
                .and().createStaticElement()
                .and().widthsOfElements(30,20,30,20);

            innerTable.createPartRowInner("_data")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createStaticElement()
                .and().createDoubleValueElement().keyValueIdx(2)
                .and().createStaticElement();
        }

        {
            final PDInnerTable innerTable = topTable.createPartInnerTable().key("P_5a").createInnerTable();

            innerTable.createPartRowInner("_data")
                .and().createStaticElement("Суммарный показатель")
                .and().createDoubleValueElement().keyValueIdx(1)
                .and().createStaticElement("Единица измерения")
                .and().createStringValueElement().keyValueIdx(2)
                .and().widthsOfElements(30,20,30,20);
        }

        return new EPSectionTemplateFactory(topTable);
    }


    public EnergyPassportSectionTemplateFactory section_2_1() {


        final PDTable pdTable = new PDTable();
        pdTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.1", "ИНФОРМАЦИЯ\n" +
            "ОБ ЭНЕРГОСБЕРЕЖЕНИИ И О ПОВЫШЕНИИ\n" +
            "ЭНЕРГЕТИЧЕСКОЙ ЭФФЕКТИВНОСТИ\n")));

        headerTool(pdTable, "h1-1", "ИНФОРМАЦИЯ\n" +
            "ОБ ЭНЕРГОСБЕРЕЖЕНИИ И О ПОВЫШЕНИИ\n" +
            "ЭНЕРГЕТИЧЕСКОЙ ЭФФЕКТИВНОСТИ\n" +
            "организаций, осуществляющих регулируемые\n" +
            "виды деятельности; организаций, осуществляющих производство\n" +
            "и (или) транспортировку воды, природного газа, тепловой\n" +
            "энергии, электрической энергии, добычу природного газа,\n" +
            "нефти, угля, производство нефтепродуктов, переработку\n" +
            "природного газа, нефти, транспортировку нефти,\n" +
            "нефтепродуктов; организаций, проводящих мероприятия\n" +
            "в области энергосбережения и повышения энергетической\n" +
            "эффективности, финансируемых полностью или частично\n" +
            "за счет субсидий из федерального бюджета, бюджетов\n" +
            "субъектов Российской Федерации, местных бюджетов\n");

        pdTable.createPartLine("1", "")
            .and().createStringValueElement().cellStyle(PDCellStyle._makeHAligmentCenter());

        headerTool(pdTable, "h1-2", "(полное наименование юридического лица)");

        headerTool(pdTable, "h1-3", "\n" +
            "\"{DD}\" {MONTH1} {YYYY} г.\n" +
            "(число, месяц, год составления) \n");

        return new EPSectionTemplateFactory(pdTable);

    }


    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_2() {
        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.2", "Общие сведения " +
            "о лице, в отношении которого указана информация")));

        headerTool(topTable, "h1-", "Общие сведения " +
            "о лице, в отношении которого указана информация");

        topTable.createPartLine("P_NAME","")
            .and().createStringValueElement();

        topTable.createPartLine()
            .and().createStaticElement()
            .and().createStaticElement("(полное наименование юридического лица, в отношении которого указана информация)");

        topTable.createPartLine("1.")
            .and().createStaticElement("Организационно-правовая форма")
            .and().createStringValueElement();

        topTable.createPartLine("2.")
            .and().createStaticElement("Почтовый адрес организации")
            .and().createStringValueElement();

        topTable.createPartLine("3.")
            .and().createStaticElement("Место нахождения")
            .and().createStringValueElement();

        topTable.createPartLine("4.")
            .and().createStaticElement("Полное наименование организации (основного общества - для дочерних " +
            "(зависимых) обществ)")
            .and().createStringValueElement();

        topTable.createPartLine("5.")
            .and().createStaticElement("Доля  государственной (муниципальной) собственности в уставном капитале" +
            "организации, %")
            .and().createDoubleValueElement();

        topTable.createPartLine("6.")
            .and().createStaticElement("Реквизиты организации:");

        topTable.createPartLine("6.1.")
            .and().createStaticElement("ОГРН")
            .and().createStringValueElement();

        topTable.createPartLine("6.2.")
            .and().createStaticElement("ИНН")
            .and().createStringValueElement();

        topTable.createPartLine("6.3.")
            .and().createStaticElement("КПП (для юридических лиц)")
            .and().createStringValueElement();

        topTable.createPartLine("6.4.")
            .and().createStaticElement("Банковские реквизиты:");

        topTable.createPartLine("6.4.1.")
            .and().createStaticElement("Полное наименование банка")
            .and().createStringValueElement();

        topTable.createPartLine("6.4.2.")
            .and().createStaticElement("БИК")
            .and().createIntegerValueElement();

        topTable.createPartLine("6.4.3.")
            .and().createStaticElement("Расчетный счет")
            .and().createIntegerValueElement();

        topTable.createPartLine("6.4.4.")
            .and().createStaticElement("Лицевой счет (при наличии)")
            .and().createIntegerValueElement();

        topTable.createPartLine("7.")
            .and().createStaticElement("Коды по классификаторам:")
            .and().createStringValueElement();

        topTable.createPartLine("7.1.")
            .and().createStaticElement("Основной код по ОКВЭД")
            .and().createStringValueElement();

        topTable.createPartLine("7.2.")
            .and().createStaticElement("Дополнительные коды по ОКВЭД")
            .and().createStringValueElement();

        topTable.createPartLine("7.3.")
            .and().createStaticElement("Код по ОКОГУ")
            .and().createStringValueElement();

        topTable.createPartLine("8.")
            .and().createStaticElement("Ф.И.О., должность руководителя")
            .and().createStringValueElement();

        topTable.createPartLine("9.")
            .and().createStaticElement("Ф.И.О.,  должность, телефон, факс, адрес электронной почты должностного " +
            "лица, ответственного за техническое состояние оборудования")
            .and().createStringValueElement();

        topTable.createPartLine("10.")
            .and().createStaticElement("Ф.И.О., должность, телефон, факс, адрес электронной почты должностного " +
            "лица, ответственного за энергетическое хозяйство")
            .and().createStringValueElement();


        topTable.createPartLine("1h1", "")
            .applyCreator(create2EmptyStaticF)
            .and().createStaticElement("Таблица 1");


        {
            final PDTable pdTable = topTable.createPartInnerTable().key("TBL_1").createInnerTable();

            PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п");
            partHeader.createStaticElement().caption("Наименование").columnKey("res_name")
                .and().createStaticElement("Единица измерения")
                .and().createStaticElement("Предшествующие годы")
                    .createStaticChild("{YYYY-4}").columnKey("YYYY-4")
                    .createStaticSibling("{YYYY-3}").columnKey("YYYY-3")
                    .createStaticSibling("{YYYY-2}").columnKey("YYYY-2")
                    .createStaticSibling("{YYYY-1}").columnKey("YYYY-1")
                .and().createStaticElement("Отчетный год");

            partHeader.widthsOfElements(5, 30, 10, 15, 15, 15, 15, 15);


            final Consumer<PDTablePart> rowCreator = (p) -> {
                    p.and().createDoubleValueElement().keyValueIdx(2)
                    .and().createDoubleValueElement().keyValueIdx(3)
                    .and().createDoubleValueElement().keyValueIdx(4)
                    .and().createDoubleValueElement().keyValueIdx(5)
                    .and().createDoubleValueElement().keyValueIdx(6);
            };

            final String mainS = "на производство основной продукции (работ, услуг)";
            final String secondS = "на производство дополнительной продукции (работ, услуг)";


            TriConsumer<String, String, String> sec = (nr, s, k) -> {
                pdTable.createPartRowNr(nr)
                    .and().createStaticElement(s)
                    .and().createStaticElement(k)
                    .and().applyCreator(rowCreator);

                pdTable.createPartRowNr(nr +".1")
                    .and().createStaticElement(mainS)
                    .and().createStaticElement(k)
                    .and().applyCreator(rowCreator);

                pdTable.createPartRowNr(nr +".2")
                    .and().createStaticElement(secondS)
                    .and().createStaticElement(k)
                    .and().applyCreator(rowCreator);

            };

            pdTable.createPartRowNr("1")
                .and().createStaticElement("Номенклатура основной продукции (работ, услуг)")
                .and().createStaticElement()
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("2")
                .and().createStaticElement("Код основной продукции (работ, услуг) по ОКДП")
                .and().createStaticElement()
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("3")
                .and().createStaticElement("Номенклатура дополнительной продукции (работ, услуг)")
                .and().createStaticElement()
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("4")
                .and().createStaticElement("Код дополнительной продукции (работ, услуг) по ОКДП")
                .and().createStaticElement()
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("5")
                .and().createStaticElement("Объем производства продукции (работ, услуг) в стоимостном выражении, всего,в том числе:")
                .and().createStaticElement(EPConstants.KRUB2)
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("5.1")
                .and().createStaticElement("основной продукции (работ, услуг)")
                .and().createStaticElement(EPConstants.KRUB2)
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("5.2")
                .and().createStaticElement("дополнительной продукции (работ, услуг)")
                .and().createStaticElement(EPConstants.KRUB2)
                .and().applyCreator(rowCreator);


            sec.accept("6", "Объем потребленной электрической энергии в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("7", "Объем потребленной электрической энергии в натуральном выражении, всего, в том числе:", EPConstants.KKWH2);
            sec.accept("8", "Объем потребленной тепловой энергии в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("9", "Объем потребленной тепловой энергии в натуральном выражении, всего, в том числе:", EPConstants.GCAL2);
            sec.accept("10", "Объем потребленного твердого топлива в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("11", "Объем потребленного твердого топлива в натуральном выражении, всего, в том числе:", EPConstants.T2);
            sec.accept("12", "Объем потребленного жидкого топлива в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("13", "Объем потребленного жидкого топлива в натуральном выражении, всего, в том числе:", EPConstants.T2);
            sec.accept("14", "Объем потребленного природного газа в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("15", "Объем потребленного природного газа в натуральном выражении, всего, в том числе:", EPConstants.KNVM2);
            sec.accept("16", "Объем потребленного сжиженного газа в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("17", "Объем потребленного сжиженного газа в натуральном выражении, всего,в том числе:", EPConstants.KT2);
            sec.accept("18", "Объем потребленного сжатого газа в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("19", "Объем потребленного сжатого газа в натуральном выражении, всего в том числе:", EPConstants.KNVM2);
            sec.accept("20", "Объем потребленного попутного нефтяного газа в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("21", "Объем потребленного попутного нефтяного газа в натуральном выражении, всего, в том числе:", EPConstants.KNVM2);
            sec.accept("22", "Объем потребленного бензина в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("23", "Объем потребленного бензина в натуральном выражении, всего, в том числе:", "тыс. л");
            sec.accept("24", "Объем потребленного керосина в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("25", "Объем потребленного керосина в натуральном выражении, всего, в том числе:", "тыс. л");
            sec.accept("26", "Объем потребленного дизельного топлива в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);
            sec.accept("27", "Объем потребленного дизельного топлива в натуральном выражении, всего, в том числе:", "тыс. л");
            sec.accept("28", "Объем иных потребленных энергетических ресурсов в стоимостном выражении, всего, в том числе:", EPConstants.KRUB2);

            pdTable.createPartRowNr("29")
                .and().createStaticElement("Объем иных потребленных энергетических ресурсов в натуральном выражении, всего, в том числе:")
                .and().createStringValueElement().keyValueIdx(1)
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("29" +".1")
                .and().createStaticElement(mainS)
                .and().createStringValueElement().keyValueIdx(1)
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("29" +".2")
                .and().createStaticElement(secondS)
                .and().createStringValueElement().keyValueIdx(1)
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("30")
                .and().createStaticElement("Суммарная максимальная мощность энергопринимающих устройств")
                .and().createStaticElement(EPConstants.KKWH2)
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("31")
                .and().createStaticElement("Суммарная среднегодовая заявленная мощность энергопринимающих устройств")
                .and().createStaticElement(EPConstants.KKWH2)
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("32")
                .and().createStaticElement("Среднесписочная численность работников, всего, в том числе:")
                .and().createStaticElement("чел.")
                .and().applyCreator(rowCreator);

            pdTable.createPartRowNr("32.1")
                .and().createStaticElement("производственного персонала")
                .and().createStaticElement("чел.")
                .and().applyCreator(rowCreator);

        }

        topTable.createPartLine("2h1", "")
            .and().createStaticElement("Сведения об обособленных подразделениях лица,в отношении которого указана информация");

        topTable.createPartLine("2h2", "")
            .applyCreator(create2EmptyStaticF)
            .and().createStaticElement("Таблица 2");

        {
            final PDTable pdTable = topTable.createPartInnerTable().key("TBL_1").createInnerTable();

            PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п").keyValueIdx(1);
            partHeader.createStaticElement().caption("Наименование подразделения").keyValueIdx(2)
                .and().createStaticElement("Адрес местонахождения").keyValueIdx(3)
                .and().createStaticElement("КПП (в случае отсутствия - территориальный код ФНС)").keyValueIdx(4)
                .and().createStaticElement("Среднесписочная численность")
                .createStaticChild("работников (всего), чел.").keyValueIdx(5)
                .createStaticSibling("производственного персонала, чел.").keyValueIdx(6)
                .and().widthsOfElements(5, 20, 20, 15, 15, 15);

            pdTable.createPart(PDPartType.ROW).key("DATA").dynamic()
                .createValueElement(PDTableCellValueCounter.class).columnKey("nr").keyValueIdx(1)// #
                .and().createStringValueElement().keyValueIdx(2)
                .and().createStringValueElement().keyValueIdx(3)
                .and().createStringValueElement().keyValueIdx(4)
                .and().createIntegerValueElement().keyValueIdx(5)
                .and().createIntegerValueElement().keyValueIdx(6);

        }

            return new EPSectionTemplateFactory(topTable);
    }

        /**
         * page 26 of Order
         * @return
         */
    public EnergyPassportSectionTemplateFactory section_2_3() {

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.3", "Сведения об оснащенности приборами учета")));

        headerTool(topTable, "h1-", "Сведения об оснащенности приборами учета");


        final PDTable pdTable = topTable.createPartInnerTable().key("S_2.3").createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п").width(10);
        partHeader.createStaticElement().caption("Наименование показателя").width(40);
        PDTableCellStatic amount = partHeader.createStaticElement().caption("Количество, шт");

        int keyValueIdx = 1;
        amount.createStaticChild().caption("Электрической энергии")
            .createStaticChild().caption("Всего").width(10).keyValueIdx(keyValueIdx++)
            .createStaticSibling().caption("В том числе в составе АИИС").width(10).keyValueIdx(keyValueIdx++);

        amount.createStaticChild().caption("Тепловой энергии")
            .createStaticChild().caption("Всего").width(10).keyValueIdx(keyValueIdx++)
            .createStaticSibling().caption("В том числе в составе АИИС").width(10).keyValueIdx(keyValueIdx++);

        amount.createStaticChild().caption("Газа")
            .createStaticChild().caption("Всего").width(10).keyValueIdx(keyValueIdx++)
            .createStaticSibling().caption("В том числе в составе АИИС").width(10).keyValueIdx(keyValueIdx++);


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

        pdTable.createPartRowNr("1.1.1")
            .and().createStaticElement("полученной от стороннего источника")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRowNr("1.1.2")
            .and().createStaticElement("собственного производства")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRowNr("1.1.3")
            .and().createStaticElement("потребленной на собственные нужды")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRowNr("1.1.4")
            .and().createStaticElement("отданной субабонентам (сторонним потребителям)")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        /// 1.2
        pdTable.createPartRowNr("1.2")
            .and().createStaticElement("Количество необорудованных узлами (приборами) учета точек приема (поставки), всего,\n" +
            "в том числе:\n")
            .and().createValueElements(6, PDTableCellValueDoubleAggregation.class).forEach(i -> {
            i.setValueFunction("sum()");
            i.setValueGroup("P_2.1.*");
        });

        pdTable.createPartRowNr("1.2.1")
            .and().createStaticElement("полученной от стороннего источника")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRowNr("1.2.2")
            .and().createStaticElement("собственного производства")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRowNr("1.2.3")
            .and().createStaticElement("потребленной на собственные нужды")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRowNr("1.2.4")
            .and().createStaticElement("отданной субабонентам (сторонним потребителям)")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        // 1.3
        pdTable.createPartRowNr("1.3")
            .and().createStaticElement("Количество узлов (приборов) учета с нарушенными сроками поверки")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        // 1.4
        pdTable.createPartRowNr("1.4")
            .and().createStaticElement("Количество узлов (приборов) учета с нарушением требований к классу точности " +
            "(относительной погрешности) узла (прибора) учета")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        // 2
        pdTable.createPartRowNr("2")
            .and().createStaticElement("Сведения об оснащенности узлами (приборами) технического учета").mergedCells(7);

        // 2.1
        pdTable.createPartRowNr("2.1")
            .and().createStaticElement("Суммарное количество узлов (приборов) учета")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        return new EPSectionTemplateFactory(topTable);
    }

    // TODO Make rexexp for functions
    public EnergyPassportSectionTemplateFactory section_2_4() {


        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.4", "Сведения по балансу электрической энергии и его изменениях")));

        headerTool(topTable, "h1-", "Сведения по балансу электрической энергии и его изменениях");


        final PDInnerTable innerTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п");
        partHeader.createStaticElement().caption("Статья").columnKey("accounting");
        partHeader
            .createStaticElement().caption("Предшествующие годы")
                .createStaticChild("{YYYY-4}").columnKey("YYYY-4")
                .createStaticSibling("{YYYY-3}").columnKey("YYYY-3")
                .createStaticSibling("{YYYY-2}").columnKey("YYYY-2")
                .createStaticSibling("{YYYY-1}").columnKey("YYYY-1")
            .and().createStaticElement("Отчетный год").columnKey("YYYY");

        partHeader.widthsOfElements(10,50,15,15,15,15,15);

        innerTable.createPartRowNr("1")
            .and().createStaticElement("Приход").mergedCells(6);

        innerTable.createPartRowNr("1.1")
            .and().createStaticElement("Сторонний источник").columnKey("income_side")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("1.2")
            .and().createStaticElement("Собственное производство").columnKey("income_own")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRow("1_total", "")
            .createStaticElement("Итого суммарный приход").cellStyle(PDCellStyle._makeHAligmentRight())
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("2")
            .and().createStaticElement("Расход").mergedCells(6);

        innerTable.createPartRowNr("2.1")
            .and().createStaticElement("На собственные нужды, всего, в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("2.1.1")
            .and().createStaticElement("производственный (технологический) расход")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.2")
            .and().createStaticElement("хозяйственные нужды")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.3")
            .and().createStaticElement("электрическое отопление")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.4")
            .and().createStaticElement("электрический транспорт")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.5")
            .and().createStaticElement("прочие собственные нужды")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.2")
            .and().createStaticElement("Субабоненты (сторонние потребители)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.3")
            .and().createStaticElement("Фактические (отчетные) потери, всего, в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.3.1|2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("2.3.1")
            .and().createStaticElement("технологические потери, всего, в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.3.1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("2.3.1.1", "")
            .and().createStaticElement("условно-постоянные")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.3.1.2", "")
            .and().createStaticElement("нагрузочные")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.3.1.3", "")
            .and().createStaticElement("потери, обусловленные допустимыми погрешностями приборов учета")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.3.2")
            .and().createStaticElement("нерациональные потери")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2_total", "")
            .createStaticElement("Итого суммарный расход").cellStyle(PDCellStyle._makeHAligmentRight())
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("3")
            .and().createStaticElement("Значения утвержденных нормативов потерь")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_5() {

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.5", "Сведения по балансу тепловой энергии и его изменениях")));

        headerTool(topTable, "h1-", "Сведения по балансу тепловой энергии и его изменениях");


        final PDInnerTable innerTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п");
        partHeader.createStaticElement().caption("Статья").columnKey("accounting");
        partHeader
            .createStaticElement().caption("Предшествующие годы")
            .createStaticChild("{YYYY-4}").columnKey("YYYY-4")
            .createStaticSibling("{YYYY-3}").columnKey("YYYY-3")
            .createStaticSibling("{YYYY-2}").columnKey("YYYY-2")
            .createStaticSibling("{YYYY-1}").columnKey("YYYY-1")
            .and().createStaticElement("Отчетный год").columnKey("YYYY");

        partHeader.widthsOfElements(10,50,15,15,15,15,15);

        innerTable.createPartRowNr("1")
            .and().createStaticElement("Приход").mergedCells(6);

        innerTable.createPartRowNr("1.1")
            .and().createStaticElement("Сторонний источник").columnKey("income_side")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("1.2")
            .and().createStaticElement("Собственное производство,\n" +
            "в том числе:").columnKey("income_own")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_1.2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("1.2.1")
            .and().createStaticElement("электрическое отопление").columnKey("income_own")
            .and().createValueElements(5, PDTableCellValueDouble.class);



        innerTable.createPartRow("1_total", "")
            .createStaticElement("Итого суммарный приход").cellStyle(PDCellStyle._makeHAligmentRight())
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("2")
            .and().createStaticElement("Расход").mergedCells(6);

        innerTable.createPartRowNr("2.1")
            .and().createStaticElement("На собственные нужды, всего, в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("2.1.1")
            .and().createStaticElement("пара, из них контактным (острым) способом")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.2")
            .and().createStaticElement("горячей воды")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRowNr("2.2")
            .and().createStaticElement("Отопление и вентиляция, всего,\n" +
            "в том числе:\n")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.2.*");
                i.setValueFunction("sum()");
            });


        innerTable.createPartRowNr("2.2.1")
            .and().createStaticElement("калориферы воздушные")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRowNr("2.3")
            .and().createStaticElement("Горячее водоснабжение")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRowNr("2.4")
            .and().createStaticElement("Субабоненты (сторонние потребители)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.5")
            .and().createStaticElement("Субабоненты (сторонние потребители)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2_total", "")
            .createStaticElement("Итого суммарный расход").cellStyle(PDCellStyle._makeHAligmentRight())
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("3")
            .and().createStaticElement("Значения утвержденных нормативов потерь")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    private EnergyPassportSectionTemplateFactory section_2_6_bak() {

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.6", "Сведения по балансу потребления котельно-печного топлива")));

        headerTool(topTable, "h1-", "Сведения по балансу потребления котельно-печного топлива");


        final PDInnerTable innerTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п");
        partHeader.createStaticElement().caption("Статья").columnKey("accounting");
        partHeader
            .createStaticElement().caption("Предшествующие годы")
            .createStaticChild("{YYYY-4}").columnKey("YYYY-4")
            .createStaticSibling("{YYYY-3}").columnKey("YYYY-3")
            .createStaticSibling("{YYYY-2}").columnKey("YYYY-2")
            .createStaticSibling("{YYYY-1}").columnKey("YYYY-1")
            .and().createStaticElement("Отчетный год").columnKey("YYYY");

        partHeader.widthsOfElements(10,50,15,15,15,15,15);

        innerTable.createPartRowNr("1")
            .and().createStaticElement("Приход").mergedCells(6);


        innerTable.createPart(PDPartType.ROW).key("P_1").dynamic()
            .createValueElement(PDTableCellValueCounter.class).columnKey("nr").keyValueIdx(1).counterPrefix("1.")
            .and().createStringValueElement().columnKey("accounting").keyValueIdx(2)
            .and().createValueElements(5, PDTableCellValueDouble.class, 3);


        innerTable.createPartRow("1_total", "")
            .createStaticElement("Итого суммарный приход")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_1.*");
                i.setValueFunction("sum()");
            });


        innerTable.createPartRowNr("2")
            .and().createStaticElement("Расход").mergedCells(6);

        innerTable.createPartRowNr("2.1")
            .and().createStaticElement("Технологическое использование, всего,\n" +
            "в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("2.1.1")
            .and().createStaticElement("нетопливное использование (в виде сырья)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.2")
            .and().createStaticElement("нагрев")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.3")
            .and().createStaticElement("сушка")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.4")
            .and().createStaticElement("обжиг (плавление, отжиг)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.1.5")
            .and().createStaticElement("бытовое использование")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.2")
            .and().createStaticElement("На выработку тепловой энергии, всего,\n" +
            "в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRowNr("2.2.1")
            .and().createStaticElement("в котельной")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRowNr("2.2.2")
            .and().createStaticElement("в собственной тепловой электрической станции (включая выработку электрической энергии)")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRow("2_total", "")
            .createStaticElement("Итого суммарный расход")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.*");
                i.setValueFunction("sum()");
            });

        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_6() {

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.6", "Сведения по балансу потребления котельно-печного топлива")));

        headerTool(topTable, "h1-", "Сведения по балансу потребления котельно-печного топлива");


        {
            final PDInnerTable innerTable = topTable.createPartInnerTable().createInnerTable();

            PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п");
            partHeader.createStaticElement().caption("Статья").columnKey("accounting");
            partHeader
                .createStaticElement().caption("Предшествующие годы")
                .createStaticChild("{YYYY-4}").columnKey("YYYY-4")
                .createStaticSibling("{YYYY-3}").columnKey("YYYY-3")
                .createStaticSibling("{YYYY-2}").columnKey("YYYY-2")
                .createStaticSibling("{YYYY-1}").columnKey("YYYY-1")
                .and().createStaticElement("Отчетный год").columnKey("YYYY");

            partHeader.widthsOfElements(10, 50, 15, 15, 15, 15, 15);


            innerTable.createPartRowNr("1")
                .and().createStaticElement("Приход").mergedCells(6);


            PDTablePart partDynamic =
                innerTable.createPart(PDPartType.ROW).key("P_1").dynamic();
            partDynamic.createValueElement(PDTableCellValueCounter.class).columnKey("nr").keyValueIdx(1).counterPrefix("1.")
                .and().createStringValueElement().columnKey("accounting").keyValueIdx(2)
                .and().createValueElements(5, PDTableCellValueDouble.class, 3);

            PDTablePart part = innerTable.createPartRow("1_total", "");
            part.createStaticElement("Итого суммарный приход").cellStyle(PDCellStyle._makeHAligmentRight())
                .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_1.*");
                    i.setValueFunction("sum()");
                });
            part.widthsOfElements(10, 50, 15, 15, 15, 15, 15);

            innerTable.createPartRowNr("2")
                .and().createStaticElement("Расход").mergedCells(6);

            innerTable.createPartRowNr("2.1")
                .and().createStaticElement("Технологическое использование, всего,\n" +
                "в том числе:")
                .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_2.1.*");
                    i.setValueFunction("sum()");
                });

            innerTable.createPartRowNr("2.1.1")
                .and().createStaticElement("нетопливное использование (в виде сырья)")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable.createPartRowNr("2.1.2")
                .and().createStaticElement("нагрев")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable.createPartRowNr("2.1.3")
                .and().createStaticElement("сушка")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable.createPartRowNr("2.1.4")
                .and().createStaticElement("обжиг (плавление, отжиг)")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable.createPartRowNr("2.1.5")
                .and().createStaticElement("бытовое использование")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable.createPartRowNr("2.2")
                .and().createStaticElement("На выработку тепловой энергии, всего,\n" +
                "в том числе:")
                .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_2.2.*");
                    i.setValueFunction("sum()");
                });

            innerTable.createPartRowNr("2.2.1")
                .and().createStaticElement("в котельной")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable.createPartRowNr("2.2.2")
                .and().createStaticElement("в собственной тепловой электрической станции (включая выработку электрической энергии)")
                .and().createValueElements(5, PDTableCellValueDouble.class);


            innerTable.createPartRow("2_total", "")
                .createStaticElement("Итого суммарный расход").cellStyle(PDCellStyle._makeHAligmentRight())
                .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_2.*");
                    i.setValueFunction("sum()");
                });

        }

        {
            final PDInnerTable innerTable2 = topTable.createPartInnerTable().createInnerTable();



        }
        return new EPSectionTemplateFactory(topTable);
    }


    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_7() {

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.7", "Сведения об использовании моторного топлива")));

        headerTool(topTable, "h1-", "Сведения об использовании моторного топлива");

        {
            final PDTable pdTable = topTable.createPartInnerTable().createInnerTable();

            PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п").nextKeyValueIdx()
                .and().createStaticElement().caption("Наименование (марка) транспортного средства, оборудования").nextKeyValueIdx()
                .and().createStaticElement("Количество единиц транспортных средств, оборудования").nextKeyValueIdx()
                .and().createStaticElement("Грузоподъемность, т, пассажиров вместимость, чел.").nextKeyValueIdx()
                .and().createStaticElement("Объем грузоперевозок, тыс. т-км, тыс. пасс.-км.")
                .and().createStaticElement("Сведения об использовании моторного топлива за отчетный год ")
                    //.createStaticChild(" ")
                    .createStaticChild("N п/п").cellStyle(new PDCellStyle().rowSpan(2)).nextKeyValueIdx().andParentCell()
                    .createStaticChild("вид использованного топлива, в том числе электрической энергии").cellStyle(new PDCellStyle().rowSpan(2)).nextKeyValueIdx().andParentCell()
                    .createStaticChild("способ измерения расхода топлива (электрической энергии)").cellStyle(new PDCellStyle().rowSpan(2)).nextKeyValueIdx().andParentCell()
                    .createStaticChild("удельный расход топлива и электрической энергии, л/100 км, л/моточас, т/100 км, т/моточас, н. куб. м/100 км, н. куб. м/моточас, кВт·ч/100 км, кВт·ч/моточас")
                        .createStaticChild("нормативный").nextKeyValueIdx().andParentCell()
                        .createStaticChild("фактический").nextKeyValueIdx().andParentCell()
                        .andParentCell()
                    .createStaticChild("пробег, тыс. км, отработано, моточас").cellStyle(new PDCellStyle().rowSpan(2)).nextKeyValueIdx().andParentCell()
                    .createStaticChild("количество топлива и электрической энергии, тыс. л, т, н. куб. м, тыс. кВт·ч")
                        .createStaticChild("полученного").nextKeyValueIdx().andParentCell()
                        .createStaticChild("израсходованного").nextKeyValueIdx()
                .and().widthsOfElements(5,10,10,10,10,5,10,10,10,10,10,10,15);



            final int subsectionCnt = 4;


            Consumer<PDTablePart> subsection = (p) -> {
                PDTableCell<?> cell = p.createStaticElement().vertical().nextKeyValueIdx();
                for (int i = 0; i < subsectionCnt; i++) {
                    cell.createStaticChild((i + 1) + "").keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
                }
            };


            Consumer<PDTablePart> subsectionStr = (p) -> {
                PDTableCell<?> cell = p.createValuePackElement().vertical().nextKeyValueIdx();
                for (int i = 0; i < subsectionCnt; i++) {
                    cell.createChildValue(PDTableCellValueString.class).keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
                }
            };

            Consumer<PDTablePart> subsectionDbl = (p) -> {
                PDTableCell<?> cell = p.createValuePackElement().vertical().nextKeyValueIdx();
                for (int i = 0; i < subsectionCnt; i++) {
                    cell.createChildValue(PDTableCellValueDouble.class).keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
                }
            };


            pdTable.createPart(PDPartType.ROW).key("DATA").dynamic()
                .createValueElement(PDTableCellValueCounter.class).nextKeyValueIdx()// #
                .and().createStringValueElement().nextKeyValueIdx()
                .and().createStringValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx()
                .and().applyCreator(subsection)
                .and().applyCreator(subsectionStr)
                .and().applyCreator(subsectionStr)
                .and().applyCreator(subsectionDbl)
                .and().applyCreator(subsectionDbl)
                .and().applyCreator(subsectionDbl)
                .and().applyCreator(subsectionDbl)
                .and().applyCreator(subsectionDbl);


//            pdTable.createPartRow("1.1", "1")
//                .and().createStringValueElement().keyValueIdx(1)
//                .and().createDoubleValueElement().keyValueIdx(2)
//                .and().createDoubleValueElement().keyValueIdx(3)
//                .and().createDoubleValueElement().keyValueIdx(4)
//                .and().createDoubleValueElement().keyValueIdx(5)
//                .and().createDoubleValueElement().keyValueIdx(6)
//                .and().createDoubleValueElement().keyValueIdx(7)
//                .and().createDoubleValueElement().keyValueIdx(8)
//                .and().createStringValueElement().keyValueIdx(9);
        }

        return new EPSectionTemplateFactory(topTable);
    };


//    @Builder
//    private class PDKeyHeader {
//        private final String key;
//        private final String header;
//    }

    private BiConsumer<PDTable, PDKeyHeader> tableHeaderCreator = (t, h) -> {
        t.viewType(PDViewType.FORM).sectionKey("S_" + h.getKey())
            .caption(h.getHeader())
            .shortCaption(h.getKey())
            .sectionNr(h.getKey()
            );
    };

    private final static BiConsumer<PDTable, PDKeyHeader> tablePartLineCreator = (t, h) ->
        t.createPartLine(h.getKey(), "")
            .and().createStaticElement(h.getHeader());

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_8() {

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.8", "Сведения об использовании вторичных энергетических ресурсов")));

        headerTool(topTable, "h1-", "Сведения об использовании вторичных энергетических ресурсов");

        topTable.createPartLine("1h1", "")
            .and().applyCreator(create2EmptyStaticF)
            .and().createStaticElement("Таблица 1");

        {
            final PDTable pdTable = topTable.createPartInnerTable().createInnerTable();

            PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п");
            partHeader.createStaticElement().caption("Наименование и источник вторичного (теплового) энергетического ресурса (далее - ВЭР)").columnKey("res_name");
            partHeader
                .createStaticElement("Характеристики ВЭР")
                .createStaticChild("фазовое состояние").columnKey("phase")
                .createStaticSibling("расход куб. м/ч").columnKey("consumption")
                .createStaticSibling("давление, МПа").columnKey("pressure")
                .createStaticSibling("температура, °C").columnKey("temperature")
                .createStaticSibling("характерные загрязнители, их концентрация, %").columnKey("pollutants")
                .and().createStaticElement("Годовой выход ВЭР, Гкал").columnKey("year_output")
                .and().createStaticElement("Годовое фактическое использование, Гкал").columnKey("year_use")
                .and().createStaticElement("Примечание").columnKey("annotation");

            partHeader.widthsOfElements(10, 30, 15, 15, 15, 15, 20, 15, 15, 20);


            Consumer<PDTablePart> valuesCreator = (p) ->
                p.createStringValueElement().keyValueIdx(1)
                    .and().createDoubleValueElement().keyValueIdx(2)
                    .and().createDoubleValueElement().keyValueIdx(3)
                    .and().createDoubleValueElement().keyValueIdx(4)
                    .and().createDoubleValueElement().keyValueIdx(5)
                    .and().createDoubleValueElement().keyValueIdx(6)
                    .and().createDoubleValueElement().keyValueIdx(7)
                    .and().createDoubleValueElement().keyValueIdx(8)
                    .and().createStringValueElement().keyValueIdx(9);


            pdTable.createPartRow("1.1", "1").applyCreator(valuesCreator);

            pdTable.createPartRow("1.2","2").applyCreator(valuesCreator);

            PDTablePart table1TotalPart = pdTable.createPartRow("1_total", "");
            table1TotalPart.createStaticElement("Итого").cellStyle(PDCellStyle._makeHAligmentRight())
                .and().createStaticElement("").mergedCells(5)
                .and().createValueElements(2, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_1.*");
                    i.setValueFunction("sum()");
                });
            table1TotalPart.createStaticElement();

        }


        tableCreateLineHeader.accept(topTable, "2h", "Сведения\n" +
            "об использовании альтернативных (местных) топлив\n" +
            "и возобновляемых источников энергии");


        topTable.createPartLine("2h1", "").applyCreator(create2EmptyStaticF).createStaticElement("Таблица 2");

        {
            final PDTable pdTable = topTable.createPartInnerTable().createInnerTable();

            PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п");
            partHeader.createStaticElement().caption("Наименование альтернативного (местного) или возобновляемого вида ТЭР").columnKey("res_name")
                .and().createStaticElement("Основные характеристики")
                .and().createStaticElement("Теплотворная способность, ккал/кг")
                .and().createStaticElement("Годовая наработка энергоустановки, ч")
                .and().createStaticElement("КПД энергоустановки, %")
                .and().createStaticElement("Годовой фактический выход энергии за отчетный год")
                    .createStaticChild("по тепловой энергии, Гкал")
                    .createStaticSibling("по электрической энергии, МВт·ч")
                .and().createStaticElement("Примечание").columnKey("annotation");

            partHeader.widthsOfElements(10, 30, 15, 15, 15, 15, 15, 15, 20);

            Consumer<PDTablePart> valuesCreator = (p) ->
                p.createStringValueElement().keyValueIdx(1)
                    .and().createDoubleValueElement().keyValueIdx(2)
                    .and().createDoubleValueElement().keyValueIdx(3)
                    .and().createDoubleValueElement().keyValueIdx(4)
                    .and().createDoubleValueElement().keyValueIdx(5)
                    .and().createDoubleValueElement().keyValueIdx(6)
                    .and().createDoubleValueElement().keyValueIdx(7)
                    .and().createStringValueElement().keyValueIdx(8);

            pdTable.createPartRow("2.1", "1").applyCreator(valuesCreator);

            pdTable.createPartRow("2.2","2").applyCreator(valuesCreator);

            PDTablePart table2TotalPart = pdTable.createPartRow("2_total", "");
            table2TotalPart.createStaticElement("Итого").cellStyle(PDCellStyle._makeHAligmentRight())
                .and().createStaticElement("").mergedCells(4)
                .and().createValueElements(2, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_2.*");
                    i.setValueFunction("sum()");
                });
            table2TotalPart.createStaticElement();

        }

        return new EPSectionTemplateFactory(topTable);
    }


    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_9() {

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.9", "Сведения " +
            "о системах освещения и показатели энергетической " +
            "эффективности использования электрической энергии на цели " +
            "наружного освещения площадок предприятий, населенных " +
            "пунктов и автомобильных дорог вне населенных пунктов ")));

        headerTool(topTable, "h1-", "Сведения\n" +
            "о системах освещения и показатели энергетической\n" +
            "эффективности использования электрической энергии на цели\n" +
            "наружного освещения площадок предприятий, населенных\n" +
            "пунктов и автомобильных дорог вне населенных пунктов");


        {
            final PDTable pdTable = topTable.createPartInnerTable().createInnerTable();

            PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п").keyValueIdx(1)
                .and().createStaticElement().caption("Наименование системы освещения").keyValueIdx(2)
                .and().createStaticElement().caption("Тип освещаемой поверхности").keyValueIdx(3)
                .and().createStaticElement().caption("Нормированная средняя горизонтальная освещенность покрытий").keyValueIdx(4)
                .and().createStaticElement().caption("Соответствие фактической средней горизонтальной освещенности нормативной (да/нет)").keyValueIdx(5)
                .and().createStaticElement().caption("Наличие системы управления освещением (да/нет)").keyValueIdx(6)
                .and().createStaticElement().caption("Количество и установленная мощность светильников")
                    .createStaticChild("со световой отдачей менее 35 лм/Вт")
                        .createStaticChild("шт.").keyValueIdx(7).andParentCell()
                        .createStaticChild("кВт").keyValueIdx(8).andParentCell()
                    .andParentCell()
                    .createStaticChild("со световой отдачей от 35 до 100 лм/Вт")
                        .createStaticChild("шт.").keyValueIdx(9).andParentCell()
                        .createStaticChild("кВт").keyValueIdx(10).andParentCell()
                    .andParentCell()
                    .createStaticChild("со световой отдачей более 100 лм/Вт")
                        .createStaticChild("шт.").keyValueIdx(11).andParentCell()
                        .createStaticChild("кВт").keyValueIdx(12).andParentCell()
                .and().createStaticElement().caption("Суммарная установленная мощность, кВт").keyValueIdx(13)
                .and().createStaticElement().caption("Время работы системы за год, часов").keyValueIdx(14)
                .and().createStaticElement().caption("Освещаемая площадь, тыс. кв. м").keyValueIdx(15)
                .and().createStaticElement().caption("Удельная мощность осветительных установок, Вт/кв. м").keyValueIdx(16)
                .and().createStaticElement().caption("Суммарный объем потребления электрической энергии за отчетный год, тыс. кВт·ч").keyValueIdx(17);

            partHeader.widthsOfElements(5, 15, 15, 15, 15, 15, 10, 10, 10, 10, 10, 10, 15, 15, 15, 15, 15);


            Consumer<PDTablePart> pcsKW = (p) -> p.createIntegerValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx();

            pdTable.createPartRow().key("DATA").dynamic()
                .and().createValueElement(PDTableCellValueCounter.class).nextKeyValueIdx()
                .and().createStringValueElement().nextKeyValueIdx()
                .and().createStringValueElement().nextKeyValueIdx()
                .and().createStringValueElement().nextKeyValueIdx()
                .and().createBooleanValueElement().nextKeyValueIdx()
                .and().createBooleanValueElement().nextKeyValueIdx()
                .and().applyCreator(pcsKW)
                .and().applyCreator(pcsKW)
                .and().applyCreator(pcsKW)
                .and().createDoubleValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx();


            final Consumer<PDTablePart> sum6 = (p) ->
                p.createValueElements(6,PDTableCellValueDoubleAggregation.class).forEach((i) -> {
                    i.nextKeyValueIdx();
                    i.setValueFunction("sum()");
                    i.setValueGroup("DATA_dr(.)_i1");
                });

            final Consumer<PDTablePart> sum5 = (p) ->
                p.createValueElements(5,PDTableCellValueDoubleAggregation.class).forEach((i) -> {
                    i.nextKeyValueIdx();
                    i.setValueFunction("sum()");
                    i.setValueGroup("DATA_dr(.)_i1");
                });


            pdTable.createPartRow().key("DATA_TOTAL")
                .and().createStaticElement()
                .and().createStaticElement("Итого").mergedCells(5).cellStyle(PDCellStyle._makeHAligmentRight())
                .and().applyCreator(sum6)
                .and().applyCreator(sum5);

        }


        return new EPSectionTemplateFactory(topTable);
    }

        /**
         *
         * @return
         */
    public EnergyPassportSectionTemplateFactory section_2_10(){

        final PDTable topTable = new PDTable();
        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.10", "Краткая характеристика зданий (строений, сооружений)")));

        headerTool(topTable, "h1-", "Краткая характеристика зданий (строений, сооружений)");


        final PDTable pdTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);
        partHeader
            .createStaticElement("№ п/п").keyValueIdx(1)
            .and().createStaticElement("Наименование здания, строения, сооружения").columnKey("nr").keyValueIdx(2)
            .and().createStaticElement("Год ввода в эксплуатацию").columnKey("year").keyValueIdx(3)
            .and().createStaticElement("Ограждающие конструкции").columnKey("walling")
            .createStaticChild().caption("наименование конструкции").columnKey("walling_construction").keyValueIdx(4)
            .createStaticSibling().caption("краткая характеристика").columnKey("walling_description").keyValueIdx(5)
            .and().createStaticElement("Общая площадь, здания, строения, сооружения, кв. м").columnKey("total_area").keyValueIdx(6)
            .and().createStaticElement("Отапливаемая площадь, здания, строения, сооружения, кв. м").columnKey("heat_area").keyValueIdx(7)
            .and().createStaticElement("Отапливаемый объем здания, строения, сооружения, куб. м").columnKey("heat_volume").keyValueIdx(8)
            .and().createStaticElement("Износ здания, строения, сооружения, %").columnKey("wear").keyValueIdx(9);

        pdTable.createPart(PDPartType.ROW).key("DATA").dynamic()
            .createValueElement(PDTableCellValueCounter.class).columnKey("nr").keyValueIdx(1)// #
            .and().createStringValueElement().columnKey("name").keyValueIdx(2) // name
            .and().createStringValueElement().columnKey("year").keyValueIdx(3) // year
            .and().createStaticElement().vertical().columnKey("walling_construction").keyValueIdx(4)
            .createStaticChild("Стены").columnKey("walls").keyValueIdx(4).valuePackIdx(1)
            .createStaticSibling("Окна").columnKey("windows").keyValueIdx(4).valuePackIdx(2)
            .createStaticSibling("Крыша").columnKey("roof").keyValueIdx(4).valuePackIdx(3)
            .and().createValuePackElement().vertical().columnKey("walling_description").keyValueIdx(5)
            .createChildValue(PDTableCellValueString.class).columnKey("walls").keyValueIdx(5).valuePackIdx(1)
            .createSiblingValue(PDTableCellValueString.class).columnKey("windows").keyValueIdx(5).valuePackIdx(2)
            .createSiblingValue(PDTableCellValueString.class).columnKey("roof").keyValueIdx(5).valuePackIdx(3)
            .and().createDoubleValueElement().columnKey("total_area").keyValueIdx(6)
            .and().createDoubleValueElement().columnKey("heat_area").keyValueIdx(7)
            .and().createDoubleValueElement().columnKey("heat_volume").keyValueIdx(8)
            .and().createDoubleValueElement().columnKey("wear").keyValueIdx(9);

        return new EPSectionTemplateFactory(topTable);
    }


    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_11() {

        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.11", "Сведения о программе энергосбережения, " +
            "повышения энергетической эффективности и выполненных " +
            "энергоресурсосберегающих мероприятиях")));

        headerTool(topTable, "h1-", "Сведения о программе энергосбережения,\n" +
            "повышения энергетической эффективности и выполненных \n" +
            "энергоресурсосберегающих мероприятиях ");


        topTable.createPartLine("1.")
            .and().createStaticElement("Наличие   программы   энергосбережения   и   повышения  энергетической эффективности:")
            .and().createBooleanValueElement();

        topTable.createPartLine("2.")
            .and().createStaticElement("Наименование  программы  энергосбережения  и  повышения  энергетической эффективности:")
            .and().createStringValueElement();

        topTable.createPartLine("3.")
            .and().createStaticElement("Дата утверждения:")
            .and().createDateValueElement();

        topTable.createPartLine("4.")
            .and().createStaticElement("Сведения  о  достижении утвержденных целевых показателей энергетической эффективности")
            .and().createStringValueElement()
            .and().createStaticElement("(достигнуты, не достигнуты)");


        headerTool(topTable, "h2-", "Оценка соответствия фактических значений\n" +
            "расчетно-нормативным по каждому показателю энергетической\n" +
            "эффективности, указанному в программе энергосбережения\n" +
            "и повышения энергетической эффективности\n");

        {
            final PDInnerTable pdTable = topTable.createPartInnerTable().key("TBL1_").createInnerTable();

            PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п").nextKeyValueIdx()
                .and().createStaticElement().caption("Наименование показателя энергетической эффективности").nextKeyValueIdx()
                .and().createStaticElement().caption("Единица измерения").nextKeyValueIdx()
                .and().createStaticElement().caption("Значение показателя")
                .createStaticChild("фактическое (по узлам (приборам) учета, расчетам)")
                .andParentCell()
                .createStaticChild("расчетно-нормативное за отчетный год").nextKeyValueIdx();

            partHeader.widthsOfElements(10, 30, 20, 15, 15);


            Consumer<PDTablePart> rowCreator = (p) -> p.createStringValueElement().nextKeyValueIdx()
                .and().createStringValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx()
                .and().createDoubleValueElement().nextKeyValueIdx();


            List<String> items = new ArrayList<>();
            items.add("По номенклатуре основной и дополнительной продукции");
            items.add("По видам проводимых работ");
            items.add("По видам оказываемых услуг");
            items.add("По основным энергоемким технологическим процессам");
            items.add("По основному технологическому оборудованию");

            int idx = 1;
            for (String s : items) {
                pdTable.createPartRowInner(idx+ "")
                    .and().createStaticElement(idx + "")
                    .and().createStaticElement(s).mergedCells(4);

                pdTable.createPartRowInner(idx + ".1")
                    .and().createStaticElement(idx + ".1")
                    .and().applyCreator(rowCreator);
                idx++;
            }
        }
        topTable.createPartLine().applyCreator(create1EmptyStaticF)
            .and().createStaticElement("1 т у.т. = 29,31 ГДж");

        headerTool(topTable, "h3-", "Сведения о выполненных энергоресурсосберегающих\n" +
            "мероприятиях по годам за пять лет, предшествующих\n" +
            "году представления информации, обеспечивших снижение\n" +
            "потребления энергетических ресурсов и воды");

        {
            final PDInnerTable pdTable = topTable.createPartInnerTable().key("TBL2_").createInnerTable();

            PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п").nextKeyValueIdx()
                .and().createStaticElement().caption("Наименование мероприятия").nextKeyValueIdx()
                .and().createStaticElement().caption("Единица измерения").nextKeyValueIdx()
                .and().createStaticElement().caption("Фактическая годовая экономия").nextKeyValueIdx()
                .and().createStaticElement().caption("Год внедрения").nextKeyValueIdx()
                .and().createStaticElement().caption("Краткое описание, достигнутый энергетический эффект").nextKeyValueIdx();

            partHeader.widthsOfElements(5, 25, 25, 15, 15, 30);


            pdTable.createPartRowNr("1")
                .and().createStaticElement("Энергоресурсосберегающие мероприятия, обеспечившие снижение потребления:").mergedCells(5);

            Consumer<PDTablePart> rowCreator = (p) -> p.createStringValueElement().keyValueIdx(1)
                .and().createStringValueElement().keyValueIdx(2)
                .and().createIntegerValueElement().keyValueIdx(3)
                .and().createDoubleValueElement().keyValueIdx(4)
                .and().createStringValueElement().keyValueIdx(5);

            class CreatorHelper {
                private final String s0;
                private final String s1;
                private final String pnt;
                private final int idx;
                public CreatorHelper(String s0, String s1, String pnt, int idx) {
                    this.s0 = s0;
                    this.s1 = s1;
                    this.pnt = pnt;
                    this.idx = idx;
                }

                private void build() {
                    pdTable.createPartRowInner(pnt + idx+ "")
                        .and().createStaticElement(pnt + idx + "")
                        .and().createStaticElement(s0)
                        .and().createStaticElement(s1).cellStyle(new PDCellStyle().hAlignment(HAlignment.CENTER))
                        .and().createStringValueElement().keyValueIdx(3)
                        .and().createStaticElement().mergedCells(2);

                    pdTable.createPartRowInner(pnt + idx + ".1")
                        .and().createStaticElement(pnt + idx + ".1")
                        .and().applyCreator(rowCreator);

                    pdTable.createPartRowInner(pnt + idx + ".2")
                        .and().createStaticElement(pnt + idx + ".2")
                        .and().applyCreator(rowCreator);
                }
            }


            List<String[]> items = new ArrayList<>();
            items.add(new String[] {"Электрической энергии", "тыс. кВт·ч"});
            items.add(new String[] {"Тепловой энергии", "Гкал"});
            items.add(new String[] {"Твердого топлива", "т"});
            items.add(new String[] {"Жидкого топлива", "т"});
            items.add(new String[] {"Природного газа", "тыс. н. куб. м"});
            items.add(new String[] {"Сжиженного газа", "тыс. т"});
            items.add(new String[] {"Сжатого газа", "тыс. н. куб. м"});
            items.add(new String[] {"Попутного нефтяного газа ", "тыс. н. куб. м"});

            int idx = 1;
            for (String[] s : items) {
                new CreatorHelper(s[0], s[1], "1.",idx++).build();
            }

            pdTable.createPartRowInner("1.9")
                .and().createStaticElement("1.9")
                .and().createStaticElement("Моторного топлива, в том числе:")
                .and().createStaticElement().mergedCells(2)
                .and().createStaticElement().mergedCells(2);

            List<String[]> items19 = new ArrayList<>();
            items19.add(new String[] {"бензина","тыс. л"});
            items19.add(new String[] {"керосина","тыс. л"});
            items19.add(new String[] {"дизельного топлива","тыс. л"});
            items19.add(new String[] {"сжиженного газа","т"});
            items19.add(new String[] {"сжатого газа","н. куб. м"});
            items19.add(new String[] {"твердого топлива","т"});
            items19.add(new String[] {"жидкого топлива","т"});

            idx = 1;
            for (String[] s: items19) {
                new CreatorHelper(s[0], s[1], "1.9.",idx++).build();
            }

        }

        topTable.createPartLine().applyCreator(create1EmptyStaticF)
            .and().createStaticElement("1 т у.т. = 29,31 ГДж");


        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_12() {

        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.12", "Сведения\n" +
            "о линиях передачи (транспортировки)\n" +
            "энергетических ресурсов \n")));

        headerTool(topTable, "h1-", "Сведения\n" +
            "о линиях передачи (транспортировки)\n" +
            "энергетических ресурсов \n");


        final PDInnerTable pdTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п").nextKeyValueIdx()
            .and().createStaticElement().caption("Наименование линии").nextKeyValueIdx()
            .and().createStaticElement().caption("Вид передаваемого ресурса").nextKeyValueIdx()
            .and().createStaticElement().caption("Способ прокладки").nextKeyValueIdx()
            .and().createStaticElement().caption("Суммарная протяженность, км").nextKeyValueIdx();

        partHeader.widthsOfElements(5, 25, 25, 15, 15);

        pdTable.createPartRow().key("DATA").dynamic()
            .and().createValueElement(PDTableCellValueCounter.class).nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().createDoubleValueElement().nextKeyValueIdx();

        return new EPSectionTemplateFactory(topTable);
    }


    /**
     *
     */
    public EnergyPassportSectionTemplateFactory section_2_13() {

        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.13", "Сведения\n" +
            "о протяженности воздушных и кабельных линий\n" +
            "передачи электроэнергии")));

        headerTool(topTable, "h1-", "Сведения\n" +
            "о протяженности воздушных и кабельных линий\n" +
            "передачи электроэнергии");


        final PDInnerTable pdTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п")
            .and().createStaticElement().caption("Класс напряжения")
            .and().createStaticElement().caption("Динамика изменения показателей по годам")
                .createStaticChild("предшествующие годы")
                    .createStaticChild("{YYYY-4}").nextKeyValueIdx().andParentCell()
                    .createStaticChild("{YYYY-3}").nextKeyValueIdx().andParentCell()
                    .createStaticChild("{YYYY-2}").nextKeyValueIdx().andParentCell()
                    .createStaticChild("{YYYY-1}").nextKeyValueIdx().andParentCell()
                .andParentCell()
                .createStaticChild("отчетный год").nextKeyValueIdx().cellStyle(new PDCellStyle().rowSpan(2));

        partHeader.widthsOfElements(5, 25, 15, 15, 15, 15, 15);


        class RowsCreator_2_13 {
            private final String nr;
            private final int startsWith;
            private final String footer;
            private List<String> items2 = new ArrayList<>();

            private RowsCreator_2_13(String nr, int startsWith, String footer, String... items2) {
                this.nr = nr;
                this.startsWith = startsWith;
                this.footer = footer;
                this.items2.addAll(Arrays.asList(items2));
            }

            private void create(final PDTable pdTable) {

                int idx = startsWith;
                for (String s : items2) {
                    pdTable.createPartRowNr(nr + "." + idx)
                        .createStaticElement(s)
                        .and().createValueElements(5, PDTableCellValueDouble.class)
                        .forEach((i) -> {
                            i.nextKeyValueIdx();
                        });
                    idx++;
                }

                if (footer != null) {
                    pdTable.createPartRow().key(nr + "_subtotal-" + startsWith + "-" + (idx - 1)).createStaticElement()
                        .and().createStaticElement(footer).cellStyle(new PDCellStyle().hAlignment(HAlignment.RIGHT))
                        .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
                        .forEach((i) -> {
                            i.nextKeyValueIdx();
                            i.setValueFunction("sum()");
                            i.setValueGroup("P_{}_i" + i.getKeyValueIdx());
                        });
                }
            }
        }

        PDTablePart part = pdTable.createPartRowNr("1").and();
        part.createStaticElement("Воздушные линии").mergedCells(6);

        new RowsCreator_2_13("1", 1,
            "Итого от 6 кВ и выше",
            "1150 кВ",
            "800 кВ",
            "750 кВ",
            "500 кВ",
            "400 кВ",
            "330 кВ",
            "220 кВ",
            "154 кВ",
            "110 кВ",
            "35 кВ",
            "27,5 кВ",
            "20 кВ",
            "10 кВ",
            "6 кВ"
            ).create(pdTable);

        new RowsCreator_2_13("1", 15,
            "Итого ниже 6 кВ",
            "3 кВ",
            "2 кВ",
            "500 В и ниже"
            ).create(pdTable);


        pdTable.createPartRow().key("1" + "_total").createStaticElement()
            .and().createStaticElement("Всего по воздушным линиям").cellStyle(new PDCellStyle().hAlignment(HAlignment.RIGHT))
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.nextKeyValueIdx();
                i.setValueFunction("sum()");
                i.setValueGroup("P_{1}_i" + i.getKeyValueIdx());
            });


        part = pdTable.createPartRowNr("2").and();
        part.createStaticElement("Кабельные линии").mergedCells(6);

        new RowsCreator_2_13("2", 1,
            "Итого от 6 кВ и выше",
            "220 кВ",
            "110 кВ",
            "35 кВ",
            "27,5 кВ",
            "20 кВ",
            "10 кВ",
            "6 кВ"
        ).create(pdTable);

        new RowsCreator_2_13("2", 8,
            "Итого ниже 6 кВ",
            "3 кВ",
            "2 кВ",
            "500 В и ниже"
        ).create(pdTable);

        pdTable.createPartRow().key("2" + "_total").createStaticElement()
            .and().createStaticElement("Всего по кабельным линиям").cellStyle(new PDCellStyle().hAlignment(HAlignment.RIGHT))
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.nextKeyValueIdx();
                i.setValueFunction("sum()");
                i.setValueGroup("P_{2}_i" + i.getKeyValueIdx());
            });

        pdTable.createPartRow().key("12" + "_total").createStaticElement()
            .and().createStaticElement("Всего по воздушным и кабельным линиям").cellStyle(new PDCellStyle().hAlignment(HAlignment.RIGHT))
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.nextKeyValueIdx();
                i.setValueFunction("sum()");
                i.setValueGroup("P_{1}_i" + i.getKeyValueIdx()+","+"P_{2}_i" + i.getKeyValueIdx());
            });


        part = pdTable.createPartRowNr("3").and();
        part.createStaticElement("Шинопроводы").mergedCells(6);

        new RowsCreator_2_13("3", 1,
            null,
            "800 кВ",
            "750 кВ",
            "500 кВ",
            "400 кВ",
            "330 кВ",
            "220 кВ",
            "154 кВ",
            "110 кВ",
            "35 кВ",
            "27,5 кВ",
            "20 кВ",
            "10 кВ",
            "6 кВ"
        ).create(pdTable);

        pdTable.createPartRow().key("3" + "_total").createStaticElement()
            .and().createStaticElement("Всего по шинопроводам").cellStyle(new PDCellStyle().hAlignment(HAlignment.RIGHT))
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.nextKeyValueIdx();
                i.setValueFunction("sum()");
                i.setValueGroup("P_{3}_i" + i.getKeyValueIdx());
            });


        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_14() {

        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.14", "Сведения\n" +
            "о количестве трансформаторов и их установленной мощности")));

        headerTool(topTable, "h1-", "Сведения\n" +
            "о количестве трансформаторов и их установленной мощности");


        final PDInnerTable pdTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);


        Consumer<PDTableCell<PDTableCellStatic>> creator = (c) -> {
            c.createStaticChild("количество, шт.").nextKeyValueIdx().andParentCell()
                .createStaticChild("установленная мощность, кВА").nextKeyValueIdx();
        };


        partHeader.createStaticElement().caption("№ п/п")
            .and().createStaticElement().caption("Единичная мощность, кВА").nextKeyValueIdx()
            .and().createStaticElement().caption("Высшее напряжение, кВ").nextKeyValueIdx()
            .and().createStaticElement("Динамика изменения показателей по годам").nextKeyValueIdx()
                .createStaticChild("предшествующие годы")
                    .createStaticChild("{YYYY-4}").applyCreator(creator).andParentCell()
                    .createStaticChild("{YYYY-3}").applyCreator(creator).andParentCell()
                    .createStaticChild("{YYYY-2}").applyCreator(creator).andParentCell()
                    .createStaticChild("{YYYY-1}").applyCreator(creator).andParentCell()
                .andParentCell()
             .createStaticChild("отчетный год").applyCreator(creator);

        partHeader.widthsOfElements(5, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15);




        class RowsCreator_2_14 {
            private final String nr;
            private final String s1;
            private List<String> items2 = new ArrayList<>();

            private RowsCreator_2_14(String nr, String s1, String ... items2) {
                this.nr = nr;
                this.s1 = s1;
                this.items2.addAll(Arrays.asList(items2));
            }

            private void create(final PDTable pdTable) {
                int idx = 0;
                for (String s : items2) {
                    PDTablePart part = pdTable.createPartRowNr(idx == 0 ? nr : nr + "." + idx)
                        .and();
                    if (idx == 0) {
                        part.createStaticElement(s1).cellStyle(new PDCellStyle().rowSpan(items2.size()).hAlignment(HAlignment.CENTER));
                    }
                    part.createStaticElement(s).cellStyle(new PDCellStyle().hAlignment(HAlignment.LEFT))
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx()
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx()
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx()
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx()
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx();
                    idx++;
                }
            }
        }

        new RowsCreator_2_14("1", "До 2500 включительно",
            "3 - 20",
            "27,5 - 35").create(pdTable);

        new RowsCreator_2_14("2", "От 2500 до 10000 включительно",
            "3 - 20",
            "35",
            "110 - 154").create(pdTable);

        new RowsCreator_2_14("3", "От 10000 до 80000 включительно",
            "3 - 20",
            "27,5 - 35",
            "110 - 154",
            "220").create(pdTable);

        new RowsCreator_2_14("4", "Более 80000",
            "110 - 154",
            "220", "330 однофазные",
            "330 трехфазные",
            "400 - 500 однофазные",
            "400 - 500 трехфазные",
            "750 - 1150").create(pdTable);

        pdTable.createPartRow("total","")
            .and().createStaticElement("Итого").cellStyle(new PDCellStyle().hAlignment(HAlignment.RIGHT)).mergedCells(2)
            .and().createValueElements(10, PDTableCellValueDoubleAggregation.class).forEach((i) -> {
            i.nextKeyValueIdx();
            i.setValueFunction("sum()");
            i.setValueGroup("P_{}_i" + i.getKeyValueIdx());
        });

        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_15() {

        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.15", "Сведения\n" +
            "о количестве и мощности устройств компенсации\n" +
            "реактивной мощности")));

        headerTool(topTable, "h1-", "Сведения\n" +
            "о количестве и мощности устройств компенсации\n" +
            "реактивной мощности");


        final PDInnerTable pdTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);


        Consumer<PDTableCell<PDTableCellStatic>> creator = (c) -> {
            c.createStaticChild("количество шт./групп").nextKeyValueIdx().andParentCell()
                .createStaticChild("установленная мощность, Мвар").nextKeyValueIdx();
        };


        partHeader.createStaticElement().caption("№ п/п")
            .and().createStaticElement().caption("Единичная мощность, кВА").nextKeyValueIdx()
            .and().createStaticElement().caption("Высшее напряжение, кВ").nextKeyValueIdx()
            .and().createStaticElement("Динамика изменения показателей по годам")
                .createStaticChild("предшествующие годы")
                    .createStaticChild("{YYYY-4}").applyCreator(creator).andParentCell()
                    .createStaticChild("{YYYY-3}").applyCreator(creator).andParentCell()
                    .createStaticChild("{YYYY-2}").applyCreator(creator).andParentCell()
                    .createStaticChild("{YYYY-1}").applyCreator(creator).andParentCell()
                .andParentCell()
             .createStaticChild("отчетный год").applyCreator(creator);

        partHeader.widthsOfElements(5, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15);


        class RowsCreator_2_15 {
            private final String nr;
            private final String s1;
            private List<String> items2 = new ArrayList<>();

            private RowsCreator_2_15(String nr, String s1, String ... items2) {
                this.nr = nr;
                this.s1 = s1;
                this.items2.addAll(Arrays.asList(items2));
            }

            private void create(final PDTable pdTable) {
                int idx = 0;
                for (String s : items2) {
                    PDTablePart part = pdTable.createPartRowNr(idx == 0 ? nr : nr + "." + idx).and();
                    if (idx == 0) {
                        part.createStaticElement(s1).cellStyle(new PDCellStyle().rowSpan(items2.size()+1).hAlignment(HAlignment.CENTER));
                    }
                    part.createStaticElement(s).cellStyle(new PDCellStyle().hAlignment(HAlignment.LEFT))
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx()
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx()
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx()
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx()
                        .and().createIntegerValueElement().nextKeyValueIdx()
                        .and().createDoubleValueElement().nextKeyValueIdx();
                    idx++;
                }

                pdTable.createPartRowNr(nr + "." + (items2.size() + 1))
                    .createStaticElement("Итого").cellStyle(new PDCellStyle().hAlignment(HAlignment.RIGHT))
                    .and().createValueElements(10, PDTableCellValueDoubleAggregation.class)
                        .forEach((i) -> {
                            i.nextKeyValueIdx();
                            i.setValueFunction("sum()");
                            i.setValueGroup("P_{}_i" + i.getKeyValueIdx());
                        });
            }
        }

        new RowsCreator_2_15("1", "Шунтирующие реакторы",
            "3 - 20 кВ",
            "27,5 - 35 кВ",
            "150 - 110 кВ",
            "500 кВ",
            "750 кВ").create(pdTable);

        new RowsCreator_2_15("2", "Синхронный компенсатор (СК) и генераторы в режиме СК",
            "До 15,0 МВА",
            "От 15,0 до 37,5 МВА",
            "50 МВА",
            "От 75,0 до 100,0 МВА",
            "160 МВА"
            ).create(pdTable);

        new RowsCreator_2_15("3", "Батарея статических конденсаторов и статический компенсатор",
            "0,38 - 20 кВ",
            "35 кВ",
            "150 - 110 кВ",
            "220 кВ и выше").create(pdTable);

        return new EPSectionTemplateFactory(topTable);
    }


    public EnergyPassportSectionTemplateFactory section_2_16() {

        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.16", "Сведения\n" +
            "о должностных лицах, ответственных за обеспечение\n" +
            "мероприятий по энергосбережению и повышению\n" +
            "энергетической эффективности")));

        headerTool(topTable, "h1-", "Сведения\n" +
            "о должностных лицах, ответственных за обеспечение\n" +
            "мероприятий по энергосбережению и повышению\n" +
            "энергетической эффективности \n");


        final PDInnerTable pdTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);



        partHeader.createStaticElement().caption("№ п/п").nextKeyValueIdx()
            .and().createStaticElement().caption("Ф.И.О.").nextKeyValueIdx()
            .and().createStaticElement().caption("Наименование должности").nextKeyValueIdx()
            .and().createStaticElement("Контактная информация (номера телефонов, факсов, адрес электронной почты)").nextKeyValueIdx()
            .and().createStaticElement("Основные функции и обязанности по обеспечению мероприятий").nextKeyValueIdx()
            .and().createStaticElement("Сведения о нормативных актах, определяющих обязанности по обеспечению мероприятий")
                .createStaticChild("N п/п").nextKeyValueIdx().andParentCell()
                .createStaticChild("наименование").nextKeyValueIdx().andParentCell()
                .createStaticChild("номер").nextKeyValueIdx().andParentCell()
                .createStaticChild("дата утверждения").nextKeyValueIdx().andParentCell();

        partHeader.widthsOfElements(5, 20, 20, 20, 20, 5, 20, 10, 10);


        final int subsectionCnt = 4;

        Consumer<PDTablePart> subsection = (p) -> {
            PDTableCell<?> cell = p.createStaticElement().vertical().nextKeyValueIdx();
            for (int i = 0; i < subsectionCnt; i++) {
                cell.createStaticChild((i + 1) + "").keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
            }
        };

        Consumer<PDTablePart> subsectionStr = (p) -> {
            PDTableCell<?> cell = p.createValuePackElement().vertical().nextKeyValueIdx();
            for (int i = 0; i < subsectionCnt; i++) {
                cell.createChildValue(PDTableCellValueString.class).keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
            }
        };

        Consumer<PDTablePart> subsectionDate = (p) -> {
            PDTableCell<?> cell = p.createValuePackElement().vertical().nextKeyValueIdx();
            for (int i = 0; i < subsectionCnt; i++) {
                cell.createChildValue(PDTableCellValueDate.class).keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
            }
        };

        pdTable.createPart(PDPartType.ROW).key("DATA").dynamic()
            .createValueElement(PDTableCellValueCounter.class).nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().applyCreator(subsection)
            .and().applyCreator(subsectionStr)
            .and().applyCreator(subsectionStr)
            .and().applyCreator(subsectionDate);


        return new EPSectionTemplateFactory(topTable);
    }


    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_17() {

        final PDTable topTable = new PDTable();

        topTable.applyCreator((t) -> tableHeaderCreator.accept(t, new PDKeyHeader("2.17", "Сведения\n" +
            "о квалификации персонала, обеспечивающего реализацию\n" +
            "мероприятий по энергосбережению и повышению\n" +
            "энергетической эффективности")));

        headerTool(topTable, "h1-", "Сведения\n" +
            "о квалификации персонала, обеспечивающего реализацию\n" +
            "мероприятий по энергосбережению и повышению\n" +
            "энергетической эффективности");


        final PDInnerTable pdTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п").nextKeyValueIdx()
            .and().createStaticElement().caption("Ф.И.О.").nextKeyValueIdx()
            .and().createStaticElement().caption("Наименование должности").nextKeyValueIdx()
            .and().createStaticElement("Сведения о квалификации")
                .createStaticChild("N п/п").nextKeyValueIdx().andParentCell()
                .createStaticChild("сведения об образовательной организации, проводившей обучение (наименование, адрес, лицензия)").nextKeyValueIdx().andParentCell()
                .createStaticChild("наименование и вид образовательной программы (подготовка, переподготовка, повышение квалификации)").nextKeyValueIdx().andParentCell()
                .createStaticChild("дата начала обучения").nextKeyValueIdx().andParentCell()
                .createStaticChild("дата окончания обучения").nextKeyValueIdx().andParentCell()
                .createStaticChild("документ об образовании (диплом, удостоверение, сертификат)").nextKeyValueIdx().andParentCell()
                .createStaticChild("сведения об аттестации и присвоении квалификации").nextKeyValueIdx().andParentCell();

        partHeader.widthsOfElements(5, 20, 20, 5, 15, 15, 10, 10, 15, 15);


        final int subsectionCnt = 4;

        Consumer<PDTablePart> subsection = (p) -> {
            PDTableCell<?> cell = p.createStaticElement().vertical().nextKeyValueIdx();
            for (int i = 0; i < subsectionCnt; i++) {
                cell.createStaticChild((i + 1) + "").keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
            }
        };

        Consumer<PDTablePart> subsectionStr = (p) -> {
            PDTableCell<?> cell = p.createValuePackElement().vertical().nextKeyValueIdx();
            for (int i = 0; i < subsectionCnt; i++) {
                cell.createChildValue(PDTableCellValueString.class).keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
            }
        };

        Consumer<PDTablePart> subsectionDate = (p) -> {
            PDTableCell<?> cell = p.createValuePackElement().vertical().nextKeyValueIdx();
            for (int i = 0; i < subsectionCnt; i++) {
                cell.createChildValue(PDTableCellValueDate.class).keyValueIdx(cell.getKeyValueIdx()).valuePackIdx(i + 1);
            }
        };

        pdTable.createPart(PDPartType.ROW).key("DATA").dynamic()
            .createValueElement(PDTableCellValueCounter.class).nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().createStringValueElement().nextKeyValueIdx()
            .and().applyCreator(subsection)
            .and().applyCreator(subsectionStr)
            .and().applyCreator(subsectionStr)
            .and().applyCreator(subsectionDate)
            .and().applyCreator(subsectionDate)
            .and().applyCreator(subsectionStr)
            .and().applyCreator(subsectionStr);

        return new EPSectionTemplateFactory(topTable);
    }



    /**
     *
     * @param <T>
     * @param <U>
     * @param <K>
     */
    @FunctionalInterface
    public interface TriConsumer<T, U, K> {

        void accept(T t, U u, K k);

        default TriConsumer<T, U, K> TriConsumer(TriConsumer<? super T, ? super U, ? super K> after) {
            Objects.requireNonNull(after);

            return (l, r, k) -> {
                accept(l, r, k);
                after.accept(l, r, k);
            };
        }
    }


    /**
     *
     * @param pdTable - to apply header
     * @param key - key of header
     * @param header - header with new line characters \n
     * @return PDTable
     */
    private PDTable headerTool(PDTable pdTable, String key, String header) {
        String[] headerLines = header.split("\n");
        for (int i = 0; i < headerLines.length; i++) {
            if (headerLines[i].trim().length() == 0) {
                continue;
            }
            pdTable.createPartLine(key + (i + 1), "")
                .and().createStaticElement(headerLines[i].trim())
                    .cellStyle(new PDCellStyle().hAlignment(HAlignment.CENTER).header1());
        }
        return pdTable;
    }



}
