package tk.chuanjing.voicememo.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import tk.chuanjing.voicememo.R;
import tk.chuanjing.voicememo.bean.VoiceMemoBean;
import tk.chuanjing.voicememo.dao.MyVoiceMemoBeanDao;
import tk.chuanjing.voicememo.utils.DateAndTimeUtils;
import tk.chuanjing.voicememo.utils.ToastUtils;

public class EditVoiceMemoActivity extends BaseActivity {

    private ImageButton imgbtn_right;
    private Button btn_pauseVoice;
    private Button btn_playVoice;
    private Button btn_stopVoice;
    private Button btn_save;
    private EditText et_memo;
    private VoiceMemoBean voiceMemoBean;
    private MediaPlayer mp = new MediaPlayer();

    @Override
    public int getLayoutResID() {
        return R.layout.activity_edit_voice_memo;
    }

    @Override
    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("编辑");
        imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
        imgbtn_right.setBackgroundResource(R.mipmap.delete);//@android:drawable/ic_menu_send
        btn_pauseVoice = (Button) findViewById(R.id.btn_pauseVoice);
        btn_playVoice = (Button) findViewById(R.id.btn_playVoice);
        btn_stopVoice = (Button) findViewById(R.id.btn_stopVoice);
        btn_save = (Button) findViewById(R.id.btn_save);
        et_memo = (EditText) findViewById(R.id.et_memo);
    }

    @Override
    public void initListener() {
        btn_pauseVoice.setOnClickListener(this);
        btn_playVoice.setOnClickListener(this);
        btn_stopVoice.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        imgbtn_right.setOnClickListener(this);
    }

    @Override
    public void initData() {
        // 获取意图对象
        Intent intent = getIntent();
        // 获取传递的ID
        Long id = intent.getLongExtra("id", 1);//如果没有传过来id，默认查询id为1的
        voiceMemoBean = MyVoiceMemoBeanDao.findById(id);

        // 更新UI
        et_memo.setText(voiceMemoBean.getMemoText());

        initMediaPlayer();
    }

    @Override
    public void onInnerClick(View v) {
        super.onInnerClick(v);
        switch (v.getId()) {
            case R.id.imgbtn_right://删除
                delete();
                break;
            case R.id.btn_save://保存修改
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

    /** 调用播放音频的Intent去播放音频
    private void playVoice() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File audioFile = new File(voiceMemoBean.getVoicePath());

            // 根据不同版本，获取该文件的Uri
            Uri audioUri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // 判断是否是7.0，适配android7.0 ，不能直接访问原路径，需要对intent 授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                audioUri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileProvider", audioFile);
            } else {
                audioUri = Uri.fromFile(audioFile);
            }
            intent.setDataAndType(audioUri, "audio/*");
            startActivity(intent);
        }catch(Exception e){
            ToastUtils.showToast(this, "错误信息：" + e.getMessage());
        }
    }
    */

    /**
     * 初始化mp
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
