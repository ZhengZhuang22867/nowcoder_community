package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try (
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while((keyword = reader.readLine()) != null){
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e) {
            logger.error("加载敏感词文件失败："+e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树中
    public void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i=0; i<keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 指针指向子节点，进入下一个循环
            tempNode = subNode;
            // 设置结束标识
            if(i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1：在前缀树上的指针
        TrieNode tempNode = rootNode;
        // 指针2：作用于文本，只会走一遍，指向的是疑似敏感词的开头
        int begin = 0;
        // 指针3：作用于文本，会多次倒退至begin的位置，记录疑似敏感词当前的字符位置，如果当前的字符是敏感词的结尾，则将begin到end位置的字符进行替换
        int end = 0;
        // 过滤后的文本
        StringBuilder sb = new StringBuilder();
        // 指针3是最先到结尾的字符，所以用它来进行遍历
        while(begin < text.length()){
            if(end < text.length()){
                char c = text.charAt(end);
                // 跳过敏感词中掺杂的符号
                if(isSymbol(c)){
                    // 指针1处于跟节点，将此结果计入结果，让指针2向后移动
                    if(tempNode == rootNode){
                        begin++;
                        sb.append(c);
                    }
                    // 无论符号在开头还是中间，指针3都向下走一步
                    end++;
                    continue;
                }
                // 不是符号
                // 检查rootNode的下级节点
                tempNode = tempNode.getSubNode(c);
                if(tempNode == null){
                    // 以begin开头的字符不是疑似敏感词
                    sb.append(text.charAt(begin));
                    // 移动2和3指针
                    end = ++begin;
                    // 指针1归位
                    tempNode = rootNode;
                }else if(tempNode.isKeywordEnd()){
                    // 发现敏感词
                    sb.append(REPLACEMENT);
                    // 移动2和3指针
                    begin = ++end;
                    // 指针1归位
                    tempNode = rootNode;
                }else{
                    // 开头是疑似敏感词，但是还不确定，继续检查
                    end++;
                }
            }else{ // end越界仍未找到敏感词
                sb.append(text.substring(begin));
                end = ++begin;
                tempNode = rootNode;
            }
        }
//        // 将最后一批字符计入结果
//        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断当前字符是否为符号
    private boolean isSymbol(Character c){
        // CharUtils.isAsciiAlphanumeric:判断是否是数字、大小写字母，如果是就返回true，否则返回false
        // 0x2E80~0x9FFF是东亚国家文字
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树的结构，不想被访问，所以设置为内部类
    private class TrieNode {
        // 关键词结束标识
        private boolean isKeywordEnd = false;
        // 子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            this.isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node){
            this.subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

    }

}
