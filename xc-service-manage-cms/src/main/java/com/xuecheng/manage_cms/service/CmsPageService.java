package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsPageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;


    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){


        //构造查询条件
        if (queryPageRequest == null){
            queryPageRequest = new QueryPageRequest();
        }

        CmsPage cmsPage = new CmsPage();
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }

        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }

        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        
        //构建查询匹配器

        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);

        if (page <= 0){
            page = 1;
        }

        if (size <=0){
            size = 10;
        }

        page = page - 1; //mongodb从0开始分页

        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        long total = all.getTotalElements();
        List<CmsPage> content = all.getContent();
        QueryResult<CmsPage> pageQueryResult = new QueryResult<>();
        pageQueryResult.setList(content);
        pageQueryResult.setTotal(total);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,pageQueryResult);
        return queryResponseResult;
    }


    //添加cmsPage
    public CmsPageResult add(CmsPage cmsPage){

        //根据siteId,pageName,pageWebPath确定唯一索引
        CmsPage cmsPage1 = cmsPageRepository.findBySiteIdAndPageNameAndPageWebPath(cmsPage.getSiteId(), cmsPage.getPageName(), cmsPage.getPageWebPath());
        //如果查询不到则代表可以插入
        if(cmsPage1 == null) {
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }

        return new CmsPageResult(CommonCode.FAIL,null);
    }


    //根据pageId查询信息
    public CmsPageResult findById(String pageId){

        CmsPage cmsPage = cmsPageRepository.findByPageId(pageId);
        if(cmsPage != null){
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    //编辑页面
    public CmsPageResult edit(String pageId,CmsPage cmsPage){
        //根据页面id查询页面
        CmsPage cmsPage1 = cmsPageRepository.findByPageId(pageId);
        if(cmsPage1 != null){
            //更新模板id
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            cmsPage1.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            cmsPage1.setPageName(cmsPage.getPageName());
            //更新访问路径
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            CmsPage save = cmsPageRepository.save(cmsPage1);
            if(save != null){
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //返回失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }


    public ResponseResult delete(String pageId){

        //根據id查詢是否存在
        CmsPage cmsPage = cmsPageRepository.findByPageId(pageId);
        if(cmsPage != null){
            cmsPageRepository.deleteById(pageId);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
}
