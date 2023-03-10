package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.CustomCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CmsPageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;


    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){


        //??????????????????
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
        
        //?????????????????????

        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);

        if (page <= 0){
            page = 1;
        }

        if (size <=0){
            size = 10;
        }

        page = page - 1; //mongodb???0????????????

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


    //??????cmsPage
    public CmsPageResult add(CmsPage cmsPage){

        //??????siteId,pageName,pageWebPath??????????????????
        CmsPage cmsPage1 = cmsPageRepository.findBySiteIdAndPageNameAndPageWebPath(cmsPage.getSiteId(), cmsPage.getPageName(), cmsPage.getPageWebPath());
        //???????????????????????????????????????
        if(cmsPage1 == null) {
            cmsPage.setPageId(null);
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }

        return new CmsPageResult(CommonCode.FAIL,null);
    }


    //??????pageId????????????
    public CmsPageResult findById(String pageId){

        CmsPage cmsPage = cmsPageRepository.findByPageId(pageId);
        if(cmsPage != null){
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    //????????????
    public CmsPageResult edit(String pageId,CmsPage cmsPage){
        //????????????id????????????
        CmsPage cmsPage1 = cmsPageRepository.findByPageId(pageId);
        if(cmsPage1 != null){
            //????????????id
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
            //??????????????????
            cmsPage1.setSiteId(cmsPage.getSiteId());
            //??????????????????
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            //??????????????????
            cmsPage1.setPageName(cmsPage.getPageName());
            //??????????????????
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            //??????????????????
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            CmsPage save = cmsPageRepository.save(cmsPage1);
            if(save != null){
                //????????????
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //????????????
        return new CmsPageResult(CommonCode.FAIL,null);
    }


    public ResponseResult delete(String pageId){

        //??????id??????????????????
        CmsPage cmsPage = cmsPageRepository.findByPageId(pageId);
        if(cmsPage != null){
            cmsPageRepository.deleteById(pageId);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //???????????????
    public String getPageHtml(String pageId) {
        //??????pageId??????????????????
        CmsPage cmsPage = cmsPageRepository.findByPageId(pageId);
        String dataUrl = cmsPage.getDataUrl();
        String templateId = cmsPage.getTemplateId();
        if(StringUtils.isEmpty(dataUrl)){
            CustomCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }

        if(StringUtils.isEmpty(templateId)){
            CustomCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }

        Map map = this.getMap(dataUrl);
        String templateFile = this.getTemplateFile(templateId);


        //?????????????????????
        String pageHtml = this.generateHtml(templateFile,map);

        return pageHtml;
    }


    //??????????????????
    public Map getMap(String dataUrl){
        //????????????????????????dataUrl??????
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map map = forEntity.getBody();
        return map;
    }


    //??????gridfs????????????
    public String getTemplateFile(String templateId) {

        //????????????id??????????????????id
        Optional<CmsTemplate> cmsTemplateOptional = cmsTemplateRepository.findById(templateId);
        if(!cmsTemplateOptional.isPresent()){
            CustomCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        CmsTemplate cmsTemplate = cmsTemplateOptional.get();
        String templateFileId = cmsTemplate.getTemplateFileId();

        // ??????id????????????
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        // ?????????????????????
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        // ??????gridFsResource????????????????????????
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        // ??????????????????
        String content = null;
        try {
            content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }


    //?????????????????????
    public String generateHtml(String template,Map map){
        //??????????????????
        Configuration configuration = new Configuration(Configuration.getVersion());
        //??????h???????????????
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",template);
        //?????????????????????
        configuration.setTemplateLoader(stringTemplateLoader);
        try {
            Template template1 = configuration.getTemplate("template");
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template1, map);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
