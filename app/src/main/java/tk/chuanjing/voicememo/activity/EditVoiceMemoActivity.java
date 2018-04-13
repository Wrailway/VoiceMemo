package tk.chuanjing.voicememo.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tk.chuanjing.voicememo.Constant;
import tk.chuanjing.voicememo.R;
import tk.chuanjing.voicememo.adapter.MemoNewAdapter;
import tk.chuanjing.voicememo.bean.VoiceMemoBean;
import tk.chuanjing.voicememo.dao.MyVoiceMemoBeanDao;
import tk.chuanjing.voicememo.utils.DateAndTimeUtils;
import tk.chuanjing.voicememo.utils.ThisProjectUtils;
import tk.chuanjing.voicememo.utils.ToastUtils;

/**
 * 编辑页面的Activity：
 *      在此Activity中可以对已经存在的备忘进行修改 或者 删除
 */
public class EditVoiceMemoActivity extends BaseActivity {

    private ImageButton imgbtn_right;
    private ImageButton imgbtn_left;
    private Button btn_pauseVoice;
    private Button btn_playVoice;
    private Button btn_stopVoice;
    private EditText et_memo;
    private VoiceMemoBean voiceMemoBean;
    private MediaPlayer mp = new MediaPlayer();

    private RecyclerView rcv_pic;
    private MemoNewAdapter memoNewAdapter;
    private List<String> imgOrVideoPathList = new ArrayList<>();

    /** 图片、视频、音频选择结果 的集合 */
    private List<LocalMedia> selectList;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_edit_voice_memo;
    }

    @Override
    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("编辑");
        imgbtn_left = (ImageButton) findViewById(R.id.imgbtn_left);
        imgbtn_left.setBackgroundResource(R.mipmap.delete);//@android:drawable/ic_menu_send
        imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
        imgbtn_right.setBackgroundResource(R.mipmap.save);
        btn_pauseVoice = (Button) findViewById(R.id.btn_pauseVoice);
        btn_playVoice = (Button) findViewById(R.id.btn_playVoice);
        btn_stopVoice = (Button) findViewById(R.id.btn_stopVoice);
        et_memo = (EditText) findViewById(R.id.et_memo);
        rcv_pic = (RecyclerView) findViewById(R.id.rcv_pic);

        // 处理RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcv_pic.setLayoutManager(layoutManager);
        memoNewAdapter = new MemoNewAdapter(imgOrVideoPathList);
        rcv_pic.setAdapter(memoNewAdapter);
    }

    @Override
    public void initListener() {
        imgbtn_left.setOnClickListener(this);
        imgbtn_right.setOnClickListener(this);
        btn_pauseVoice.setOnClickListener(this);
        btn_playVoice.setOnClickListener(this);
        btn_stopVoice.setOnClickListener(this);

        // RecyclerView的条目点击事件
        memoNewAdapter.setOnItemClickListener(new MemoNewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                // 打开图片或者视频
                String path = imgOrVideoPathList.get(postion);
                if(ThisProjectUtils.isVideo(path)) {
                    // 是视频
                    PictureSelector.create(EditVoiceMemoActivity.this)
                            .externalPictureVideo(path);
                }else {
                    // 是图片，组装selectList
                    selectList = new ArrayList<LocalMedia>();
                    LocalMedia localMedia;
                    for (int i = 0; i < imgOrVideoPathList.size(); i++) {
                        localMedia = new LocalMedia();
                        localMedia.setPath(imgOrVideoPathList.get(i));
                        localMedia.setChecked(true);
                        localMedia.setPosition(i);
                        //localMedia.setMimeType();
                        selectList.add(localMedia);
                    }

                    PictureSelector.create(EditVoiceMemoActivity.this)
                            .themeStyle(R.style.PictureSelectorStyle)
                            .openExternalPreview(postion, Constant.IMAGE_PATH, selectList);
                }
            }
        });
    }

    @Override
    public void initData() {
        // 获取意图对象
        Intent intent = getIntent();
        // 获取传递的ID
        Long id = intent.getLongExtra("id", 1);//如果没有传过来id，默认查询id为1的
        voiceMemoBean = MyVoiceMemoBeanDao.findById(id);

        // 更新EditText
        et_memo.setText(voiceMemoBean.getMemoText());
        // 更新RecyclerView
        String imgOrVideoPath = voiceMemoBean.getImgOrVideoPath();
        imgOrVideoPathList = ThisProjectUtils.getImgOrVideoPathForStr(imgOrVideoPath);
        if(imgOrVideoPathList != null && imgOrVideoPathList.size() > 0) {
            memoNewAdapter.setDate(imgOrVideoPathList);
            memoNewAdapter.notifyDataSetChanged();
        }

        initMediaPlayer();
    }

    @Override
    public void onInnerClick(View v) {
        super.onInnerClick(v);
        switch (v.getId()) {
            case R.id.imgbtn_left://删除
                delete();
                break;
            case R.id.imgbtn_right://保存修改
                update();
                break;
            case R.id.btn_pauseVoice:
                if (mp.isPlaying()) {
                    mp.pause();//暂停播放
                }
                break;
            case R.id.btn_playVoice:
                if (!mp.isPlaying()) {
                    mp.start();//播放音频
                }
                break;
            case R.id.btn_stopVoice:
                mp.reset();//将mp重置为创建状态
                initMediaPlayer();//初始化mp，这样保证按下stop按钮后再按play按钮可以播放
                break;
        }
    }

    /** PictureSelector的回调 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:// 图片、视频、音频选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    if(selectList  == null || selectList.size() < 1){
                        return;
                    }
                    // 取出选择图片界面返回的图片地址，放到Adapter的数据源List集合中
                    imgOrVideoPathList.clear();//先清除之前数据源里面已经有的数据
                    for (int i = 0; i < selectList.size(); i++) {
                        imgOrVideoPathList.add(selectList.get(i).getPath());
                    }

                    // 更新数据，刷新RecyclerView
                    memoNewAdapter.setDate(imgOrVideoPathList);
                    memoNewAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void delete() {
        // 从数据库删除
        MyVoiceMemoBeanDao.deleteVoiceMemo(voiceMemoBean.getId());
        // 删除文件
        File voiceFile = new File(voiceMemoBean.getVoicePath());
        if (voiceFile.exists()) {
            voiceFile.delete();
        }
        ToastUtils.showToast(this, "删除成功！");
        finish();//删除后，关闭这个页面，自动回到列表页面
    }

    private void update() {
        voiceMemoBean.setMemoText(et_memo.getText().toString());
        voiceMemoBean.setUpdataTime(DateAndTimeUtils.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        MyVoiceMemoBeanDao.updateVoiceMemo(voiceMemoBean);  //保存到数据库
        finish();//保存成功后关闭此activity
    }

    /**
     * 初始化MediaPlayer
     */
    private void initMediaPlayer() {
        /*
        MediaPlayer mp = new MediaPlayer();//新建一个的实例
        mp.setDataSource();//设置要播放文件的路径
        mp.prepare();//播放 准备完成，开始播放前要调用
        mp.start();//播放
        mp.pause();//暂停
        mp.reset();//将mp对象重置到刚创建的状态
        mp.stop();//停止播放，使用后当前mp对象无法再播放
        mp.release();//释放播放相关资源，一般在活动的onDestroy()方法里调用
        mp.isPlaying();//判断mp对象是否正在播放
        mp.seekTo();//调转到指定位置播放
        mp.getDuration();//获得载入的音频的播放时长
        */
        File audioFile = new File(voiceMemoBean.getVoicePath());
        try {
            mp.setDataSource(audioFile.getPath());//设置播放音频文件的路径
            mp.prepare();//播放准备完成，开始播放前要调用
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            //释放mp
            mp.stop();
            mp.release();
        }
    }
}
