package com.school.community.dao;

import com.school.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/*
    LoginTicket的Mapper
 */
@Mapper
@Deprecated //不推荐使用这个组件，改成使用Redis存储登录凭证
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "value(#{userId},#{ticket},#{status},#{expired})"
    })
    //自动生成主键，并注入给LoginTicket中的属性id
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改ticket的状态
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
