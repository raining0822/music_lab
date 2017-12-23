package com.lava.music.web;

import com.lava.music.model.Song;
import com.lava.music.model.TagAuth;
import com.lava.music.model.User;
import com.lava.music.model.UserRecord;
import com.lava.music.service.SongService;
import com.lava.music.service.UserRecordService;
import com.lava.music.service.UserService;
import com.lava.music.util.CommonUtil;
import com.lava.music.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mac on 2017/8/17.
 */
@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRecordService userRecordService;

    @Autowired
    private SongService songService;

    /**
     * 跳转到登录页
     * @return
     */
    @RequestMapping("/go")
    public String toLogin(){
        return "user/login";
    }

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping("/login")
    public String login(@RequestParam String userName, @RequestParam String userPwd, HttpServletRequest request, ModelMap modelMap){
        User user = userService.login(userName, userPwd);
        if(user == null){
            modelMap.addAttribute("msg", "用户名或密码错误！");
            return "forward:/user/go";
        }
        if(user != null && user.getEffect() == 0) {
            modelMap.addAttribute("msg", "此用户已经被禁用！");
            return "forward:/user/go";
        }
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);
        userService.updateLoginTime(user.getId());
        //添加日志
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.LOGIN);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(user.getId());
        userRecordService.addRecord(userRecord);
        return "user/main";
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @RequestMapping("/out")
    public String loginOut(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        session.removeAttribute("loginUser");
        //添加日志
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.LOGIN_OUT);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(user.getId());
        userRecordService.addRecord(userRecord);
        return "redirect:/user/go";
    }

    /**
     * 跳转到修改密码页面
     * @return
     */
    @RequestMapping("/pwd")
    public String toPwd(){
        return "user/pwd";
    }

    /**
     * 分页查询所有用户
     * @param pageNo
     * @param modelMap
     * @return
     */
    @RequestMapping("/list/{pageNo}")
    public String list(@PathVariable Integer pageNo, ModelMap modelMap){
        Integer totalCount = userService.getTotalCount();
        Page<User> page = new Page<User>(pageNo, 10, totalCount);
        page  = userService.getPage(page);
        modelMap.addAttribute("page", page);
        return "user/list";
    }

    /**
     * 禁用某个用户
     * @param userId
     * @param pageNo
     * @param request
     * @return
     */
    @RequestMapping("/del/{userId}/{pageNo}")
    public String delUser(@PathVariable String userId, @PathVariable Integer pageNo, HttpServletRequest request){
        userService.delUser(userId);
        //添加日志
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.DELETE_USER);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(user.getId());
        userRecord.setSourceData(userId);
        userRecordService.addRecord(userRecord);
        return "redirect:/user/list/" + pageNo;
    }

    /**
     * 激活某一个用户
     * @param userId
     * @param pageNo
     * @param request
     * @return
     */
    @RequestMapping("/active/{userId}/{pageNo}")
    public String activeUser(@PathVariable String userId, @PathVariable Integer pageNo, HttpServletRequest request){
        userService.activeUser(userId);
        //添加日志
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.ACTIVE_USER);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(user.getId());
        userRecord.setSourceData(userId);
        userRecordService.addRecord(userRecord);
        return "redirect:/user/list/" + pageNo;
    }

    /**
     * 添加用户
     * @param userName
     * @param userType
     * @param modelMap
     * @param request
     * @return
     */
    @RequestMapping("/add")
    public String createUser(@RequestParam String userName, @RequestParam Integer userType, @RequestParam String userAuthority, ModelMap modelMap, HttpServletRequest request){
        User user = userService.findByUserName(userName);
        if(user != null){
            modelMap.addAttribute("msg","用户名不能重复！");
            return "forward:/user/toadd";
        }

        userService.addUser(userName, userType, userAuthority);
        //添加日志
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        UserRecord userRecord = new UserRecord();
        userRecord.setAction(UserRecord.ADD_USER);
        userRecord.setCreateTime(new Date());
        userRecord.setUserId(loginUser.getId());
        userRecord.setSourceData(userName);
        userRecordService.addRecord(userRecord);
        return "redirect:/user/list/1";
    }

    @RequestMapping("/edit")
    public String userEdit(@RequestParam String userName,@RequestParam String userId, @RequestParam Integer userType, @RequestParam String userAuthority,ModelMap modelMap ){
        User user = userService.findById(userId);
        user.setUserType(userType);
        userService.updateUser(user, userAuthority);
        return "redirect:/user/list/1";
    }

    /**
     * 跳转到添加用户页
     * @return
     */
    @RequestMapping("/toadd")
    public String toAdd(){
        return "user/add";
    }

    /**
     * 修改密码
     * @param oldPwd
     * @param newPwd
     * @param modelMap
     * @param request
     * @return
     */
    @RequestMapping("/change/pwd")
    public String changePwd(@RequestParam String oldPwd, @RequestParam String newPwd, @RequestParam String newPwd2, ModelMap modelMap, HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        if(user != null){
            if(!user.getUserPwd().equals(CommonUtil.MD5(oldPwd))){
                modelMap.addAttribute("msg","现有密码错误！");
                return "forward:/user/pwd";
            }
            else if(!newPwd.equals(newPwd2)){
                modelMap.addAttribute("msg","两次密码不一致！");
                return "forward:/user/pwd";
            }
            else{
                userService.updatePwd(user.getId(), CommonUtil.MD5(newPwd));
                //添加日志
                UserRecord userRecord = new UserRecord();
                userRecord.setAction(UserRecord.CHANGE_PWD);
                userRecord.setCreateTime(new Date());
                userRecord.setUserId(user.getId());
                userRecordService.addRecord(userRecord);
            }
        }
        return "redirect:/user/go";
    }

    /**
     * 获取某个用户的信息
     * @param userId
     * @param modelMap
     * @return
     */
    @RequestMapping("/info/{userId}")
    public String userInfo(@PathVariable String userId, ModelMap modelMap){
        User user = userService.findById(userId);
        //Integer addLabelCount = userRecordService.selectAddLabel(Long.valueOf(userId));
        //Integer delLabelCount = userRecordService.selectDelLabel(Long.valueOf(userId));
        Integer songCount = userRecordService.selectSong(Long.valueOf(userId));
        //modelMap.addAttribute("addLabelCount", addLabelCount);
        //modelMap.addAttribute("delLabelCount", delLabelCount);
        modelMap.addAttribute("songCount", songCount);
        modelMap.addAttribute("user", user);
        return "user/info";
    }

    @RequestMapping("/to_edit/{userId}")
    public String toEdit(@PathVariable String userId, ModelMap modelMap){
        User user = userService.findById(userId);
        List<TagAuth> authList = user.getTagAuthList();
        List<Integer> ids = new ArrayList<Integer>();
        for(TagAuth tagAuth : authList){
            ids.add(tagAuth.getId());
        }
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("tagAuthIds", ids);
        return "user/edit";
    }

    @RequestMapping("/work/{pageNo}")
    public String toWork(@PathVariable Integer pageNo, ModelMap modelMap, HttpServletRequest request){

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loginUser");
        //首先查询当前用户正在处理的任务数
        Integer totalCount = userService.findWorkSongTotalCount(user);
        if(totalCount < 100){
            Integer pullCount = 100 - totalCount;
            //算出还能添加多少任务
            //从任务池中拉取增量任务（拉取的时候要进行判断，只有符合当前用户权限的任务才能拉取）
            List<Song> pullList = songService.pullSongTask(user);
        }
        /*Page<Song> page = new Page<Song>(pageNo, 10, totalCount);
        page = songService.findWorkSongByPage(page,user);
        modelMap.addAttribute("page", page);*/
        return "user/work";

    }


}
