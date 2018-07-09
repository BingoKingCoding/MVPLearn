package com.bingo.king.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.bingo.king.R;
import com.bingo.king.app.base.BaseAdapter;
import com.bingo.king.app.utils.GlideUtils;
import com.bingo.king.mvp.model.entity.zhihu.CommentBean;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * <知乎评论列表>
 * Created by wwb on 2017/12/12 09:42.
 */

public class ZhiHuCommentAdapter extends BaseAdapter<CommentBean.CommentsBean,BaseViewHolder>
{

    private TextView tvCommentReply;
    private static final int STATE_NULL = 0;    //未知
    private static final int STATE_NONE = 1;    //无需展开
    private static final int STATE_EXPAND = 2;  //已展开
    private static final int STATE_SHRINK = 3;  //已收缩

    private static final int MAX_LINE = 2;  //起始最多显示2行
    private TextView tvCommentExpand;


    public ZhiHuCommentAdapter(@Nullable List<CommentBean.CommentsBean> data)
    {
        super(R.layout.item_zhihu_comment, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentBean.CommentsBean item)
    {
        GlideUtils.getInstance().loadImage(item.getAvatar(),helper.getView(R.id.civ_comment_face));
        helper.setText(R.id.tv_comment_name,item.getAuthor());
        helper.setText(R.id.tv_comment_content,item.getContent());
        helper.setText(R.id.tv_comment_time, TimeUtils.getFriendlyTimeSpanByNow(item.getTime()));
        helper.setText(R.id.tv_comment_like, String.valueOf(item.getLikes()));


        tvCommentReply = helper.getView(R.id.tv_comment_reply);
        tvCommentExpand = helper.getView(R.id.tv_comment_expand);
        if (item.getReply_to() != null && item.getReply_to().getId() != 0) {
            tvCommentReply.setVisibility(View.VISIBLE);
            SpannableString ss = new SpannableString("@" + item.getReply_to().getAuthor() + ": " + item.getReply_to().getContent());
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.comment_at)), 0,item.getReply_to().getAuthor().length() + 2 , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tvCommentReply.setText(ss);
            if (item.getReply_to().getExpandState() == STATE_NULL) {    //未知
                tvCommentReply.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tvCommentReply.getLineCount() > MAX_LINE) {
                            tvCommentReply.setMaxLines(MAX_LINE);
                            tvCommentExpand.setVisibility(View.VISIBLE);
                            tvCommentExpand.setText("展开");
                            mData.get(helper.getAdapterPosition()).getReply_to().setExpandState(STATE_SHRINK);
                            tvCommentExpand.setOnClickListener(new OnStateClickListener(helper.getAdapterPosition(), tvCommentReply));
                        } else {
                            tvCommentExpand.setVisibility(View.GONE);
                            mData.get(helper.getAdapterPosition()).getReply_to().setExpandState(STATE_NONE);
                        }
                    }
                });
            } else if(item.getReply_to().getExpandState() == STATE_NONE) {  //无需展开
                tvCommentExpand.setVisibility(View.GONE);
            } else if(item.getReply_to().getExpandState() == STATE_EXPAND) {    //已展开
                tvCommentReply.setMaxLines(Integer.MAX_VALUE);
                tvCommentExpand.setText("收起");
                tvCommentExpand.setVisibility(View.VISIBLE);
                tvCommentExpand.setOnClickListener(new OnStateClickListener(helper.getAdapterPosition(), tvCommentReply));
            } else {    //已收缩
                tvCommentReply.setMaxLines(MAX_LINE);
                tvCommentExpand.setText("展开");
                tvCommentExpand.setVisibility(View.VISIBLE);
                tvCommentExpand.setOnClickListener(new OnStateClickListener(helper.getAdapterPosition(),tvCommentReply));
            }
        } else {
            tvCommentReply.setVisibility(View.GONE);
            tvCommentExpand.setVisibility(View.GONE);
        }
    }

    private class OnStateClickListener implements View.OnClickListener {

        TextView replyView;
        int position;

        OnStateClickListener(int position,TextView replyView) {
            this.position = position;
            this.replyView = replyView;
        }

        @Override
        public void onClick(View view) {
            TextView tv = (TextView) view;
            if (mData.get(position).getReply_to().getExpandState() == STATE_SHRINK) {
                tv.setText("收起");
                replyView.setMaxLines(Integer.MAX_VALUE);
                replyView.setEllipsize(null);
                mData.get(position).getReply_to().setExpandState(STATE_EXPAND);
            } else {
                tv.setText("展开");
                replyView.setMaxLines(MAX_LINE);
                replyView.setEllipsize(TextUtils.TruncateAt.END);
                mData.get(position).getReply_to().setExpandState(STATE_SHRINK);
            }
        }
    }
}
