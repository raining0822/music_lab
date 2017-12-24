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
     * 跳转到标签库页面
     * @param modelMap
     * @return
     */
    @RequestMapping("/list")
    public String dimensionList(ModelMap modelMap){
        return "label/label";
    }

    /**
     * 获取所有的标签，包括根标签
     * @return
     */
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

    @RequestMapping("/init_label_no")
    public void initLabelNo(){
        labelService.initLabelNo();
    }



    @RequestMapping("/all")
    @ResponseBody
    public List<Label> getAllLabel(@PathVariable Integer rootTag){
        if(rootLabel != null){
            return rootLabel.getSonLabels();
        }
        Label root = labelService.findById(1L);
        root = findAllLabelsByRoot(root);
        rootLabel = root;
        return rootLabel.getSonLabels();
    }




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
        return "label/experiment";
    }

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
     * 移动某一个标签
     * @param labelId
     * @param request
     * @return
     */
    @RequestMapping("/move/label")
    @ResponseBody
    public String moveLabel(@RequestParam String labelId, @RequestParam String targetId, @RequestParam String moveType, HttpServletRequest request){
        Label label = labelService.findById(Long.valueOf(labelId));
        Label targetLabel = null;
        if(StringUtils.hasText(targetId)){
            targetLabel = labelService.findById(Long.valueOf(targetId));
        }
        if(StringUtils.hasText(moveType) && targetLabel != null){
            if(moveType.equals("next")){
                Long selfFatherId = label.getFatherId();
                Long fatherId = targetLabel.getFatherId();
                if(selfFatherId.equals(fatherId)){
                    //只是改变顺序
                    List<Label> labelList = labelService.findLabel(selfFatherId);

                }else{
                    //修改父id，并且改变顺序
                }
            }
            else if(moveType.equals("prev")){

            }
            else if(moveType.equals("inner")){

            }
        }
        //labelService.moveLabel(Long.valueOf(labelId), Long.valueOf(targetId));
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
