package com.danjam.search.querydsl;

import com.danjam.amenity.QAmenity;
import com.danjam.booking.QBooking;
import com.danjam.d_amenity.QDamenity;
import com.danjam.d_category.QDcategory;
import com.danjam.dorm.QDorm;
import com.danjam.room.QRoom;
import com.danjam.room.RoomController;
import com.danjam.search.SearchDto;
import com.danjam.users.QUsers;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.querydsl.core.types.Projections.list;

@Repository
@RequiredArgsConstructor
public class SearchRepoImpl implements SearchRepo {
    private final JPAQueryFactory queryFactory;
    private final QDorm qDorm = QDorm.dorm;
    private final QUsers qUsers = QUsers.users;
    private final QDcategory qDcategory = QDcategory.dcategory;
    private final QRoom qRoom = QRoom.room;
    /*private final QRoom subRoom = new QRoom("subRoom");

    private final JPQLQuery<Integer> groupByDorm = JPAExpressions
            .select(subRoom.price.min())
            .from(subRoom)
            .where(subRoom.dorm.id.eq(qDorm.id))
            .groupBy(subRoom.dorm.id);

    private final JPQLQuery<DormDto> dormList = JPAExpressions
            .select(Projections.constructor(DormDto.class,
                    qDorm.id,
                    qDorm.name,
                    qDorm.description,
                    qDorm.contactNum,
                    qDorm.city,
                    qDorm.town,
                    qDorm.address,
                    Projections.constructor(CategoryDto.class,
                            qDcategory.id,
                            qDcategory.name),
                    Projections.constructor(UserDto.class,
                            qUsers.id,
                            qUsers.name,
                            qUsers.role),
                    Projections.constructor(RoomDto.class,
                            qRoom.id,
                            qRoom.person,
                            qRoom.price)
            ))
            .from(qDorm)
            .join(qDorm.dcategory, qDcategory)
            .join(qDorm.user, qUsers)
            .join(qRoom).on(qDorm.id.eq(qRoom.dorm.id));*/

    @Override
    public List<DormDto> cheapRoom(SearchDto searchDto) {
        System.out.println("cheapRoom");
        String city = searchDto.getCity();
        LocalDateTime checkIn = searchDto.getCheckIn();
        LocalDateTime checkOut = searchDto.getCheckOut();
        int person = searchDto.getPerson();
        System.out.println(searchDto);

        QRoom subRoom = new QRoom("subRoom");
        QBooking subBooking = new QBooking("subBooking");

        JPQLQuery<Long> groupByDate = JPAExpressions
                .select(subBooking.room.id)
                .from(subBooking)
                .where(subBooking.checkIn.between(checkIn, checkOut),
                        subBooking.checkOut.between(checkIn, checkOut));

        JPQLQuery<Integer> groupByDorm = JPAExpressions
                .select(subRoom.price.min())
                .from(subRoom)
                .where(subRoom.dorm.id.eq(qDorm.id))
                .groupBy(subRoom.dorm.id);

        // 시티에 대한 검색조건만 존재할 때
        JPQLQuery<Long> searchByPerson = JPAExpressions
                .select(qRoom.id)
                .from(qRoom)
                .where(qRoom.person.loe(searchDto.getPerson()));

        return queryFactory
                .select(Projections.constructor(DormDto.class,
                        qDorm.id,
                        qDorm.name,
                        qDorm.description,
                        qDorm.contactNum,
                        qDorm.city,
                        qDorm.town,
                        qDorm.address,
                        Projections.constructor(CategoryDto.class,
                                qDcategory.id,
                                qDcategory.name),
                        Projections.constructor(UserDto.class,
                                qUsers.id,
                                qUsers.name,
                                qUsers.role),
                        Projections.constructor(RoomDto.class,
                                qRoom.id,
                                qRoom.person,
                                qRoom.price)
                        /*,
                        list(Projections.constructor(BookingDto.class,
                                qBooking.id,
                                qBooking.checkIn,
                                qBooking.checkOut,
                                qBooking.room))*/
//                        queryFactory.select(Projections.constructor(RoomDto.class,
//                                qRoom.id,
//                                qRoom.person,
//                                qRoom.price))
//                                .from(qRoom).where(minPrice.having(qRoom.price.eq(qRoom.price.min()))).groupBy(qRoom.dorm.id)
//                                Projections.constructor(CategoryDto.class,
//                                qDcategory.id,
//                                qDcategory.name),
//                        Projections.constructor(UserDto.class,
//                                qUsers.id,
//                                qUsers.name,
//                                qUsers.role),
//                        list(Projections.constructor(RoomDto.class,
//                                qRoom.id,
//                                qRoom.person,
//                                qRoom.price,
//                                list(Projections.constructor(BookingDto.class,
//                                        qBooking.id,
//                                        qBooking.checkIn,
//                                        qBooking.checkOut
//                                ))))
                ))
                .from(qDorm)
                .join(qDorm.dcategory, qDcategory)
                .join(qDorm.user, qUsers)
                .join(qRoom).on(qDorm.id.eq(qRoom.dorm.id))
                .where(
                        qRoom.id.notIn(groupByDate),
                        qRoom.price.eq(groupByDorm),
                        qDorm.city.eq(city),
                        qRoom.person.loe(person)
                )
                .fetch();
    }

    @Override
    public List<DormDto> findList() {
//        return dormList.where(qRoom.price.eq(groupByDorm)).fetch();
        QRoom subRoom = new QRoom("subRoom");

        JPQLQuery<Integer> groupByDorm = JPAExpressions
                .select(subRoom.price.min())
                .from(subRoom)
                .where(subRoom.dorm.id.eq(qDorm.id))
                .groupBy(subRoom.dorm.id);

        return queryFactory
                .select(Projections.constructor(DormDto.class,
                        qDorm.id,
                        qDorm.name,
                        qDorm.description,
                        qDorm.contactNum,
                        qDorm.city,
                        qDorm.town,
                        qDorm.address,
                        Projections.constructor(CategoryDto.class,
                                qDcategory.id,
                                qDcategory.name),
                        Projections.constructor(UserDto.class,
                                qUsers.id,
                                qUsers.name,
                                qUsers.role),
                        Projections.constructor(RoomDto.class,
                                qRoom.id,
                                qRoom.person,
                                qRoom.price)
                ))
                .from(qDorm)
                .join(qDorm.dcategory, qDcategory)
                .join(qDorm.user, qUsers)
                .join(qRoom).on(qDorm.id.eq(qRoom.dorm.id))
                .where(qRoom.price.eq(groupByDorm))
                .fetch();
    }

    @Override
    public List<String> findByCity(String city) {
        if (city.equalsIgnoreCase("선택")) {

        }
        return queryFactory
                .select(qDorm.dorm.town)
                .from(qDorm)
                .where(qDorm.city.eq(city))
                .distinct()
                .fetch();
    }

    @Override
    public List<DormDto> findByFilter(FilterDto filterDto) {
        System.out.println("findByAmenity");
        System.out.println(filterDto.getSearchDto());
        System.out.println(filterDto.getAmenities());
        System.out.println(filterDto.getCities());

        List<String> cities = filterDto.getCities();
        LocalDateTime checkIn = filterDto.getSearchDto().getCheckIn();
        LocalDateTime checkOut = filterDto.getSearchDto().getCheckOut();
        int person = filterDto.getSearchDto().getPerson();

        QDorm subDorm = new QDorm("subDorm");
        QRoom subRoom = new QRoom("subRoom");
        QBooking subBooking = new QBooking("subBooking");
        QAmenity subAmenity = new QAmenity("subAmenity");
        QDamenity subDamenity = new QDamenity("subDamenity");

        JPQLQuery<Long> groupByDate = JPAExpressions
                .select(subBooking.room.id)
                .from(subBooking)
                .where(subBooking.checkIn.between(checkIn, checkOut),
                        subBooking.checkOut.between(checkIn, checkOut));

        JPQLQuery<Integer> groupByDorm = JPAExpressions
                .select(subRoom.price.min())
                .from(subRoom)
                .where(subRoom.dorm.id.eq(qDorm.id))
                .groupBy(subRoom.dorm.id);

        // filterDto에서 골라준 town과 같은 town의 호텔 반환
        JPQLQuery<Long> groupByTown = JPAExpressions
                .select(subDorm.id)
                .from(subDorm)
                .where(subDorm.town.in(filterDto.getCities()));

//        JPQLQuery<String> groupByCity =
//                JPAExpressions
//                .select(subDorm.town)
//                .from(subDorm)
//                .where(subDorm.city.eq(filterDto.getSearchDto().getCity()))
//                .distinct().fetch().forEach(System.out::println);

//        JPQLQuery<Long> groupByFilterTown = JPAExpressions
//                .select(subDorm.id)
//                .from(subDorm)
//                .where(subDorm.town.contains(groupByCity));

        // dorm이 가진 amenity 리스트
        /*JPQLQuery<Long> groupByDamenity = JPAExpressions
                .select(subDamenity.amenity.id)
                .from(subDamenity)
                .where(subDamenity.dorm.id.eq(qDorm.id))
                .groupBy(subDamenity.dorm.id);*/

        return queryFactory
                .select(Projections.constructor(DormDto.class,
                        qDorm.id,
                        qDorm.name,
                        qDorm.description,
                        qDorm.contactNum,
                        qDorm.city,
                        qDorm.town,
                        qDorm.address,
                        Projections.constructor(CategoryDto.class,
                                qDcategory.id,
                                qDcategory.name),
                        Projections.constructor(UserDto.class,
                                qUsers.id,
                                qUsers.name,
                                qUsers.role),
                        Projections.constructor(RoomDto.class,
                                qRoom.id,
                                qRoom.person,
                                qRoom.price)
//                        queryFactory.select(Projections.constructor(RoomDto.class,
//                                qRoom.id,
//                                qRoom.person,
//                                qRoom.price))
//                                .from(qRoom).where(minPrice.having(qRoom.price.eq(qRoom.price.min()))).groupBy(qRoom.dorm.id)
//                                Projections.constructor(CategoryDto.class,
//                                qDcategory.id,
//                                qDcategory.name),
//                        Projections.constructor(UserDto.class,
//                                qUsers.id,
//                                qUsers.name,
//                                qUsers.role),
//                        list(Projections.constructor(RoomDto.class,
//                                qRoom.id,
//                                qRoom.person,
//                                qRoom.price,
//                                list(Projections.constructor(BookingDto.class,
//                                        qBooking.id,
//                                        qBooking.checkIn,
//                                        qBooking.checkOut
//                                ))))
                ))
                .from(qDorm)
                .join(qDorm.dcategory, qDcategory)
                .join(qDorm.user, qUsers)
                .join(qRoom).on(qDorm.id.eq(qRoom.dorm.id))
                .where(
                        qRoom.id.notIn(groupByDate),
                        qRoom.price.eq(groupByDorm),
                        qDorm.id.in(groupByTown),
//                        qDorm.id.eq(groupByDamenity.co),
//                        qDorm.city.eq(filterDto.getSearchDto().getCity()),
//                        qDorm.town.contains(groupByCity),
//                        qDorm.id.eq(groupByFilterTown),
                        qRoom.person.loe(person))
                .fetch();
    }
}
