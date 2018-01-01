package com.lava.music.util;

import com.lava.music.dao.LabelDao;
import com.lava.music.model.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mac on 2017/12/26.
 */

public class LabelUtil {


    /**
     * 获取一个新节点的labelNo
     * @param fatherLabel
     * @param sonList
     * @return
     */
    public static String getLabelNo(Label fatherLabel, List<Label> sonList){
        if(fatherLabel != null){
            if(sonList == null || sonList.size() == 0){
                return fatherLabel.getLabelNo() + "001";
            }
            Label lastLabel = sonList.get(sonList.size() - 1 );
            String lastLabelNo = lastLabel.getLabelNo();
            Integer number = Integer.valueOf(lastLabelNo.substring(lastLabelNo.length()-3));
            number ++;
            String numberStr = addZero(number,3);
            return fatherLabel.getLabelNo() + numberStr;
        }
        return null;
    }

    /**
     * 给一个不满n位的数字补零
     * @param number
     * @return
     */
    public static String addZero(Integer number, Integer count){
        String numberStr = String.valueOf(number);
        int len = numberStr.length();
        if(len < count){
            String tmp = "";
            for(int k = 0; k < (count - len); k ++){
                tmp += "0";
            }
            numberStr = tmp + numberStr;
        }
        return numberStr;
    }

    /**
     *
     * @param label
     * @return
     */
    public static String addOne(Label label, String fatherLabelNo){
        String labelNo = label.getLabelNo();
        Integer number = Integer.valueOf(labelNo.substring(labelNo.length()-3));
        number++;
        return fatherLabelNo + addZero(number, 3);
    }


    public static List<Label> flushLabelNo(Label label, List<Label> sonList) {
        String labelNo = label.getLabelNo();
        int i = 1;
        for(Label sonLabel : sonList){
            sonLabel.setFatherId(label.getId());
            sonLabel.setLabelLevel(label.getLabelLevel() + 1);
            sonLabel.setLabelNo(labelNo + addZero(i, 3));
            i++;
        }
        return sonList;
    }

    public static boolean checkTag(List<Label> songLabels, String[] labelIds){
        if(songLabels.size() != labelIds.length){
            return false;
        }
        for(Label label : songLabels){
            String labelId = String.valueOf(label.getId());
            if(labelIds.length == songLabels.size()){
                Arrays.sort(labelIds);
                int result = Arrays.binarySearch(labelIds, labelId);
                if(result < 0){
                   return false;
                }
            }
        }
        return true;
    }
}
