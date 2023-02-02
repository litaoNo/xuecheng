package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CmsPageControllerApi {

    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    public CmsPageResult add(CmsPage cmsPage);

    public CmsPageResult findById(String pageId);

    public CmsPageResult edit(String pageId,CmsPage cmsPage);

    public ResponseResult delete(String pageId);
}
