package com.travelfamilies.mapper;

import com.travelfamilies.pojo.Hotel;
import com.travelfamilies.pojo.Room;
import com.travelfamilies.request.hotelRequest.*;
import com.travelfamilies.response.GetHotelResponse;
import com.travelfamilies.response.GetRoomDayMess;
import com.travelfamilies.response.GetRoomInfoOrderResponse;
import com.travelfamilies.response.GetRoomResponse;
import org.apache.ibatis.annotations.*;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface HotelMapper {

    @Insert("insert into hotel(id,name,city,district,business_area,address,description,star_level,base_price,main_pic,status)" +
            " values (#{id},#{addHotelRequest.name},#{addHotelRequest.city},#{addHotelRequest.district}," +
            "#{addHotelRequest.businessArea},#{addHotelRequest.address},#{addHotelRequest.description}," +
            "#{addHotelRequest.starLevel},#{addHotelRequest.basePrice},#{mainPic},#{addHotelRequest.status})")
    int addHotel(String mainPic, HotelRequest addHotelRequest,long id);


    @Select("select name from hotel where name=#{name} and city=#{city}")
    String getHotelByName(String name, String city);

    @Insert("insert into hotel_room_type (hotel_id,room_name,bed_type,`window`,area,default_price,total_inventory,status,main_url)" +
            "values (#{addroomRequest.hotelId},#{addroomRequest.roomName},#{addroomRequest.bedType}," +
            "#{addroomRequest.window},#{addroomRequest.area},#{addroomRequest.defaultPrice}," +
            "#{addroomRequest.totalInventory},#{addroomRequest.status},#{main_url})")
    @Options(useGeneratedKeys = true, keyProperty = "addroomRequest.id", keyColumn = "id")
    int addRoom(String main_url, RoomRequest addroomRequest);

    int addStock(@Param("roomTypeId") Integer id, @Param("hotelId") Long hotelId,
                 @Param("totalInventory") int totalInventory, @Param("thirtyDays") List<String> thirtyDays,
                 @Param("price") Double price);

    @Update("update hotel set name=#{updateHotelRequest.name},city=#{updateHotelRequest.city}," +
            "district=#{updateHotelRequest.district},business_area=#{updateHotelRequest.businessArea}," +
            "address=#{updateHotelRequest.address},description=#{updateHotelRequest.description}," +
            "star_level=#{updateHotelRequest.starLevel},base_price=#{updateHotelRequest.basePrice} " +
            "where id=#{id}")
    int updateHotel(HotelRequest updateHotelRequest,long id);

    @Update("update hotel_room_type set room_name=#{roomName},bed_type=#{bedType},`window`=#{window},area=#{area}," +
            "default_price=#{defaultPrice},total_inventory=#{totalInventory},status=#{status} where id=#{id}")
    int updateRoom(RoomRequest updateRoomRequest);

    @Update("update hotel_room_stock set price=#{newPrice} where room_type_id=#{roomId} and hotel_id=#{hotelId} and sale_date >= NOW()")
    int updateStockPrice(Double newPrice, int roomId, Long hotelId);

    @Update("update hotel_room_stock set price=#{price},stock=#{stock} where sale_date=#{time} and room_type_id=#{roomTypeId}")
    int updateDayMess(UpdateDayMessRequest updateDayMessRequest);

    @Select("select room_name from hotel_room_type where id=#{id}")
    String getRoomById(Integer id);

    @Select("Select name from hotel where id=#{hotelId}")
    String getHotelByID(Long hotelId);

    @Select("select id from hotel_room_stock where sale_date=#{time} and room_type_id=#{roomTypeId}")
    Integer getStockByDay(String time, int roomTypeId);

    @Select("select id,hotel_id,default_price,total_inventory,status from hotel_room_type where status=1")
    List<GetRoomResponse> getRoom();

    int addStockByDay(@Param("rooms") List<GetRoomResponse> rooms,@Param("time") String time);

    int deleteStockByDay(@Param("rooms") List<GetRoomResponse> rooms,@Param("time") String beforeTime);

    @Select("select id,name,city,district,business_area,base_price,main_pic from hotel where status=1")
    List<GetHotelResponse> get();

    @Select("select * from hotel where id=#{hotelId}")
    Hotel getHotel(Long hotelId);

    List<Room> getRooms(GetRoomRequest getRoomRequest,String localDay);

    List<GetRoomDayMess> getDayMess(@Param("roomIds") List<Integer> roomsIds, @Param("today") String today);


    List<GetHotelResponse> searchHotel(QueryHotelRequest queryHotelRequest);

    List<GetHotelResponse> searchHotelByCityAndTime(@Param("reserveRoomRequest") ReserveRoomRequest reserveRoomRequest, String endDay);

    @Select("select count(*) from hotel_room_stock where " +
            "room_type_id=#{locateRoomRequest.roomId} " +
            "and status=1 " +
            "and sale_date between #{locateRoomRequest.startTime} and #{localDays} " +
            "and stock <= 0")
    int getLocateRoom(LocateRoomRequest locateRoomRequest,String localDays);

    @Select("select id,hotel_id,room_name,bed_type from hotel_room_type where id=#{roomId}")
    GetRoomInfoOrderResponse getRoomDetail(int roomId);

    @Select("select sum(price) from hotel_room_stock where " +
            "room_type_id=#{locateRoomRequest.roomId} " +
            "AND sale_date between #{locateRoomRequest.startTime} and #{localDays}")
    BigDecimal getTotalPrice(LocateRoomRequest locateRoomRequest, String localDays);

    @Update("update hotel_room_stock set stock=stock-1 where room_type_id=#{roomId}" +
            " and sale_date between #{stareTime} and #{localDays} " +
            "and stock>0 " +
            "and status=1")
    int reduceStockRoom(int roomId, String stareTime, String localDays);

    @Update("update hotel_room_stock set stock=stock+1 " +
            "where room_type_id=#{roomId} " +
            "and sale_date between #{startTime} and #{localDays}")
    int rollBackStock(Integer roomId, String startTime, String localDays);

}
