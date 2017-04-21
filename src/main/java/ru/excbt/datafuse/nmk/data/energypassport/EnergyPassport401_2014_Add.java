package ru.excbt.datafuse.nmk.data.energypassport;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.passdoc.*;

import java.util.function.Consumer;

/**
 * Created by kovtonyk on 05.04.2017.
 */
@Service
public class EnergyPassport401_2014_Add {



    public EnergyPassportSectionTemplateFactory section_1_3() {

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_1.3")
            .caption("ИНФОРМАЦИЯ\n" +
                "              о потреблении энергетических ресурсов в здании\n" +
                "                        (строении, сооружении) \n")
            .shortCaption("1.3")
            .sectionNr("1.3")
            .sectionHeader("ИНФОРМАЦИЯ\n" +
                "              о потреблении энергетических ресурсов в здании\n" +
                "                        (строении, сооружении) <*>\n" +
                "                                за 20__ год");

        topTable.createPartLine("1").createStaticElement("Место нахождения")
            .and().createStringValueElement();

        topTable.createPartLine("2").createStaticElement("Тип здания (строения, сооружения) и функциональное назначение:");


        topTable.createPartLine("2_type",false)
            .and().createStaticElement("         ")
            .and().createBooleanValueElement().keyValueIdx(1)
            .and().createStaticElement("ОБЩЕСТВЕННОЕ         ").keyValueIdx(1)
            .and().createBooleanValueElement().keyValueIdx(2)
            .and().createStaticElement("ЖИЛОЕ                ").keyValueIdx(2)
            .and().createBooleanValueElement().keyValueIdx(3)
            .and().createStaticElement("СТРОЕНИЕ, СООРУЖЕНИЕ ").keyValueIdx(3);

        final PDInnerTable innerTable = topTable.createPartInnerTable().key("P_2").createInnerTable();
        innerTable.noBorder(true);
        {
            innerTable.createPartRow("2a", false)
                .and().createStaticElement("Здравоохранение      ")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createStaticElement("Жилой дом            ")
                .and().createStringValueElement().keyValueIdx(2)
                .and().createStaticElement("                     ")
                .and().createStringValueElement().keyValueIdx(3);

            innerTable.createPartRow("2b", false)
                .and().createStaticElement("Воспитание и обучение")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createStaticElement("Общежитие            ")
                .and().createStringValueElement().keyValueIdx(2)
                .and().createStringValueElement().keyValueIdx(3);


            innerTable.createPartRow("2c", false)
                .and().createStaticElement("Наука                ")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createStaticElement("Гостиница            ")
                .and().createStringValueElement().keyValueIdx(2)
                .and().createStringValueElement().keyValueIdx(3);

            innerTable.createPartRow("2d", false)
                .and().createStaticElement("Управленческо-административное     ")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createStaticElement("Другое")
                .and().createStringValueElement().keyValueIdx(2)
                .and().createStringValueElement().keyValueIdx(3);

            innerTable.createPartRow("2e", false)
                .and().createStaticElement("Другое               ")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createStaticElement("                     ")
                .and().createStaticElement("                     ")
                .and().createStringValueElement().keyValueIdx(3);

        }


        topTable.createPartLine("2a-1", false)
            .and().createStaticElement("Здравоохранение      ")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Жилой дом            ")
            .and().createStringValueElement().keyValueIdx(2)
            .and().createStaticElement("                     ")
            .and().createStringValueElement().keyValueIdx(3);

        topTable.createPartLine("2b-1", false)
            .and().createStaticElement("Воспитание и обучение")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Общежитие            ")
            .and().createStringValueElement().keyValueIdx(2)
            .and().createStringValueElement().keyValueIdx(3);


        topTable.createPartLine("2c-1", false)
            .and().createStaticElement("Наука                ")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Гостиница            ")
            .and().createStringValueElement().keyValueIdx(2)
            .and().createStringValueElement().keyValueIdx(3);

        topTable.createPartLine("2d-0", false)
            .and().createStaticElement("Управленческо-       ");

        topTable.createPartLine("2d-1", false)
            .and().createStaticElement("административное     ")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("Другое")
            .and().createStringValueElement().keyValueIdx(2)
            .and().createStringValueElement().keyValueIdx(3);

        topTable.createPartLine("2e-1", false)
            .and().createStaticElement("Другое               ")
            .and().createStringValueElement().keyValueIdx(1)
            .and().createStaticElement("                     ")
            .and().createStaticElement("                     ")
            .and().createStringValueElement().keyValueIdx(3);

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

        topTable.createPartLine("3.8.2-1",false)
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

        topTable.createPartLine("3.9.1-1",false)
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

        topTable.createPartLine("3.11").createStaticElement("Окна");

        topTable.createPartLine("3.11a",false)
            .and().createStaticElement("Тип оконных блоков");

        topTable.createPartLine("3.11a",false)
            .and().createStaticElement("Деревянные рамы:")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.11b",false)
            .and().createStaticElement("- одинарные")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.11c",false)
            .and().createStaticElement("- двойные")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.11d",false)
            .and().createStaticElement("Энергосберегающие стеклопакеты:")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.11e",false)
            .and().createStaticElement("- однокамерные")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.11f",false)
            .and().createStaticElement("- двухкамерные (многокамерные)")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.11g",false)
            .and().createStaticElement("- двухкамерные (многокамерные) с напылением")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.11h",false)
            .and().createStaticElement("Другие (указать, какие)")
            .and().createStaticElement()
            .and().createStringValueElement();

        topTable.createPartLine("3.11i",false)
            .and().createStaticElement("Остекление энергосберегающими " +
            "стеклопакетами")
            .and().createStaticElement("(% от общего остекления)")
            .and().createStringValueElement();

        topTable.createPartLine("3.12")
            .and().createStaticElement("Входные двери")
            .and().createStaticElement("(есть/нет/количество)");
        topTable.createPartLine("3.12a",false)
            .and().createStaticElement("Одинарные")
            .and().createStaticElement()
            .and().createStringValueElement();
        topTable.createPartLine("3.12b",false)
            .and().createStaticElement("Двойные")
            .and().createStaticElement()
            .and().createStringValueElement();
        topTable.createPartLine("3.12c",false)
            .and().createStaticElement("Количество входов")
            .and().createStaticElement()
            .and().createStringValueElement();
        topTable.createPartLine("3.12d",false)
            .and().createStaticElement("Из них оборудованы:")
            .and().createStaticElement()
            .and().createStringValueElement();
        topTable.createPartLine("3.12e",false)
            .and().createStaticElement("- тамбуром")
            .and().createStaticElement()
            .and().createStringValueElement();
        topTable.createPartLine("3.12f",false)
            .and().createStaticElement("- доводчиком")
            .and().createStaticElement()
            .and().createStringValueElement();
        topTable.createPartLine("3.12g",false)
            .and().createStaticElement("- тепловой завесой в рабочем состоянии")
            .and().createStaticElement()
            .and().createStringValueElement();
        topTable.createPartLine("3.12h",false)
            .and().createStaticElement("- тепловой завесой с регулированием включения и отключения")
            .and().createStaticElement()
            .and().createStringValueElement();
        topTable.createPartLine("3.12i",false)
            .and().createStaticElement("- автоматизацией отключения тепловой завесы")
            .and().createStaticElement()
            .and().createStringValueElement();


        topTable.createPartLine("3.13")
            .and().createStaticElement("Крыша")
            .and().createStaticElement("(есть/нет)");

        topTable.createPartLine("3.13a",false)
            .and().createStaticElement("Без чердачного помещения")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13b",false)
            .and().createStaticElement("С чердачным помещением")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13c",false)
            .and().createStaticElement("В том числе:")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13d",false)
            .and().createStaticElement("- с холодным чердаком")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13e",false)
            .and().createStaticElement("- с утепленным чердаком")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13f",false)
            .and().createStaticElement("Утепление крыши")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13g",false)
            .and().createStaticElement("Плоская (мягкая) кровля:")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13h",false)
            .and().createStaticElement("- с однослойной системой теплоизоляции типовое решение)")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13i",false)
            .and().createStaticElement("- наличие технического этажа")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13j",false)
            .and().createStaticElement("- с двухслойной системой теплоизоляции")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13k",false)
            .and().createStaticElement("- наличие технического этажа")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13l",false)
            .and().createStaticElement("Металлическая:")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13m",false)
            .and().createStaticElement("- без утепления крыши изнутри")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13n",false)
            .and().createStaticElement("- без утепления чердачного помещения")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13p",false)
            .and().createStaticElement("- с утеплением крыши изнутри")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13q",false)
            .and().createStaticElement("- с утеплением чердачного помещения")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13r",false)
            .and().createStaticElement("Наличие протечек (конденсата) на потолке верхнего этажа")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13s",false)
            .and().createStaticElement("Отсутствие протечек (конденсата) на потолке верхнего этажа")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13t",false)
            .and().createStaticElement("Отсутствие наледи на крыше (во время отопительного сезона)")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.13u",false)
            .and().createStaticElement("Наличие наледи на крыше (во время отопительного сезона)")
            .and().createStaticElement()
            .and().createBooleanValueElement();


        topTable.createPartLine("3.14")
            .and().createStaticElement("Подвальные помещения")
            .and().createStaticElement("(есть/нет)");


        topTable.createPartLine("3.14a",false)
            .and().createStaticElement("Без подвального помещения")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.14b",false)
            .and().createStaticElement("С холодным подвалом")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.14c",false)
            .and().createStaticElement("С теплым подвалом")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.14d",false)
            .and().createStaticElement("Сырые")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.14e",false)
            .and().createStaticElement("В сухом состоянии")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.14f",false)
            .and().createStaticElement("Стены не промерзают")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.14g",false)
            .and().createStaticElement("Стены промерзают")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.14h",false)
            .and().createStaticElement("Имеется остекление")
            .and().createStaticElement()
            .and().createBooleanValueElement();


        topTable.createPartLine("3.15")
            .and().createStaticElement("Подключение к сетям инженерно-технического обеспечения")
            .and().createStaticElement("(есть/нет)");




        topTable.createPartLine("3.16")
            .and().createStaticElement("Присоединение к магистральной тепловой сети (при отсутствии собственного источника)")
            .and().createStaticElement("(есть/нет)");


        topTable.createPartLine("3.16a",false)
            .and().createStaticElement("Групповое (центральный тепловой пункт)(далее - ЦТП)")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.16b",false)
            .and().createStaticElement("Индивидуальное (индивидуальный тепловойпункт) (далее - ИТП)")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.16c",false)
            .and().createStaticElement("Индивидуальное с автоматизацией отопленияи горячего водоснабжения (далее - АИТП)")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.16d",false)
            .and().createStaticElement("Присоединение системы отопления:")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.16e",false)
            .and().createStaticElement("- зависимое")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("3.16f",false)
            .and().createStaticElement("- независимое")
            .and().createStaticElement()
            .and().createBooleanValueElement();

        topTable.createPartLine("4")
            .and().createStaticElement("Сведения о потреблении энергоресурсов в базовом году");

        topTable.createPartLine("4a",false)
            .and().createStaticElement("Тепловая энергия")
            .and().createStaticElement(EPConstants.GCAL_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4b",false)
            .and().createStaticElement("Электрическая энергия")
            .and().createStaticElement(EPConstants.KWH_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4c",false)
            .and().createStaticElement("Газ")
            .and().createStaticElement(EPConstants.KVM_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4d",false)
            .and().createStaticElement("Жидкое топливо")
            .and().createStaticElement(EPConstants.T_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4e",false)
            .and().createStaticElement("Твердое топливо")
            .and().createStaticElement(EPConstants.T_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("4f",false)
            .and().createStaticElement("Моторное топливо")
            .and().createStaticElement(EPConstants.L_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("5")
            .and().createStaticElement("Тарифы на оплату энергетических ресурсов");

        topTable.createPartLine("5a",false)
            .and().createStaticElement("Тепловая энергия")
            .and().createStaticElement(EPConstants.RUB_GCAL)
            .and().createDoubleValueElement();

        topTable.createPartLine()
            .and().createStaticElement("Электрическая энергия:");

        topTable.createPartLine("5b",false)
            .and().createStaticElement("- одноставочный тариф")
            .and().createStaticElement(EPConstants.RUB_KWH)
            .and().createDoubleValueElement();

        topTable.createPartLine("5c",false)
            .and().createStaticElement("- двуставочный тариф")
            .and().createStaticElement(EPConstants.RUB_KWH)
            .and().createDoubleValueElement();

        topTable.createPartLine("5d",false)
            .and().createStaticElement()
            .and().createStaticElement(EPConstants.RUB_KWH)
            .and().createDoubleValueElement();

        topTable.createPartLine("5e",false)
            .and().createStaticElement("Газ")
            .and().createStaticElement(EPConstants.KVM_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("5f",false)
            .and().createStaticElement("Твердое топливо")
            .and().createStaticElement(EPConstants.RUB_T)
            .and().createDoubleValueElement();

        topTable.createPartLine("5g",false)
            .and().createStaticElement("Моторное топливо")
            .and().createStaticElement(EPConstants.RUB_T)
            .and().createDoubleValueElement();

        topTable.createPartLine("6")
            .and().createStaticElement("Оплата энергетических ресурсов");

        topTable.createPartLine("6a",false)
            .and().createStaticElement("Тепловая энергия")
            .and().createStaticElement(EPConstants.RUB_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("6b",false)
            .and().createStaticElement("Электрическая энергия")
            .and().createStaticElement(EPConstants.RUB_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("6c",false)
            .and().createStaticElement("Газ")
            .and().createStaticElement(EPConstants.RUB_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("6d",false)
            .and().createStaticElement("Жидкое топливо")
            .and().createStaticElement(EPConstants.RUB_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("6e",false)
            .and().createStaticElement("Твердое топливо")
            .and().createStaticElement(EPConstants.RUB_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("6f",false)
            .and().createStaticElement("Моторное топливо")
            .and().createStaticElement(EPConstants.RUB_YEAR)
            .and().createDoubleValueElement();

        topTable.createPartLine("7")
            .and().createStaticElement("Сведения об оснащенности приборами учета");

        topTable.createPartLine("7.1")
            .and().createStaticElement("Коммерческий учет");

        topTable.createPartLine("7.1a",false)
            .and().createStaticElement("Количество вводов тепловой энергии:")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1b",false)
            .and().createStaticElement("- количество вводов, оборудованных узлами коммерческого учета")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1c",false)
            .and().createStaticElement("- в составе автоматизированной информационной системы (далее - АИС)")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1d",false)
            .and().createStaticElement("из них в составе:");

        topTable.createPartLine("7.1e",false)
            .and().createStaticElement("- индивидуального учета (на здание)")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1f",false)
            .and().createStaticElement("- в составе группового учета (на несколько зданий)")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1g",false)
            .and().createStaticElement("Количество вводов электрической энергии:")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1h",false)
            .and().createStaticElement("- количество вводов, оборудованных узлами коммерческого учета")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1i",false)
            .and().createStaticElement("- в составе АИС")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1j",false)
            .and().createStaticElement("из них в составе:");

        topTable.createPartLine("7.1k",false)
            .and().createStaticElement("- индивидуального учета (на здание)")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1l",false)
            .and().createStaticElement("- группового учета (на несколько зданий)")
            .and().createStaticElement()
            .and().createIntegerValueElement();


        topTable.createPartLine("7.1m",false)
            .and().createStaticElement("Количество вводов по газу:")
            .and().createStaticElement()
            .and().createIntegerValueElement();


        topTable.createPartLine("7.1n",false)
            .and().createStaticElement("- количество вводов, оборудованных узлами коммерческого учета")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1o",false)
            .and().createStaticElement("- в составе АИС")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1p",false)
            .and().createStaticElement("из них в составе:");

        topTable.createPartLine("7.1q",false)
            .and().createStaticElement("- индивидуального учета (на здание)")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1r",false)
            .and().createStaticElement("- группового учета (на несколько зданий)")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1s",false)
            .and().createStaticElement("из них в составе:");

        topTable.createPartLine("7.1t",false)
            .and().createStaticElement("- индивидуального учета (на здание)")
            .and().createStaticElement()
            .and().createIntegerValueElement();

        topTable.createPartLine("7.1u",false)
            .and().createStaticElement("- группового учета (на несколько зданий)")
            .and().createStaticElement()
            .and().createIntegerValueElement();


        section_helper1(topTable,"7.2.", "Технический учет",
            new String[]{"Суммарное количество узлов технического учета:",
                        "- по тепловой энергии",
                "- по электрической энергии",
                "- по газу"},
            PDTableCellValueBoolean.class,
            PDTableCellValueInteger.class);

//
//        topTable.createPartLine("7.2")
//            .and().createStaticElement("Технический учет")
//            .and().createStaticElement(EPConstants.YN)
//            .and().createBooleanValueElement();
//
//        topTable.createPartLine("7.2a",false)
//            .and().createStaticElement("Суммарное количество узлов технического учета:")
//            .and().createStaticElement()
//            .and().createIntegerValueElement();
//
//        topTable.createPartLine("7.2b",false)
//            .and().createStaticElement("- по тепловой энергии")
//            .and().createStaticElement()
//            .and().createIntegerValueElement();
//
//        topTable.createPartLine("7.2c",false)
//            .and().createStaticElement("- по электрической энергии")
//            .and().createStaticElement()
//            .and().createIntegerValueElement();
//
//        topTable.createPartLine("7.2d",false)
//            .and().createStaticElement("- по газу")
//            .and().createStaticElement()
//            .and().createIntegerValueElement();




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
            create2EmptyStaticF,
            new String[]{"A", "B", "C", "D", "E", "F", "G"},
            create2EmptyStaticF,
            PDTableCellValueBoolean.class,
            PDTableCellValueBoolean.class);


        return new EPSectionTemplateFactory(topTable);
    }


    private String removeLastPoint (String nr) {
        return (nr.length() > 0 && nr.charAt(nr.length() - 1) == '.' ? nr.substring(0, nr.length() - 1) : nr);
    }
    private final static Consumer<PDTablePart> create1EmptyStaticF = (p) -> p.createStaticElement();
    private final static Consumer<PDTablePart> create2EmptyStaticF = (p) -> p.createStaticElement().and().createStaticElement();



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
            }

            if (masterValueType != null)
                masterPart.createValueElement(valueType);

        }
        String alfa = "abcdefghijklmnopqrstuwxyz";

        Preconditions.checkState(points.length < alfa.length());

        int idx = 0;
        for (String s : points) {
            PDTablePart itemPart = pdTable.createPartLine(removeLastPoint(masterNr) + alfa.charAt(idx++), false);
            if (beforeItem != null) beforeItem.accept(itemPart);
            itemPart.createStaticElement(s);
            if (afterItem != null) afterItem.accept(itemPart);
            itemPart.createValueElement(valueType);
        }

    }

    private <M extends PDTableCell<M>, V extends PDTableCell<V>> void section_helper1(PDTable pdTable,
                                                                                      String masterNr,
                                                                                      String[] partHeaders,
                                                                                      String[] points,
                                                                                      final Class<M> masterValueType,
                                                                                      final Class<V> valueType) {

        section_helper1(pdTable, masterNr, partHeaders, null, points, create1EmptyStaticF, masterValueType, valueType);
    }


    private <M extends PDTableCell<M>, V extends PDTableCell<V>> void section_helper1(PDTable pdTable,
                                                                                      String masterNr,
                                                                                      String partHeader,
                                                                                      String[] points,
                                                                                      final Class<M> masterValueType,
                                                                                      final Class<V> valueType) {

        section_helper1(pdTable, masterNr, new String[]{partHeader}, points, masterValueType, valueType);
//
//        PDTablePart part =
//            pdTable.createPartLine(masterNr)
//                .and().createStaticElement(partHeader)
//                .and().createStaticElement().and();
//
//        if (masterValueType != null)
//            part.createValueElement(valueType);
//
//        String alfa = "abcdefghijklmnopqrstuwxyz";
//
//        Preconditions.checkState(points.length < alfa.length());
//
//        int idx = 0;
//        for (String s : points) {
//            pdTable.createPartLine(removeLastPoint(masterNr) + alfa.charAt(idx++))
//                .and().createStaticElement(s)
//                .and().createStaticElement()
//                .and().createValueElement(valueType);
//        }

    }


    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_1_4() {

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_1.4")
            .caption("ИНФОРМАЦИЯ\n" +
                "           о потреблении энергетических ресурсов на производство " +
                "                       продукции (работ, услуг)\n" +
                "                                за 20__ год\n")
            .shortCaption("1.4")
            .sectionNr("1.4")
            .sectionHeader("ИНФОРМАЦИЯ\n" +
                "           о потреблении энергетических ресурсов на производство\n" +
                "                       продукции (работ, услуг)\n" +
                "                                за 20__ год\n");


        topTable.createPartLine("1.")
            .and().createStaticElement("Промышленное производство (цех, участок)")
            .and().createStringValueElement();

        topTable.createPartLine("1.1.")
            .and().createStaticElement("Отраслевая принадлежность")
            .and().createStringValueElement();

        topTable.createPartLine("1.2.")
            .and().createStaticElement("Основные виды продукции")
            .and().createStringValueElement();

        topTable.createPart(PDPartType.SIMPLE_LINE).key("P_1_2b")
            .createStaticElement("Код основной продукции (работ, услуг) по ОКП")
            .and().createStringValueElement();

        topTable.createPart(PDPartType.SIMPLE_LINE).createStaticElement("2. ")
            .and().createStaticElement("Сведения о потреблении энергоресурсов по номенклатуре основной продукции\n" +
                "(работам, услугам)\n");

        final PDInnerTable innerTable = topTable.createPartInnerTable().key("P_2").createInnerTable();

        innerTable.createPartRowInner( "_heat")
            .createStaticElement("Тепловая энергия").keyValueIdx(1).and().createStaticElement(EPConstants.GCAL_YEAR)
            .and().createDoubleValueElement().keyValueIdx(1)
            .and().createStaticElement("Тепловая энергия").keyValueIdx(2).and().createStaticElement(EPConstants.TUT_YEAR)
            .and().createDoubleValueElement().keyValueIdx(2)
            .and().widthsOfElements(30,20,10,30,20,10);

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


        return new EPSectionTemplateFactory(topTable);
    }


    public EnergyPassportSectionTemplateFactory section_2_2() {
        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_2.2")
            .caption("2.2. Общие сведения " +
                "о лице, в отношении которого указана информация")
            .shortCaption("2.2")
            .sectionNr("2.2")
            .sectionHeader("Общие сведения " +
                "о лице, в отношении которого указана информация");

        topTable.createPart(PDPartType.SIMPLE_LINE).key("P_NAME")
            .and().createStringValueElement();

        topTable.createPart(PDPartType.SIMPLE_LINE).key("P_NAME")
            .and().createStaticElement("(полное наименование юридического лица, в отношении которого" +
            "указана информация)");

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


        return new EPSectionTemplateFactory(topTable);
    }

        /**
         * page 26 of Order
         * @return
         */
    public EnergyPassportSectionTemplateFactory section_2_3() {

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_2.3")
            .caption("2.3. Сведения об оснащенности приборами учета")
            .shortCaption("2.3")
            .sectionNr("2.3")
            .sectionHeader("Сведения об оснащенности приборами учета");


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

        pdTable.createPartRow("1.1.1")
            .and().createStaticElement("полученной от стороннего источника")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRow("1.1.2")
            .and().createStaticElement("собственного производства")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRow("1.1.3")
            .and().createStaticElement("потребленной на собственные нужды")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRow("1.1.4")
            .and().createStaticElement("отданной субабонентам (сторонним потребителям)")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        /// 1.2
        pdTable.createPartRow("1.2")
            .and().createStaticElement("Количество необорудованных узлами (приборами) учета точек приема (поставки), всего,\n" +
            "в том числе:\n")
            .and().createValueElements(6, PDTableCellValueDoubleAggregation.class).forEach(i -> {
            i.setValueFunction("sum()");
            i.setValueGroup("P_2.1.*");
        });

        pdTable.createPartRow("1.2.1")
            .and().createStaticElement("полученной от стороннего источника")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRow("1.2.2")
            .and().createStaticElement("собственного производства")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRow("1.2.3")
            .and().createStaticElement("потребленной на собственные нужды")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        pdTable.createPartRow("1.2.4")
            .and().createStaticElement("отданной субабонентам (сторонним потребителям)")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        // 1.3
        pdTable.createPartRow("1.3")
            .and().createStaticElement("Количество узлов (приборов) учета с нарушенными сроками поверки")
            .and().createValueElements(6, PDTableCellValueDouble.class);

        // 1.4
        pdTable.createPartRow("1.4")
            .and().createStaticElement("Количество узлов (приборов) учета с нарушением требований к классу точности " +
            "(относительной погрешности) узла (прибора) учета")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        // 2
        pdTable.createPartRow("2")
            .and().createStaticElement("Сведения об оснащенности узлами (приборами) технического учета").mergedCells(7);

        // 2.1
        pdTable.createPartRow("2.1")
            .and().createStaticElement("Суммарное количество узлов (приборов) учета")
            .and().createValueElements(6, PDTableCellValueDouble.class);


        return new EPSectionTemplateFactory(topTable);
    }

    // TODO Make rexexp for functions
    public EnergyPassportSectionTemplateFactory section_2_4() {

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_2.4")
            .caption("2.4. Сведения по балансу электрической энергии и его изменениях")
            .shortCaption("2.4")
            .sectionNr("2.4")
            .sectionHeader("Сведения по балансу электрической энергии и его изменениях");

        final PDInnerTable innerTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п");
        partHeader.createStaticElement().caption("Статья").columnKey("accounting");
        partHeader
            .createStaticElement().caption("Предшествующие годы")
                .createStaticChild("___").columnKey("YYYY-4")
                .createStaticSibling("___").columnKey("YYYY-3")
                .createStaticSibling("___").columnKey("YYYY-2")
                .createStaticSibling("___").columnKey("YYYY-1")
            .and().createStaticElement("Отчетный год").columnKey("YYYY");

        partHeader.widthsOfElements(10,50,15,15,15,15,15);

        innerTable.createPartRow("1")
            .and().createStaticElement("Приход").mergedCells(6);

        innerTable.createPartRow("1.1")
            .and().createStaticElement("Сторонний источник").columnKey("income_side")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("1.2")
            .and().createStaticElement("Собственное производство").columnKey("income_own")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRow("1_total", "")
            .createStaticElement("Итого суммарный приход")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("2")
            .and().createStaticElement("Расход").mergedCells(6);

        innerTable.createPartRow("2.1")
            .and().createStaticElement("На собственные нужды, всего, в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("2.1.1")
            .and().createStaticElement("производственный (технологический) расход")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.2")
            .and().createStaticElement("хозяйственные нужды")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.3")
            .and().createStaticElement("электрическое отопление")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.4")
            .and().createStaticElement("электрический транспорт")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.5")
            .and().createStaticElement("прочие собственные нужды")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.2")
            .and().createStaticElement("Субабоненты (сторонние потребители)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.3")
            .and().createStaticElement("Фактические (отчетные) потери, всего, в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.3.1|2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("2.3.1")
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

        innerTable.createPartRow("2.3.2")
            .and().createStaticElement("нерациональные потери")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2_total", "")
            .createStaticElement("Итого суммарный расход")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("3")
            .and().createStaticElement("Значения утвержденных нормативов потерь")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_5() {

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_2.5")
            .caption("Сведения по балансу тепловой энергии и его изменениях")
            .shortCaption("2.5")
            .sectionNr("2.5")
            .sectionHeader("Сведения по балансу тепловой энергии и его изменениях ");

        final PDInnerTable innerTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п");
        partHeader.createStaticElement().caption("Статья").columnKey("accounting");
        partHeader
            .createStaticElement().caption("Предшествующие годы")
            .createStaticChild("___").columnKey("YYYY-4")
            .createStaticSibling("___").columnKey("YYYY-3")
            .createStaticSibling("___").columnKey("YYYY-2")
            .createStaticSibling("___").columnKey("YYYY-1")
            .and().createStaticElement("Отчетный год").columnKey("YYYY");

        partHeader.widthsOfElements(10,50,15,15,15,15,15);

        innerTable.createPartRow("1")
            .and().createStaticElement("Приход").mergedCells(6);

        innerTable.createPartRow("1.1")
            .and().createStaticElement("Сторонний источник").columnKey("income_side")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("1.2")
            .and().createStaticElement("Собственное производство,\n" +
            "в том числе:").columnKey("income_own")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_1.2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("1.2.1")
            .and().createStaticElement("электрическое отопление").columnKey("income_own")
            .and().createValueElements(5, PDTableCellValueDouble.class);



        innerTable.createPartRow("1_total", "")
            .createStaticElement("Итого суммарный приход")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("2")
            .and().createStaticElement("Расход").mergedCells(6);

        innerTable.createPartRow("2.1")
            .and().createStaticElement("На собственные нужды, всего, в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("2.1.1")
            .and().createStaticElement("пара, из них контактным (острым) способом")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.2")
            .and().createStaticElement("горячей воды")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRow("2.2")
            .and().createStaticElement("Отопление и вентиляция, всего,\n" +
            "в том числе:\n")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.2.*");
                i.setValueFunction("sum()");
            });


        innerTable.createPartRow("2.2.1")
            .and().createStaticElement("калориферы воздушные")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRow("2.3")
            .and().createStaticElement("Горячее водоснабжение")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        innerTable.createPartRow("2.4")
            .and().createStaticElement("Субабоненты (сторонние потребители)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.5")
            .and().createStaticElement("Субабоненты (сторонние потребители)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2_total", "")
            .createStaticElement("Итого суммарный расход")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("3")
            .and().createStaticElement("Значения утвержденных нормативов потерь")
            .and().createValueElements(5, PDTableCellValueDouble.class);


        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_6() {

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_2.6")
            .caption("Сведения по балансу потребления котельно-печного топлива")
            .shortCaption("2.6")
            .sectionNr("2.6")
            .sectionHeader("Сведения по балансу тепловой энергии и его изменениях ");

        final PDInnerTable innerTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);

        partHeader.createStaticElement().caption("№ п/п");
        partHeader.createStaticElement().caption("Статья").columnKey("accounting");
        partHeader
            .createStaticElement().caption("Предшествующие годы")
            .createStaticChild("___").columnKey("YYYY-4")
            .createStaticSibling("___").columnKey("YYYY-3")
            .createStaticSibling("___").columnKey("YYYY-2")
            .createStaticSibling("___").columnKey("YYYY-1")
            .and().createStaticElement("Отчетный год").columnKey("YYYY");

        partHeader.widthsOfElements(10,50,15,15,15,15,15);

        innerTable.createPartRow("1")
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


        innerTable.createPartRow("2")
            .and().createStaticElement("Расход").mergedCells(6);

        innerTable.createPartRow("2.1")
            .and().createStaticElement("Технологическое использование, всего,\n" +
            "в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.1.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("2.1.1")
            .and().createStaticElement("нетопливное использование (в виде сырья)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.2")
            .and().createStaticElement("нагрев")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.3")
            .and().createStaticElement("сушка")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.4")
            .and().createStaticElement("обжиг (плавление, отжиг)")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.1.5")
            .and().createStaticElement("бытовое использование")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.2")
            .and().createStaticElement("На выработку тепловой энергии, всего,\n" +
            "в том числе:")
            .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
            .forEach((i) -> {
                i.setValueGroup("P_2.2.*");
                i.setValueFunction("sum()");
            });

        innerTable.createPartRow("2.2.1")
            .and().createStaticElement("в котельной")
            .and().createValueElements(5, PDTableCellValueDouble.class);

        innerTable.createPartRow("2.2.2")
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
    public EnergyPassportSectionTemplateFactory section_2_6b() {

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_2.6")
            .caption("Сведения по балансу потребления котельно-печного топлива")
            .shortCaption("2.6")
            .sectionNr("2.6")
            .sectionHeader("Сведения по балансу тепловой энергии и его изменениях ");

        {
            final PDInnerTable innerTable = topTable.createPartInnerTable().createInnerTable();

            PDTablePart partHeader = innerTable.createPart(PDPartType.HEADER);

            partHeader.createStaticElement().caption("№ п/п");
            partHeader.createStaticElement().caption("Статья").columnKey("accounting");
            partHeader
                .createStaticElement().caption("Предшествующие годы")
                .createStaticChild("___").columnKey("YYYY-4")
                .createStaticSibling("___").columnKey("YYYY-3")
                .createStaticSibling("___").columnKey("YYYY-2")
                .createStaticSibling("___").columnKey("YYYY-1")
                .and().createStaticElement("Отчетный год").columnKey("YYYY");

            partHeader.widthsOfElements(10, 50, 15, 15, 15, 15, 15);


            innerTable.createPartRow("1")
                .and().createStaticElement("Приход").mergedCells(6);


            PDTablePart partDynamic =
                innerTable.createPart(PDPartType.ROW).key("P_1").dynamic();
            partDynamic.createValueElement(PDTableCellValueCounter.class).columnKey("nr").keyValueIdx(1).counterPrefix("1.")
                .and().createStringValueElement().columnKey("accounting").keyValueIdx(2)
                .and().createValueElements(5, PDTableCellValueDouble.class, 3);

            partDynamic.indentAfter(false);
        }

        {
            final PDInnerTable innerTable2 = topTable.createPartInnerTable().createInnerTable();

            PDTablePart part =innerTable2.createPartRow("1_total", "");
            part.createStaticElement("Итого суммарный приход");
            part.createValueElements(5, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_1.*");
                    i.setValueFunction("sum()");
                });
            part.widthsOfElements(10, 50, 15, 15, 15, 15, 15);

            innerTable2.createPartRow("2")
                .and().createStaticElement("Расход").mergedCells(6);

            innerTable2.createPartRow("2.1")
                .and().createStaticElement("Технологическое использование, всего,\n" +
                "в том числе:")
                .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_2.1.*");
                    i.setValueFunction("sum()");
                });

            innerTable2.createPartRow("2.1.1")
                .and().createStaticElement("нетопливное использование (в виде сырья)")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable2.createPartRow("2.1.2")
                .and().createStaticElement("нагрев")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable2.createPartRow("2.1.3")
                .and().createStaticElement("сушка")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable2.createPartRow("2.1.4")
                .and().createStaticElement("обжиг (плавление, отжиг)")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable2.createPartRow("2.1.5")
                .and().createStaticElement("бытовое использование")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable2.createPartRow("2.2")
                .and().createStaticElement("На выработку тепловой энергии, всего,\n" +
                "в том числе:")
                .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_2.2.*");
                    i.setValueFunction("sum()");
                });

            innerTable2.createPartRow("2.2.1")
                .and().createStaticElement("в котельной")
                .and().createValueElements(5, PDTableCellValueDouble.class);

            innerTable2.createPartRow("2.2.2")
                .and().createStaticElement("в собственной тепловой электрической станции (включая выработку электрической энергии)")
                .and().createValueElements(5, PDTableCellValueDouble.class);


            innerTable2.createPartRow("2_total", "")
                .createStaticElement("Итого суммарный расход")
                .and().createValueElements(5, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_2.*");
                    i.setValueFunction("sum()");
                });

        }
        return new EPSectionTemplateFactory(topTable);
    }


    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_8() {

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_2.8")
            .caption("Сведения об использовании вторичных энергетических ресурсов")
            .shortCaption("2.8")
            .sectionNr("2.8");


        topTable.createPartLine("1h", "Сведения об использовании вторичных энергетических ресурсов");

        topTable.createPartLine("1h1", "Таблица 1");

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

            pdTable.createPartRow("1.1", "1")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createDoubleValueElement().keyValueIdx(2)
                .and().createDoubleValueElement().keyValueIdx(3)
                .and().createDoubleValueElement().keyValueIdx(4)
                .and().createDoubleValueElement().keyValueIdx(5)
                .and().createDoubleValueElement().keyValueIdx(6)
                .and().createDoubleValueElement().keyValueIdx(7)
                .and().createDoubleValueElement().keyValueIdx(8)
                .and().createStringValueElement().keyValueIdx(9);

            pdTable.createPartRow("1.2","2")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createDoubleValueElement().keyValueIdx(2)
                .and().createDoubleValueElement().keyValueIdx(3)
                .and().createDoubleValueElement().keyValueIdx(4)
                .and().createDoubleValueElement().keyValueIdx(5)
                .and().createDoubleValueElement().keyValueIdx(6)
                .and().createDoubleValueElement().keyValueIdx(7)
                .and().createDoubleValueElement().keyValueIdx(8)
                .and().createStringValueElement().keyValueIdx(9);


            PDTablePart table1TotalPart = pdTable.createPartRow("1_total", "");
            table1TotalPart.createStaticElement("Итого")
                .and().createStaticElement("").mergedCells(5)
                .and().createValueElements(2, PDTableCellValueDoubleAggregation.class)
                .forEach((i) -> {
                    i.setValueGroup("P_1.*");
                    i.setValueFunction("sum()");
                });
            table1TotalPart.createStaticElement();

        }
        topTable.createPartLine("2h", "Сведения\n" +
            "об использовании альтернативных (местных) топлив\n" +
            "и возобновляемых источников энергии");

        topTable.createPartLine("2h1", "Таблица 2");

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

            pdTable.createPartRow("2.1", "1")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createDoubleValueElement().keyValueIdx(2)
                .and().createDoubleValueElement().keyValueIdx(3)
                .and().createDoubleValueElement().keyValueIdx(4)
                .and().createDoubleValueElement().keyValueIdx(5)
                .and().createDoubleValueElement().keyValueIdx(6)
                .and().createDoubleValueElement().keyValueIdx(7)
                .and().createStringValueElement().keyValueIdx(8);

            pdTable.createPartRow("2.2","2")
                .and().createStringValueElement().keyValueIdx(1)
                .and().createDoubleValueElement().keyValueIdx(2)
                .and().createDoubleValueElement().keyValueIdx(3)
                .and().createDoubleValueElement().keyValueIdx(4)
                .and().createDoubleValueElement().keyValueIdx(5)
                .and().createDoubleValueElement().keyValueIdx(6)
                .and().createDoubleValueElement().keyValueIdx(7)
                .and().createStringValueElement().keyValueIdx(8);


            PDTablePart table2TotalPart = pdTable.createPartRow("2_total", "");
            table2TotalPart.createStaticElement("Итого")
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
    public EnergyPassportSectionTemplateFactory section_2_10(){

        final PDTable topTable = new PDTable().viewType(PDViewType.FORM).sectionKey("S_2.10")
            .caption("Краткая характеристика зданий (строений, сооружений)")
            .shortCaption("2.10")
            .sectionNr("2.10");


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


}
