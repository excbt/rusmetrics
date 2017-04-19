package ru.excbt.datafuse.nmk.data.energypassport;

import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.passdoc.*;

/**
 * Created by kovtonyk on 05.04.2017.
 */
@Service
public class EnergyPassport401_2014_Add {


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
    public EnergyPassportSectionTemplateFactory section_2_10(){

        final PDTable topTable = new PDTable().viewType(PDViewType.TABLE).sectionKey("S_2.10")
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
