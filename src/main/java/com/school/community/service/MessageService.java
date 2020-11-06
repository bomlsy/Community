package com.school.community.service;

import com.school.community.dao.MessageMapper;
import com.school.community.entity.Message;
import com.school.community.util.HostHolder;
import com.school.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    //查询当前用户的会话列表
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    //查询当前用户会话的数量
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    //查询私信列表
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    //查询私信的数量
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    //查询未读私信的数量
    public int findLetterUnread(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    //添加私信
    public int addMessage(Message message) {
        //过滤敏感词和html标签
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    //更新私信状态
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }
}
