package com.xuecheng.manage_cms.service;


import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsTemplateService {

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    public QueryResponseResult findAll() {

        List<CmsTemplate> allBySiteId = cmsTemplateRepository.findAll();
        if (allBySiteId != null) {
            QueryResult<CmsTemplate> cmsTemplateQueryResult = new QueryResult<>();
            cmsTemplateQueryResult.setList(allBySiteId);
            cmsTemplateQueryResult.setTotal(allBySiteId.size());
            return new QueryResponseResult(CommonCode.SUCCESS, cmsTemplateQueryResult);
        }
        return new QueryResponseResult(CommonCode.SUCCESS, new QueryResult());
    }

}
