package com.lava.music.util;

import com.lava.music.dao.SongDao;
import com.lava.music.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Leon on 2018/1/2.
 */
@Component
public class TimeTask {

    @Autowired
    private SongService songService;

    @Scheduled(fixedRate = 1000*60*5)
    public void allotLabelTask(){
        songService.allotTask();
    }

    @Scheduled(fixedRate = 1000*60*10)
    public void allAuditTask(){
        songService.allotAuditTask();
    }

    @Scheduled(fixedRate = 1000*60*5)
    public void back(){
        songService.back();
    }
}
