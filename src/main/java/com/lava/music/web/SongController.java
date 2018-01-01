package com.lava.music.web;

import com.alibaba.fastjson.JSONObject;
import com.lava.music.model.*;
import com.lava.music.service.*;
import com.lava.music.util.CommonUtil;
import com.lava.music.util.HttpUtil;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2017/8/14.
 */
@RequestMapping("/song")
@Controller
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private UserRecordService userRecordService;

    @Autowired
    private SongRecordService songRecordService;

    @Autowired
    private UserService userService;



    /**
     * 分页显示曲库中的所有单曲
     * @param pageNo
     * @param modelMap
     * @return
     */
    @RequestMapping("/list/{pageNo}")
    public String songList(@PathVariable Integer pageNo, ModelMap modelMap){
        Integer totalCount = songService.findSongTotalCount();
        Page<Song> page = new Page<Song>(pageNo, 10, totalCount);
        page = songService.findSongByPage(page);
        modelMap.addAttribute("page", page);
        return "song/song";
    }

    /**
     * 根据某个标签查询打上该标签的所有单曲，分页显示
     * @param pageNo
     * @param labelId
     * @param modelMap
     * @return
     */
    @RequestMapping("/label/{labelId}/list/{pageNo}")
    public String songLabelList(@PathVariable Integer pageNo,@PathVariable String labelId, ModelMap modelMap){
        Integer totalCount = songService.findSongLabelTotalCount(Long.valueOf(labelId));
        Page<Song> page = new Page<Song>(pageNo, 10, totalCount);
        page = songService.findSongByLabelPage(page, Long.valueOf(labelId));
        Label label = labelService.findById(Long.valueOf(labelId));
        //该页面有转移标签的功能，这里是查询了所有的维度和维度下的标签
        List<Label> dimensionList =  labelService.findDimension();
        Map<Label, List<Label>> dimensionMap = new LinkedHashMap<Label, List<Label>>();
        for(Label dimension : dimensionList){
            if(dimension != null){
                List<Label> labelList = labelService.findLabel(dimension.getId());
                dimensionMap.put(dimension, labelList);
            }
        }
        modelMap.addAttribute("dimensionMap", dimensionMap);
        modelMap.addAttribute("page", page);
        modelMap.addAttribute("label", label);
        return "label/label_song";
    }

    /**
     * 根据多个标签显示对应的单曲，分页
     * @param pageNo
     * @param labelIds
     * @param modelMap
     * @return
     */
    @RequestMapping("/labels/list/{labelIds}/{pageNo}")
    public String songLabelsList(@PathVariable Integer pageNo, @PathVariable String labelIds, ModelMap modelMap){
        String labelIdsStr = labelIds;
        if(StringUtils.hasText(labelIds) && labelIds.contains("_")){
            if(labelIds.endsWith("_")){
                labelIds = labelIds.substring(0, labelIds.lastIndexOf("_"));
                labelIds = labelIds.replace("_",",");
            }
        }
        Integer totalCount = songService.findSongLabelsTotalCount(labelIds);
        Page<Song> page = new Page<Song>(pageNo, 10, totalCount);
        page = songService.findSongByLabelsPage(page, labelIds);
        List<Label> labels = labelService.findLabels(labelIds);
        modelMap.addAttribute("page", page);
        modelMap.addAttribute("labels", labels);
        modelMap.addAttribute("labelIdsStr",labelIdsStr);
        return "label/labels_song";
    }

    /**
     * 跳转到单曲打标签页
     * @param songId
     * @param modelMap
     * @return
     */
    @RequestMapping("/label/{songId}")
    public String toSongLabel(@PathVariable String songId,ModelMap modelMap, HttpServletRequest request){
        //查询单曲信息
        Song song = songService.findById(songId);
        modelMap.addAttribute("song", song);
        //查询标签信息
        List<Label> labelList = labelService.findLabelBySongId(songId);

        modelMap.addAttribute("labelList", labelList);
        return "song/song_label";
    }

    @RequestMapping("/audit/{songId}")
    public String toAuditLabel(@PathVariable String songId,ModelMap modelMap, HttpServletRequest request){
        //查询单曲信息
        Song song = songService.findById(songId);
        modelMap.addAttribute("song", song);
        //查询标签信息
        List<Label> labelList = labelService.findLabelBySongId(songId);
        modelMap.addAttribute("labelList", labelList);
        return "user/audit_label";
    }


    /**
     * 给一首单曲打上标签，多个标签
     * @param songId
     * @param labelIds
     * @param request
     * @return
     */
    @RequestMapping("/label/add/{songId}/{labelIds}")
    public String addLabel(@PathVariable String songId, @PathVariable String labelIds,HttpServletRequest request){
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        String labelIdsStr = labelIds;
        if(StringUtils.hasText(labelIds) && labelIds.contains("_")){
            if(labelIds.endsWith("_")){
                labelIds = labelIds.substring(0, labelIds.lastIndexOf("_"));
                labelIds = labelIds.replace("_",",");
            }
        }
        songService.addLabels(songId, labelIds, loginUser.getId());
        //添加日志
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.SONG_ADD_LABEL);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        userRecord.setSourceData(songId + "|" + labelIds);
        userRecordService.addRecord(userRecord);
        return "redirect:/user/work/label";
    }

    @RequestMapping("/audit/add/{songId}/{labelIds}")
    public String auditLabel(@PathVariable String songId, @PathVariable String labelIds,HttpServletRequest request){
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        String labelIdsStr = labelIds;
        if(StringUtils.hasText(labelIds) && labelIds.contains("_")){
            if(labelIds.endsWith("_")){
                labelIds = labelIds.substring(0, labelIds.lastIndexOf("_"));
                labelIds = labelIds.replace("_",",");
            }
        }
        songService.auditLabels(songId, labelIds, loginUser.getId());
        return "redirect:/user/work/audit";
    }

    /**
     * 推送单曲到任务池
     * @param pushIds
     * @return
     */
    @RequestMapping("/push")
    @ResponseBody
    public String pushSongIds(@RequestParam String pushIds, HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        try{
            if(StringUtils.hasText(pushIds)){
                songService.pushSong(pushIds, user.getId());
                return "success";
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }




    /**
     * 根据songId获取某一首单曲信息，目前用于播放
     * @param songId
     * @return
     */
    @RequestMapping("/{songId}")
    @ResponseBody
    public Song findSongById(@PathVariable String songId){
        Song song = songService.findById(songId);
        if(song != null){
            song.setPicUrl("http://img01.th-music.com/" + song.getAlbumPic());
            if(song.getTsId() != null){
                //获取tsId的播放地址
                String url = "http://apilava.dmhmusic.com/SONG/lslink.json?from=lava&ci=" + song.getTsId() + "&rate=128";
                String responseText = HttpUtil.sendGetRequest(url);
                JSONObject object = (JSONObject) JSONObject.parse(responseText);
                if(object.get("state").toString().equals("true")){
                    JSONObject dataObject = object.getJSONObject("data");
                    if(dataObject != null && dataObject.containsKey("path")){
                        song.setRealAudioUrl(dataObject.getString("path"));
                    }
                    else {
                        song.setRealAudioUrl("返回的data对象为空或没有path属性");
                    }
                }
                else{
                    song.setRealAudioUrl("服务器出错，code：" + object.get("errcode") + " |  message : " + object.get("errmsg"));
                }
            }
        }
        return song;
    }

    @RequestMapping("/test_task")
    public void testTask(){
        //songService.allotTask();
        songService.allotAuditTask();
    }
}
