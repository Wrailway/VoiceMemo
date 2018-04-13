package tk.chuanjing.voicememo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tk.chuanjing.voicememo.R;
import tk.chuanjing.voicememo.bean.VoiceMemoBean;

/**
 * Created by ChuanJing on 2018/03/28.
 */
public class MemoListAdapter extends RecyclerView.Adapter<MemoListAdapter.ViewHolder> {

    private List<VoiceMemoBean> voiceMemoList;

    public MemoListAdapter(List<VoiceMemoBean> voiceMemoList){
        this.voiceMemoList = voiceMemoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_recyclerview_memo_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return voiceMemoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView tv_memoText;
        TextView tv_newTime;
        TextView tv_updataTime;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            tv_memoText = (TextView)view.findViewById(R.id.tv_memoText);
            tv_newTime = (TextView)view.findViewById(R.id.tv_newTime);
            tv_updataTime = (TextView)view.findViewById(R.id.tv_updataTime);
        }

        public void setData(final int position) {
            final VoiceMemoBean voiceMemo = voiceMemoList.get(position);
            tv_memoText.setText(voiceMemo.getMemoText());
            tv_newTime.setText("创建：" + voiceMemo.getNewTime());
            tv_updataTime.setText("上次修改：" + voiceMemo.getUpdataTime());

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

    public void setDate(List<VoiceMemoBean> voiceMemoList) {
        this.voiceMemoList = voiceMemoList;
    }
}
