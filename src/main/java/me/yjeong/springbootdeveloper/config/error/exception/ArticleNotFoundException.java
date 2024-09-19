package me.yjeong.springbootdeveloper.config.error.exception;

import me.yjeong.springbootdeveloper.config.error.ErrorCode;

public class ArticleNotFoundException extends BusinessBaseException{
    public ArticleNotFoundException(){
        super(ErrorCode.ARTICLE_NOT_FOUND);
    }
}
