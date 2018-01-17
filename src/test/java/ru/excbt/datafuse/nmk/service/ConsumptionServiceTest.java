package ru.excbt.datafuse.nmk.service;

import com.fasterxml.uuid.Generators;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.QContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.service.DBSessionService;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.repository.ContZPointConsumptionRepository;
import ru.excbt.datafuse.nmk.repository.DataElConsumptionRepository;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplicationTest.class)
public class ConsumptionServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionServiceTest.class);

    @Autowired
    private ConsumptionService consumptionService;

    @Autowired
    private ContZPointConsumptionRepository zPointConsumptionRepository;

    @Autowired
    private ContZPointRepository contZPointRepository;

    @Autowired
    private DataElConsumptionRepository dataElConsumptionRepository;

    @Autowired
    private DBSessionService dbSessionService;

    @Test
    @Transactional
    public void testEntity() {
        ContZPointConsumption consumption = new ContZPointConsumption();
        consumption.setContServiceType(ContServiceTypeKey.CW.getKeyname());
        zPointConsumptionRepository.saveAndFlush(consumption);
    }


    /**
     *
     */
    @Test
    @Transactional
    public void processHWaterOne() {

        ContZPoint contZPoint = contZPointRepository.findOne(71843481L);

        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        ConsumptionTask task = ConsumptionTask.builder()
            .contZPointId(contZPoint.getId())
            .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
            .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
            .dateTimeFrom(day.atZone(ZoneId.systemDefault()).toInstant())
            .dateTimeTo(day.plusDays(1).minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant()).build();

        consumptionService.processHWater(task, true);
    }


    /**
     *
     */
    @Test
    @Transactional
    public void processHWaterDay() {

        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ConsumptionTask task = ConsumptionTask.builder()
            .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
            .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
            .dateTimeFrom(day.atZone(ZoneId.systemDefault()).toInstant())
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .dateTimeTo(day.plusDays(1).minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant()).build();

        consumptionService.processHWater(task, true);

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }

    /**
     *
     */
    @Test
    @Transactional
    public void processHWaterDayTask() {

        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ConsumptionTask task = ConsumptionTask.builder()
            .name("MyName")
            .dateTimeFrom(day.atZone(ZoneId.systemDefault()).toInstant())
            .dateTimeTo(day.plusDays(1).minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant())
            .contServiceType(ContServiceTypeKey.HW.getKeyname())
            .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
            .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
            .dataType(ConsumptionService.DATA_TYPE_HWATER)
            .retryCnt(3).build();

        task = consumptionService.saveConsumptionTask(task, ConsumptionService.TASK_STATE_SCHEDULED);

        consumptionService.processHWater(task);

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }

    @Test
    @Transactional
    public void invalidateConsumption() {
        LocalDateTime day = LocalDateTime.of(2017, 5, 26, 0,0);

        ConsumptionService.ConsumptionZPointKey key = ConsumptionService.ConsumptionZPointKey
            .builder()
            .contZPointId(71843465L)
            .contDateTime(day.atZone(ZoneId.systemDefault()).toInstant())
            .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
            .build();
        Set<ConsumptionService.ConsumptionZPointKey> keys = new HashSet<>();
        keys.add(key);
        consumptionService.invalidateConsumption(keys);
    }


    @Test
    //@Ignore
    public void testHWaterConsumption2016() {

        LocalDateTime startDay = LocalDateTime.of(2016, 1, 1, 0,0);
        LocalDateTime endDay = LocalDateTime.of(2016,1,2,0,0);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        LocalDateTime day = startDay;
        while (day.isBefore(endDay) || day.isEqual(endDay)) {
            log.info("Processing: {}-{}-{}", day.getYear(), day.getMonthValue(), day.getDayOfMonth());
            ConsumptionTask task = ConsumptionTask.builder()
                .name("MyName")
                .dateTimeFrom(day.atZone(ZoneId.systemDefault()).toInstant())
                .dateTimeTo(day.plusDays(1).minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant())
                .contServiceType(ContServiceTypeKey.HW.getKeyname())
                .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
                .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
                .taskUUID(Generators.timeBasedGenerator().generate())
                .dataType(ConsumptionService.DATA_TYPE_HWATER)
                .retryCnt(3).build();

            task = consumptionService.saveConsumptionTask(task, ConsumptionService.TASK_STATE_SCHEDULED);

            consumptionService.processHWater(task);

            day = day.plusDays(1);
        }

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }


    @Test
    @Transactional
    public void processElOne() {

        ContZPoint contZPoint = contZPointRepository.findOne(128551676L);

        LocalDateTime day = LocalDateTime.of(2016, 10, 14, 0,0);
        LocalDateTime endDay = day.plusDays(1).minusSeconds(1);

        ConsumptionTask task = ConsumptionTask.builder()
            .contZPointId(contZPoint.getId())
            .dataType(ConsumptionService.DATA_TYPE_ELECTRICITY)
            .contServiceType(ContServiceTypeKey.EL.getKeyname())
            .srcTimeDetailType(TimeDetailKey.TYPE_1H_ABS.getKeyname())
            .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
            .dateTimeFrom(day.atZone(ZoneId.systemDefault()).toInstant())
            .dateTimeTo(endDay.atZone(ZoneId.systemDefault()).toInstant()).build();

        consumptionService.processElCons(task);


        QContServiceDataElCons contServiceDataElCons = QContServiceDataElCons.contServiceDataElCons;



        Predicate predicate = contServiceDataElCons.contZPointId.eq(task.getContZPointId())
            .and(contServiceDataElCons.timeDetailType.eq(task.getSrcTimeDetailType()))
            .and(contServiceDataElCons.deleted.eq(0))
            .and(contServiceDataElCons.dataDate.lt(LocalDateUtils.asDate(day)));


        Pageable p = new PageRequest(1, 1,
            new Sort(new Sort.Order(Sort.Direction.DESC, contServiceDataElCons.id.getMetadata().getName())));

        Page<ContServiceDataElCons> dataList =
        dataElConsumptionRepository.findAll(predicate, p
            //new OrderSpecifier<>(Order.ASC, contServiceDataElCons.id)
        );

        JPAQueryFactory jpaQueryFactory = //dbSessionService.jpaQueryFactory();
            //dbSessionService.sqlQueryFactory();
            new JPAQueryFactory(dbSessionService.em());
        ContServiceDataElCons d = jpaQueryFactory.selectFrom(contServiceDataElCons).where(predicate).limit(1).fetchOne();
        List<ContServiceDataElCons> d_all = jpaQueryFactory.selectFrom(contServiceDataElCons).where(predicate).limit(1).fetch();
        //List<ContServiceDataElCons> d_all = jpaQueryFactory.selectFrom(contServiceDataElCons).where(predicate).limit(1).fetchAll();
            //.orderBy(contServiceDataElCons.id.desc()).limit(1);

        List<Tuple> resultList = jpaQueryFactory.select(
            contServiceDataElCons.contZPointId,

            contServiceDataElCons.p_Ap.max(),
            contServiceDataElCons.p_Ap1.max(),
            contServiceDataElCons.p_Ap2.max(),
            contServiceDataElCons.p_Ap3.max(),

            contServiceDataElCons.p_An.max(),
            contServiceDataElCons.p_An1.max(),
            contServiceDataElCons.p_An2.max(),
            contServiceDataElCons.p_An3.max(),

            contServiceDataElCons.q_Rp.max(),
            contServiceDataElCons.q_Rp1.max(),
            contServiceDataElCons.q_Rp2.max(),
            contServiceDataElCons.q_Rp3.max(),

            contServiceDataElCons.q_Rn.max(),
            contServiceDataElCons.q_Rn1.max(),
            contServiceDataElCons.q_Rn2.max(),
            contServiceDataElCons.q_Rn3.max()
        )
            .from(contServiceDataElCons).where(predicate).groupBy(contServiceDataElCons.contZPointId).fetch();

        resultList.forEach(i -> {
            Long id = i.get(contServiceDataElCons.contZPointId);
            Double p_An = i.get(contServiceDataElCons.p_An.max());
            Double p_Ap = i.get(contServiceDataElCons.p_Ap.max());
            log.info("id:{}, p_An: {}, p_Ap: {}", id, p_An, p_Ap);

            id = i.get(0, Long.class);
            p_An = i.get(5, Double.class);
            p_Ap = i.get(1, Double.class);
            log.info("id:{}, p_An: {}, p_Ap: {}", id, p_An, p_Ap);

            ContServiceDataElCons data = ConsumptionService.lastTupleDataToDalaElCons(i);

        });

        log.info("Found: {}", dataList.getContent().size());
        log.info("Found: {}", d_all.size());

        //dataElConsumptionRepository.findAll(, new PageRequest(1,1));
    }

    @Test
    //@Ignore
    public void testElConsumption2016() {

        LocalDateTime startDay = LocalDateTime.of(2016, 1, 1, 0,0);
        LocalDateTime endDay = LocalDateTime.of(2016,2,1,0,0);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        LocalDateTime day = startDay;
        while (day.isBefore(endDay) ) {
            log.info("Processing: {}-{}-{}", day.getYear(), day.getMonthValue(), day.getDayOfMonth());

            ConsumptionTask task = ConsumptionTask.builder()
                .name("MyName")
                .dataType(ConsumptionService.DATA_TYPE_ELECTRICITY)
                .contServiceType(ContServiceTypeKey.EL.getKeyname())
                .srcTimeDetailType(TimeDetailKey.TYPE_1H_ABS.getKeyname())
                .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
                .taskUUID(Generators.timeBasedGenerator().generate())
                .dateTimeFrom(day.atZone(ZoneId.systemDefault()).toInstant())
                .dateTimeTo(day.plusDays(1).minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant())
                .build();


//            ConsumptionTask task = ConsumptionTask.builder()
//                .name("MyName")
//                .dateTimeFrom(day.atZone(ZoneId.systemDefault()).toInstant())
//                .dateTimeTo(day.plusDays(1).minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant())
//                .contServiceType(ContServiceTypeKey.HW.getKeyname())
//                .srcTimeDetailType(TimeDetailKey.TYPE_1H.getKeyname())
//                .destTimeDetailType(TimeDetailKey.TYPE_24H.getKeyname())
//                .taskUUID(Generators.timeBasedGenerator().generate())
//                .dataType(ConsumptionService.DATA_TYPE_HWATER)
//                .retryCnt(3).build();

            task = consumptionService.saveConsumptionTask(task, ConsumptionService.TASK_STATE_SCHEDULED);

            consumptionService.processElCons(task);

            day = day.plusDays(1);
        }

        stopWatch.stop();
        log.info("Test Time: {}", stopWatch.toString());
    }

}
