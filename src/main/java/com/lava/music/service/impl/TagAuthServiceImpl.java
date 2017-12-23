package com.lava.music.service.impl;

import com.lava.music.dao.TagAuthDao;
import com.lava.music.service.BaseService;
import com.lava.music.service.TagAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mac on 2017/12/19.
 */
@Service
public class TagAuthServiceImpl extends BaseService implements TagAuthService {

    @Autowired
    private TagAuthDao tagAuthDao;
}
