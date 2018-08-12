package com.zjj.cosco.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socks.library.KLog;
import com.zjj.cosco.R;
import com.zjj.cosco.adapter.PeopleAdapter;
import com.zjj.cosco.entity.People;
import com.zjj.cosco.helper.SoftKeyboardStateHelper;
import com.zjj.cosco.utils.CommonUtils;
import com.zjj.cosco.utils.WidgetController;
import com.zjj.cosco.view.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by administrator on 2018/8/9.
 */

public class RecyclerViewActivity extends Activity implements PeopleAdapter.ReplySubWdUserCallBack, View.OnTouchListener {
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.ll_circle)
    CoordinatorLayout ll_circle;
    @BindView(R.id.input_layout)
    LinearLayout bottomSheet;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.tv_niming)
    TextView tvNiming;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.rl_circle_title)
    LinearLayout title;
    private List<People> list;
    private PeopleAdapter adapter;
    private InputMethodManager imm;
    private int outPosition;
    private int innerPosition;
    private int mScreenHeight = 0;
    private int selectCircleItemH;
    private int selectCommentItemOffset;
    private LinearLayoutManager linearLayoutManager;
    private SoftKeyboardStateHelper softKeyboardStateHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        ButterKnife.bind(this);
        list = new ArrayList<>();
        List<String> replyList = new ArrayList<>();
        replyList.add("张三：这家伙尽瞎扯。");
        replyList.add("李四：6666666666。");
        replyList.add("王五：没有的事说的跟真的一样。");
        list.add(new People("zhangsan", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("lisi", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("wangwu", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("zhaoliu", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("xiaoqi", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("wangba", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("liujiu", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("zhaoshi", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("shuangshiyi", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("shuangshier", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("zengjinjin", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("chenyuanyuan", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("liangxuan", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        list.add(new People("zuoyou", "今天是个没好日子，我要吃好的喝好的，但我脾气不好", replyList, 10, "0"));
        adapter = new PeopleAdapter(this, list);
        adapter.setReplySubWdUser(this);
        linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, R.color.theme_content_bg))
                .size(getResources().getDimensionPixelSize(R.dimen.recycle_view_divider_size_10dp))
                .build());  //添加分割线
        rv.setAdapter(adapter);
        rv.setOnTouchListener(this);
        setSoftKeyboardListener();
    }

    private void setSoftKeyboardListener() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        softKeyboardStateHelper = new SoftKeyboardStateHelper(this, ll_circle);
        softKeyboardStateHelper.addSoftKeyboardChangedListener(listener);
    }

    private SoftKeyboardStateHelper.OnSoftKeyboardStateChangedListener listener = new SoftKeyboardStateHelper.OnSoftKeyboardStateChangedListener() {
        @Override
        public void OnSoftKeyboardStateChanged(boolean isKeyBoardShow, final int keyboardHeight) {
            if (isKeyBoardShow) {
                bottomSheet.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mScreenHeight <= 0) {
                            mScreenHeight = CommonUtils.getScreenHeight(RecyclerViewActivity.this);
                        }

                        int _navigationBarHeight = CommonUtils.getNavigationBarHeight(RecyclerViewActivity.this);
                        int _bottom_sheet_hight = WidgetController.getHeight(bottomSheet);
//                        int _bottom_sheet_hight = WidgetController.dip2px(getContext(),114);
                        int _offHeight = 0;
                        if (mScreenHeight >= 0) {
                            // 计算view要显示的高度坐标
                            _offHeight = mScreenHeight - (_navigationBarHeight + _bottom_sheet_hight + keyboardHeight);
                        }
                        int final_offHeight = _offHeight;
                        WidgetController.setLayoutY(bottomSheet, final_offHeight);
//                WidgetController.setLayout_Y(bottomSheet,final_offHeight);
                        bottomSheet.setVisibility(View.VISIBLE);
                        bottomSheet.bringToFront();
                        etComment.requestFocus();
                        etComment.setFocusable(true);
                        etComment.setFocusableInTouchMode(true);

                        //偏移listview
                        if (linearLayoutManager != null) {
                            linearLayoutManager.scrollToPositionWithOffset(outPosition,
                                    getListviewOffset(mScreenHeight, selectCircleItemH, keyboardHeight, _bottom_sheet_hight));
                        }
                    }
                });
            } else {
                bottomSheet.setVisibility(View.GONE);
            }
        }
    };

    /**
     * 测量偏移量
     *
     * @return
     */
    private int getListviewOffset(int screen_height, int selectCircleItemH, int currentKeyboardH, int editTextBodyHeight) {
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = 0;
        listviewOffset = screen_height - currentKeyboardH - editTextBodyHeight - title.getHeight() - selectCircleItemH;
        if (listviewOffset > 0){
            listviewOffset = 0;
        }else {
            listviewOffset =  listviewOffset + selectCommentItemOffset;
        }
        return listviewOffset;
    }

    @Override
    public void onReplySubWdUser(int outPosition, int innerPosition) {
        List<String> pinglunBean = list.get(outPosition).getReply();
        if (pinglunBean != null) {
            setInputLayoutView(outPosition, innerPosition, "");
            etComment.requestFocus();
            imm.showSoftInput(etComment, 0);
        }
    }

    private void setInputLayoutView(int outPosition, int innerPosition, String edittext_hint) {
        this.outPosition = outPosition;
        this.innerPosition = innerPosition;
        etComment.setHint("回复 (" + list.get(outPosition).getName()+")"+list.get(outPosition).getReply().get(innerPosition));
        measureCircleItemHighAndCommentItemOffset(!TextUtils.isEmpty(edittext_hint) ? "public" : "reply", outPosition, innerPosition);
    }

    /**
     * 计算评论条目高度和偏移量
     */
    private void measureCircleItemHighAndCommentItemOffset(String commentType, int outPosition, int innerPosition) {
        int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = linearLayoutManager.getChildAt(outPosition - firstPosition);
        if (selectCircleItem != null) {
            selectCircleItemH = selectCircleItem.getHeight();
        }
        if ("reply".equals(commentType)) {
            //回复评论的情况
            RecyclerView commentRv = selectCircleItem.findViewById(R.id.rv_replay);
            if (commentRv != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentRv.getChildAt(innerPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (softKeyboardStateHelper != null) {
            softKeyboardStateHelper.removeSoftKeyboardChangedListener(listener);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float y = 0;
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            y = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            float dy = event.getY() - y;
            if (Math.abs(dy) > 0){
                imm.hideSoftInputFromWindow(ll_circle.getWindowToken(), 0);
            }
        }
        return false;
    }
}