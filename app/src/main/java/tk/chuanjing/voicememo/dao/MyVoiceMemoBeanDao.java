package tk.chuanjing.voicememo.dao;

import java.util.List;

import tk.chuanjing.voicememo.MyApplication;
import tk.chuanjing.voicememo.bean.VoiceMemoBean;
import tk.chuanjing.voicememo.bean.VoiceMemoBeanDao;

/**
 * Created by ChuanJing on 2018/3/29.
 */

public class MyVoiceMemoBeanDao {

    private static VoiceMemoBeanDao dao = MyApplication.getDaoInstant().getVoiceMemoBeanDao();

    /**
     * 添加数据，如果有重复则覆盖
     */
    public static void insertVoiceMemo(VoiceMemoBean voiceMemo) {
        //System.out.println("voiceMemo看看：" + voiceMemo);
        dao.insertOrReplace(voiceMemo);
    }

    /**
     * 删除数据
     */
    public static void deleteVoiceMemo(long id) {
        dao.deleteByKey(id);
    }

    /**
     * 更新数据
     */
    public static void updateVoiceMemo(VoiceMemoBean voiceMemo) {
        dao.update(voiceMemo);
    }

    /**
     * 查询全部数据
     */
    public static List<VoiceMemoBean> findAll() {
        return dao.loadAll();
    }

    /**
     * 根据ID查询
     * @param id
     */
    public static VoiceMemoBean findById(Long id) {
        // return dao.queryBuilder().where(VoiceMemoBeanDao.Properties.Id.eq(id)).list();
        return dao.queryBuilder().where(VoiceMemoBeanDao.Properties.Id.eq(id)).unique();
    }
}
