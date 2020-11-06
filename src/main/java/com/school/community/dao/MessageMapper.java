package com.school.community.dao;

import com.school.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查询当前用户的会话列表，针对每个会话只返回最新的一条私信，支持分页
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表，支持分页
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信的数量
    //两个功能：总共的未读私信的数量&与某个用户的会话中未读私信的数量
    //因此，conversationId这个参数是动态的，传了就根据这个查，没传就不考虑
    int selectLetterUnreadCount(int userId, String conversationId);

    //增加私信（发送私信）
    int insertMessage(Message message);

    //修改消息的状态
    int updateStatus(List<Integer> ids, int status);


}
