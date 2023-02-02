package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {


}
