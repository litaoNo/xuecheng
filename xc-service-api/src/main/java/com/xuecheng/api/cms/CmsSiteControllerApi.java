package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsSiteControllerApi {
    public QueryResponseResult findList(QueryPageRequest queryPageRequest);
}
