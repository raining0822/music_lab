package com.lava.music.web;

import com.lava.music.model.Label;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.model.UserRecord;
import com.lava.music.service.LabelService;
import com.lava.music.service.UserRecordService;
import com.lava.music.service.UserService;
import com.lava.music.util.LabelUtil;
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

    @Autowired
    private UserService userService;

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
        root = initLabelSon(root);
        rootLabel = root;
        return root;
    }

    private Label initLabelSon(Label label){
        List<Label> labelList = labelService.findLabel(label);
        if(labelList != null && labelList.size() > 0){
            label.setSonLabels(labelList);
            for(Label label1 : labelList){
                initLabelSon(label1);
            }
        }
        return label;
    }

    /**
     * 刷新标签数据中的labelNO
     * 用于排序和归类
     */
    @RequestMapping("/init_label_no")
    public void initLabelNo(){
        labelService.initLabelNo();
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
                Long labelId = labelService.addLabel(labelName, label);
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
     * 移动某一个标签
     * @param labelId  需要移动的标签
     * @param targetId 移到的目标标签
     * @param moveType 移动类型
     * @param request
     * @return
     */
    @RequestMapping("/move/label")
    @ResponseBody
    public String moveLabel(@RequestParam String labelId, @RequestParam String targetId, @RequestParam String moveType, HttpServletRequest request){
        Label moveLabel = null;
        Label targetLabel = null;
        Label moveFatherLabel = null;
        Label targetFatherLabel = null;
        List<Label> moveLabelList = null;
        List<Label> targetLabelList = null;
        if(StringUtils.hasText(targetId) && StringUtils.hasText(labelId)){
            moveLabel = labelService.findById(Long.valueOf(labelId));
            moveFatherLabel = labelService.findById(moveLabel.getFatherId());
            moveLabelList = labelService.findLabel(moveLabel.getFatherId());
            targetLabel = labelService.findById(Long.valueOf(targetId));
            targetFatherLabel = labelService.findById(targetLabel.getFatherId());
            targetLabelList = labelService.findLabel(targetLabel.getFatherId());
        }
        if(StringUtils.hasText(moveType)){
            if(moveType.equals("inner")){
                //如果是inner
                //将移动标签从移动标签集合中删除
                moveLabelList.remove(moveLabel);
                //获取目标标签的子标签集合
                List<Label> targetLabelSonList = labelService.findLabel(targetLabel.getId());
                //将移动标签添加到目标标签的子标签集合汇总
                targetLabelSonList.add(moveLabel);
                //刷新两个List的labelNo
                targetLabelSonList = LabelUtil.flushLabelNo(targetLabel, targetLabelSonList);
                moveLabelList = LabelUtil.flushLabelNo(moveFatherLabel, moveLabelList);
                //将结果更新到数据库
                Integer number = labelService.updateLabel(targetLabel, targetLabelSonList);
                Integer number2 = labelService.updateLabel(moveFatherLabel, moveLabelList);
            }else{
                if(moveLabel.getFatherId().equals(targetLabel.getFatherId())){
                    //删除移动的标签
                    moveLabelList.remove(moveLabel);
                    if(moveType.equals("next")){
                        //将标签添加到目标标签的后面
                        moveLabelList.add(moveLabelList.indexOf(targetLabel) + 1, moveLabel);
                    }
                    else if(moveType.equals("prev")){
                        //将标签添加到目标标签的前面
                        Integer targetLabelIndex = moveLabelList.indexOf(targetLabel);
                        if(targetLabelIndex == 0){
                            LinkedList<Label> moveLabelLinkedList = new LinkedList<Label>(moveLabelList);
                            moveLabelLinkedList.addFirst(moveLabel);
                            moveLabelList = new ArrayList<Label>(moveLabelLinkedList);
                        }else{
                            moveLabelList.add(moveLabelList.indexOf(targetLabel), moveLabel);
                        }
                    }
                    //刷新List的labelNo
                    moveLabelList = LabelUtil.flushLabelNo(moveFatherLabel, moveLabelList);
                    //将结果更新到数据库
                    Integer number = labelService.updateLabel(moveFatherLabel, moveLabelList);
                }
                else{
                    //如果两个标签的父ID不一样
                    //将移动标签从移动标签的集合中删除
                    moveLabelList.remove(moveLabel);
                    //将移动标签添加到目标标签集合的中的目标标签的前面或后面
                    if(moveType.equals("next")){
                        targetLabelList.add(targetLabelList.indexOf(targetLabel) + 1, moveLabel);
                    }
                    else if(moveType.equals("prev")){
                        Integer targetLabelIndex = targetLabelList.indexOf(targetLabel);
                        if(targetLabelIndex == 0){
                            LinkedList<Label> targetLabelLinkedList = new LinkedList<Label>(targetLabelList);
                            targetLabelLinkedList.addFirst(moveLabel);
                            targetLabelList = new ArrayList<Label>(targetLabelLinkedList);
                        }else{
                            targetLabelList.add(targetLabelList.indexOf(targetLabel), moveLabel);
                        }
                    }
                    //刷新两个List的labelNo
                    targetLabelList = LabelUtil.flushLabelNo(targetFatherLabel, targetLabelList);
                    moveLabelList = LabelUtil.flushLabelNo(moveFatherLabel, moveLabelList);
                    //将结果更新到数据库
                    Integer number = labelService.updateLabel(targetFatherLabel, targetLabelList);
                    Integer number2 = labelService.updateLabel(moveFatherLabel, moveLabelList);
                }
            }
        }
        rootLabel = null;
        //添加日志
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.MOVE_LABEL);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        //userRecord.setSourceData(label.getFatherId() + "|" + labelId + "|" + targetId);
        //userRecordService.addRecord(userRecord);
        return "done";
    }


    @RequestMapping("/all")
    @ResponseBody
    public List<Label> getAllLabel(HttpServletRequest request){
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        List<TagAuth> tagAuthList = userService.findUserTagAuth(loginUser.getId());
        Label root = labelService.findById(1L);
        //查询根标签下的三个标签
        List<Label> rootSonList = labelService.findLabel(root.getId());
        List<Label> userLabels = new ArrayList<Label>();
        for(Label label : rootSonList){
            String labelName = label.getLabelName();
            for(TagAuth tagAuth : tagAuthList){
                if(labelName.trim().equals(tagAuth.getName().trim())){
                    label = initLabelSon(label);
                    userLabels.add(label);
                    break;
                }
            }
        }
        return userLabels;
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







    @RequestMapping("/experiment")
    public String experiment(ModelMap modelMap){
        return "label/experiment";
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
