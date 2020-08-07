package com.school.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailParseException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/*
    敏感词过滤器
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换字符
    private static final String REPLACE = "***";

    //初始化根节点
    private TrieNode rootNode = new TrieNode();
    //PostConstruct注解表示这是一个初始化方法，当容器实例化这个Bean以后，在调用构造方法之后，自动调用这个方法
    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                //把从文件中读取到的敏感词存放到keyword中，不为空则继续读
                //把keyword添加到前缀树
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    //将一个敏感词添加到前缀树
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i=0; i<keyword.length(); i++) {
            char c = keyword.charAt(i);
            //获取子节点
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                //子节点为空，则说明子节点在此之前不存在，初始化子节点
                subNode = new TrieNode();
                //把这个子节点放到当前节点的子节点位置
                tempNode.addSubNode(c, subNode);
            }

            //指向子节点，进入下一循环
            tempNode = subNode;
            //设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    //过滤器
    //text:待过滤的文本，返回的是过滤后的文本
    public String filter (String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //指针1：指向的是树
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //检测到的结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);

            //跳过符号
            if (isSymbol(c)) {
                //若指针1处于根节点，将符号计入结果，指针2下移一位
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头还是中间，指针3都向下走一步
                position++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一位置
                begin++;
                position++;
                //指针1重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                //发现敏感词，将begin ~ position的敏感词替换掉
                sb.append(REPLACE);
                //指针指向下一位置
                position++;
                begin = position;
                //指针1重新指向根节点
                tempNode = rootNode;
            } else {
                //继续检查下一个字符
                position++;
            }
        }
        //将最后一批字符放入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    //判断是否为符号
    private boolean isSymbol (Character c) {
        //这个函数用于判断c字符是不是合法字符
        //0x2E80到0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //定义前缀树的节点
    private class TrieNode {
        //该节点是不是敏感词结束的字符
        private boolean isKeywordEnd = false;

        //子节点（key是下级节点的字符，value是节点）
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        //添加子节点
        public void addSubNode(Character c, TrieNode subNode){
            subNodes.put(c, subNode);
        }
        //获取子节点:通过子节点的Key来获取它的value
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
