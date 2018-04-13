package tk.chuanjing.voicememo.utils;

import java.util.Arrays;
import java.util.List;

import tk.chuanjing.voicememo.Constant;

/**
 * Created by ChuanJing on 2018/4/8.
 */

public class ThisProjectUtils {
    /**把  集合路径转换成  用分隔符分隔的字符串*/
    public static String getImgOrVideoPathForList(List<String> imgOrVideoPathList) {
        if(imgOrVideoPathList  == null || imgOrVideoPathList.size() < 1){
            return null;
        } else {
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < imgOrVideoPathList.size(); i++) {
                sb.append(imgOrVideoPathList.get(i) + Constant.IMG_OR_VIDEO_PATH_SEPARATE);
            }

            // 去掉最后一个多余的分隔符
            String imgOrVideoPathStr = sb.substring(0, sb.length()-3);
            return imgOrVideoPathStr;
        }
    }

    /**把  用分隔符分隔的字符串路径转换  成  集合*/
    public static List<String> getImgOrVideoPathForStr(String imgOrVideoPathStr) {
        if(imgOrVideoPathStr  == null || imgOrVideoPathStr.length() < 1){
            return null;
        } else {
            String[] split = imgOrVideoPathStr.split(Constant.IMG_OR_VIDEO_PATH_SEPARATE);
            List<String> imgOrVideoPathList = Arrays.asList(split);
            return imgOrVideoPathList;
        }
    }

    /** 给一个文件路径，判断是不是视频 */
    public static boolean isVideo(String path) {
        String[] videoSuffixNameStr = {".avi", ".rmvb", ".rm", ".asf", ".divx", ".mpg", ".mpeg ", ".mpe", ".wmv", ".mp4", ".mkv", ".vob"};
        List<String> videoSuffixNameList = Arrays.asList(videoSuffixNameStr);

        String SuffixName = path.substring(path.lastIndexOf("."), path.length());
        if(videoSuffixNameList.contains(SuffixName)) {
            return true;
        }
        return false;
    }

//    public static void main(String[] agrs) {
//        System.out.println("是不是视频：：：" + isVideo("jkjk.mp4"));
//    }
}
