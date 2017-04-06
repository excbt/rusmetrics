package ru.excbt.datafuse.nmk.data.service.energypassport;

import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.passdoc.*;

/**
 * Created by kovtonyk on 05.04.2017.
 */
@Service
public class EnergyPassport401_2014_Add {


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


        return new EPSectionTemplateFactory(topTable);
    }

    /**
     *
     * @return
     */
    public EnergyPassportSectionTemplateFactory section_2_10(){

        final PDTable topTable = new PDTable().viewType(PDViewType.TABLE).sectionKey("S_2.10")
            .caption("Краткая характеристика зданий (строений, сооружений)");


        final PDTable pdTable = topTable.createPartInnerTable().createInnerTable();

        PDTablePart partHeader = pdTable.createPart(PDPartType.HEADER);
        partHeader
            .createStaticElement("№ п/п").keyValueIdx(1)
            .and().createStaticElement("Наименование здания, строения, сооружения").keyValueIdx(2)
            .and().createStaticElement("Год ввода в эксплуатацию").keyValueIdx(3)
            .and().createStaticElement("Ограждающие конструкции")
            .createStaticChild().caption("наименование конструкции").keyValueIdx(4)
            .createStaticSibling().caption("краткая характеристика").keyValueIdx(5)
            .and().createStaticElement("Общая площадь, здания, строения, сооружения, кв. м").keyValueIdx(6)
            .and().createStaticElement("Отапливаемая площадь, здания, строения, сооружения, кв. м").keyValueIdx(7)
            .and().createStaticElement("Отапливаемый объем здания, строения, сооружения, куб. м").keyValueIdx(8)
            .and().createStaticElement("Износ здания, строения, сооружения, %").keyValueIdx(9);

        pdTable.createPart(PDPartType.ROW).key("DATA").dynamic()
            .createValueElement(PDTableCellValueCounter.class).keyValueIdx(1)// #
            .and().createStringValueElement().keyValueIdx(2) // name
            .and().createStringValueElement().keyValueIdx(3) // year
            .and().createStaticElement().keyValueIdx(4)
            .createStaticChild("Стены").keyValueIdx(4).valuePackIdx(1)
            .createStaticSibling("Окна").keyValueIdx(4).valuePackIdx(2)
            .createStaticSibling("Крыша").keyValueIdx(4).valuePackIdx(3)
            .and().createValuePackElement().keyValueIdx(5)
            .createChildValue(PDTableCellValueBoolean.class).keyValueIdx(5).valuePackIdx(1)
            .createSiblingValue(PDTableCellValueDouble.class).keyValueIdx(5).valuePackIdx(2)
            .createSiblingValue(PDTableCellValueDouble.class).keyValueIdx(5).valuePackIdx(3)
            .and().createDoubleValueElement().keyValueIdx(6)
            .and().createDoubleValueElement().keyValueIdx(7)
            .and().createDoubleValueElement().keyValueIdx(8);

        return new EPSectionTemplateFactory(topTable);
    }


}
