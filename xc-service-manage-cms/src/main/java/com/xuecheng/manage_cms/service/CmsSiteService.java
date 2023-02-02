package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsSiteService {

    @Autowired
    private CmsSiteRepository cmsSiteRepository;


    //查询所有站点信息
    public QueryResponseResult findList(QueryPageRequest queryPageRequest){

        List<CmsSite> all = cmsSiteRepository.findAll();
        QueryResult<CmsSite> cmsSiteQueryResult = new QueryResult<>();
        cmsSiteQueryResult.setList(all);
        cmsSiteQueryResult.setTotal(all.size());
        return new QueryResponseResult(CommonCode.SUCCESS,cmsSiteQueryResult);
    }
}
