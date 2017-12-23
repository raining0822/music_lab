package com.lava.music.web;

import com.lava.music.model.Label;
import com.lava.music.model.User;
import com.lava.music.model.UserRecord;
import com.lava.music.service.LabelService;
import com.lava.music.service.UserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by mac on 2017/8/18.
 */
@Controller
@RequestMapping("/label")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @Autowired
    private UserRecordService userRecordService;

    private Label rootLabel = null;


    /**
     * 获取所有的维度和维度下的标签
     * @param modelMap
     * @return
     */
    @RequestMapping("/list")
    public String dimensionList(ModelMap modelMap){
        //List<Label> labelList_1 = getAllLabels();
        /*List<Label> dimensionList =  labelService.findDimension();
        Map<Label, List<Label>> dimensionMap = new LinkedHashMap<Label, List<Label>>();
        for(Label dimension : dimensionList){
            if(dimension != null){
                List<Label> labelList = labelService.findLabel(dimension.getId());
                dimensionMap.put(dimension, labelList);
            }
        }
        modelMap.addAttribute("dimensionMap", dimensionMap);*/
        //modelMap.addAttribute("labelList", labelList_1);
        return "label/label";
    }

    @RequestMapping("/all")
    @ResponseBody
    public List<Label> getAllLabel(){
        if(rootLabel != null){
            return rootLabel.getSonLabels();
        }
        Label root = labelService.findById(1L);
        root = findAllLabelsByRoot(root);
        rootLabel = root;
        return rootLabel.getSonLabels();
    }

    /*private List<Label> getAllLabels(){
        List<Label> labelList_1 = labelService.findLabel(1L);
        for(Label label : labelList_1){
            List<Label> labelList_2 = labelService.findLabel(label.getId());
            if(labelList_2 != null && labelList_2.size() > 0){
                label.setSonLabels(labelList_2);
                for(Label label1 : labelList_2){
                    List<Label> labelList_3 = labelService.findLabel(label1.getId());
                    if(labelList_3 != null && labelList_3.size() > 0){
                        label1.setSonLabels(labelList_3);
                    }
                }
            }
        }
        return labelList_1;
    }*/


    /**
     * 获取某个节点下的标签集合
     * @param fatherId
     * @return
     */
    @RequestMapping("/label_list/{fatherId}")
    @ResponseBody
    public List<Label> getLabelByFatherId(@PathVariable String fatherId){
        List<Label> labelList = labelService.findLabel(Long.valueOf(fatherId));
        return labelList;
    }

    @RequestMapping("/label_list/all")
    @ResponseBody
    public Label findAllLabels(){
        if(rootLabel != null){
            return rootLabel;
        }
        Label root = labelService.findById(1L);
        root = findAllLabelsByRoot(root);
        rootLabel = root;
        return root;
    }


    private Label findAllLabelsByRoot(Label label){
        List<Label> labelList = labelService.findLabel(label);
        if(labelList != null && labelList.size() > 0){
            label.setSonLabels(labelList);
            for(Label label1 : labelList){
                findAllLabelsByRoot(label1);
            }
        }
        return label;
    }


    @RequestMapping("/experiment")
    public String experiment(ModelMap modelMap){
        /*List<Label> dimensionList =  labelService.findDimension();
        Map<Label, List<Label>> dimensionMap = new LinkedHashMap<Label, List<Label>>();
        for(Label dimension : dimensionList){
            if(dimension != null){
                List<Label> labelList = labelService.findLabel(dimension.getId());
                dimensionMap.put(dimension, labelList);
            }
        }
        modelMap.addAttribute("dimensionMap", dimensionMap);*/
        return "label/experiment";
    }

    /**
     * 给标签库中添加维度
     * @param dimensionName
     * @param request
     * @return

    @RequestMapping("/add/dimension")
    public String addDimension(@RequestParam String dimensionName, HttpServletRequest request){
        labelService.addLabel(dimensionName, 1, null);
        //添加日志
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.ADD_DIMENSION);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        userRecord.setSourceData(dimensionName);
        userRecordService.addRecord(userRecord);
        return "redirect:/label/list";
    }
     */

    /**
     * 添加一个标签
     * @param labelName
     * @param fId
     * @param request
     * @return
     */
    @RequestMapping("/add/label")
    @ResponseBody
    public Long addLabel(@RequestParam String labelName, @RequestParam String fId, HttpServletRequest request){
        if(StringUtils.hasText(fId)){
            Label label = labelService.findById(Long.valueOf(fId));
            if(label != null){
                Long labelId = labelService.addLabel(labelName, label.getLabelLevel() + 1, Long.valueOf(fId));
                rootLabel = null;
                //添加日志
                HttpSession session = request.getSession();
                User loginUser = (User) session.getAttribute("loginUser");
                UserRecord userRecord = new UserRecord();
                userRecord.setAction(UserRecord.ADD_LABEL);
                userRecord.setCreateTime(new Date());
                userRecord.setUserId(loginUser.getId());
                userRecord.setSourceData(labelName + "|" + fId);
                userRecordService.addRecord(userRecord);
                return labelId;
            }
        }
        return null;
    }

    /**
     * 给维度添加分类
     * @param typeName
     * @param type_fId
     * @param request
     * @return

    @RequestMapping("/add/type")
    public String addType(@RequestParam String typeName, @RequestParam String type_fId, HttpServletRequest request){
        labelService.addLabel(typeName, 2, Long.valueOf(type_fId));
        //添加日志
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.ADD_LABEL);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        userRecord.setSourceData(typeName + "|" + type_fId);
        userRecordService.addRecord(userRecord);
        return "redirect:/label/list";
    }
     */
    /**
     * 将某一个维度下的标签转到另一个维度下
     * @param dimensionId
     * @param labelId
     * @param request
     * @return

    @RequestMapping("/move/{dimensionId}/{labelId}")
    public String moveLabel(@PathVariable String dimensionId, @PathVariable String labelId, HttpServletRequest request){
        labelService.moveLabel(Long.valueOf(labelId), Long.valueOf(dimensionId));
        //添加日志
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.MOVE_LABEL);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        userRecord.setSourceData(labelId + "|" + dimensionId);
        userRecordService.addRecord(userRecord);
        return "redirect:/label/list";
    }
     */

    /**
     * 移动某一个标签
     * @param labelId
     * @param request
     * @return
     */
    @RequestMapping("/move/label")
    @ResponseBody
    public String moveLabel(@RequestParam String labelId, @RequestParam String targetId, HttpServletRequest request){
        Label label = labelService.findById(Long.valueOf(labelId));
        labelService.moveLabel(Long.valueOf(labelId), Long.valueOf(targetId));
        rootLabel = null;
        //添加日志
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.MOVE_LABEL);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        userRecord.setSourceData(label.getFatherId() + "|" + labelId + "|" + targetId);
        userRecordService.addRecord(userRecord);
        return "done";
    }

    /**
     * 删除某一个标签
     * @param labelId
     * @param request
     * @return
     */
    @RequestMapping("/del/label")
    @ResponseBody
    public String delLabel(@RequestParam String labelId, HttpServletRequest request){
        List<Label> labelList = labelService.findLabel(Long.valueOf(labelId));
        if(labelList != null && labelList.size() > 0){
            return "son";
        }
        labelService.delLabel(Long.valueOf(labelId));
        rootLabel = null;
        //添加日志
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.DELETE_LABEL);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        userRecord.setSourceData(labelId);
        userRecordService.addRecord(userRecord);
        return "done";
    }
    /**
     * 编辑某一个标签
     * @param labelId
     * @param request
     * @return
     */
    @RequestMapping("/edit/label")
    @ResponseBody
    public String editLabel(@RequestParam String labelId,@RequestParam String labelName, HttpServletRequest request){
        labelService.updateLabel(Long.valueOf(labelId), labelName);
        rootLabel = null;
        //添加日志
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.EDIT_Label);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        userRecord.setSourceData(labelId + "|" + labelName);
        userRecordService.addRecord(userRecord);
        return "done";
    }

    /**
     * 删除某一个维度
     * @param dimensionId
     * @param request
     * @return

    @RequestMapping("/del/dimension/{dimensionId}")
    public String delDimension(@PathVariable String dimensionId, HttpServletRequest request){
        List<Label> labelList = labelService.findLabel(Long.valueOf(dimensionId));
        //只有当该维度下没有标签的时候，才能删除该维度
        if(labelList != null && labelList.size() < 1 ){
            labelService.delLabel(Long.valueOf(dimensionId));
            //添加日志
            HttpSession session = request.getSession();
            User loginUser = (User) session.getAttribute("loginUser");
            UserRecord userRecord = new UserRecord();
            userRecord.setAction(UserRecord.DELETE_DIMENSION);
            userRecord.setCreateTime(new Date());
            userRecord.setUserId(loginUser.getId());
            userRecord.setSourceData(dimensionId);
            userRecordService.addRecord(userRecord);
        }
        return "redirect:/label/list";
    }
     */
    /**
     * 获取某一首单曲的标签
     * @param songId
     * @return
     */
    @RequestMapping("/song/{songId}")
    @ResponseBody
    public List<Label> findSongLabel(@PathVariable String songId){
        List<Label> labelList = labelService.findLabelBySongId(songId);
        return labelList;
    }
}
