package com.xh.blog.vo;

import lombok.Data;

//vo:把某个指定页面或组件的所需要的数据封装
@Data
public class PageParams {
    //当前页数，默认为1
    private int page = 1;
    //一页展示的数量
    private int pageSize = 10;

    private Long categoryId;

    private Long tagId;

    private String month;

    private String year;

    //如果月只有一位，则在月前加上0 如03
    public String getMonth() {
        if(this.month != null && this.month.length()==1){
            //方便对sql语句进行处理
            return "0"+this.month;
        }
        return month;
    }
}
