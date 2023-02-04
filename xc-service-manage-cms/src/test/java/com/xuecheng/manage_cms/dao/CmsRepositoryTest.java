package com.xuecheng.manage_cms.dao;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    @Test
    public void tesRestTemplate(){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/findById?id=5a791725dd573c3574ee333f", Map.class);

        Map body = forEntity.getBody();
        System.out.println(body);
    }


    @Test
    public void testGridFs() throws FileNotFoundException {
        String path = "d:/index_banner.ftl";
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectId store = gridFsTemplate.store(fileInputStream, "index_banner.ftl");
        System.out.println(store);

        System.out.println(path);
    }


    @Test
    public void down() throws IOException {
        // 根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("63dd06e345ef123344764862")));
        // 打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        // 创建gridFsResource，用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        // 获取文件内容
        String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");

        System.out.println(content);
    }
}
