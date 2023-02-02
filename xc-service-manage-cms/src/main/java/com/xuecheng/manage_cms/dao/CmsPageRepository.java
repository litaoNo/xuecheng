package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {

    public CmsPage findBySiteIdAndPageNameAndPageWebPath(String siteId,String pageName,String pageWebPath);

    public CmsPage findByPageId(String pageId);


}
