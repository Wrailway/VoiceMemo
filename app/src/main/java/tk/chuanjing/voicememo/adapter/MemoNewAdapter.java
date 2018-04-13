package tk.chuanjing.voicememo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import tk.chuanjing.voicememo.MyApplication;
import tk.chuanjing.voicememo.R;

/**
 * Created by ChuanJing on 2018/04/08.
 */
public class MemoNewAdapter extends RecyclerView.Adapter<MemoNewAdapter.ViewHolder> {

    private List<String> imgOrVideoPathList;

    public MemoNewAdapter(List<String> imgOrVideoPathList){
        this.imgOrVideoPathList = imgOrVideoPathList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_recyclerview_memo_new, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return imgOrVideoPathList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView iv_pic;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            iv_pic = (ImageView)view.findViewById(R.id.iv_pic);
        }

        public void setData(final int position) {
            final String imgPath = imgOrVideoPathList.get(position);
            File img = new File(imgPath);
            Glide.with(MyApplication.getInstance()).load(img).into(iv_pic);

            // 设置整个view条目点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), "点击了" + testBean.name, Toast.LENGTH_SHORT).show();
                    // 回调自己写的条目点击事件
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(view, position);
                    }
                }
            });

            /* 设置每个条目中图片的点击事件
            iv_ic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "点击了" + voiceMemoBean.name + "的图片", Toast.LENGTH_SHORT).show();
                }
            });
            */
        }
    }

    // 自定义RecyclerView条目点击事件
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }

    public void setDate(List<String> voiceMemoList) {
        this.imgOrVideoPathList = voiceMemoList;
    }
}
