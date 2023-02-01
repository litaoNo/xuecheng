package com.xuecheng.framework.domain.cms.request;

import lombok.Data;

@Data
public class QueryPageRequest {

    //页面id
    private String pageId;

    //站点id
    private String siteId;

    //页面名称
    private String pageName;

    //页面别名
    private String pageAliase;

    //模板id
    private String templateId;

}
