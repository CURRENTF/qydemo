//package com.example.qydemo0;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.text.Html;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.animation.BounceInterpolator;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.example.androidsdemo.view.SlotMachine;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//import cn.forward.androids.utils.DateUtil;
//import cn.forward.androids.utils.LogUtil;
//import cn.forward.androids.views.BitmapScrollPicker;
//import cn.forward.androids.views.ScrollPickerView;
//import cn.forward.androids.views.StringScrollPicker;
//
//
//public class ScrollPickerViewDemo extends Activity {
//
//    // 初始月份
//    private static final int ORIGIN_YEAR = 2000;
//    private static final int ORIGIN_MONTH = 1;
//    private static final int ORIGIN_DAY = 1;
//
//    // 可选择的年份，从1900到现在
//    private static final String[] YEARS;
//
//    static {
//        ArrayList<String> list = new ArrayList<String>();
//        int curYear = DateUtil.getYear();
//        for (int i = 1900; i <= curYear; i++) {
//            list.add(i + "");
//        }
//        YEARS = list.toArray(new String[list.size()]);
//    }
//
//    // 月份
//    private static final String[] MONTHS = {"1", "2", "3", "4", "5", "6", "7",
//            "8", "9", "10", "11", "12"};
//
//    private StringScrollPicker mYearView;
//    private StringScrollPicker mMonthView;
//    private StringScrollPicker mDayView;
//
//    private ScrollPickerView mPicker01;
//    private BitmapScrollPicker mPicker02;
//    private BitmapScrollPicker mPickerHorizontal;
//    private BitmapScrollPicker mPickerHorizontal2;
//    private StringScrollPicker mPickerHorizontal3;
//
//    private Button mBtnPlay, mBtnPlay02;
//    boolean mIsPlaying = false;
//    private SlotMachine mSlotMachine;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scrollpickerview);
//
//        setTitle("ScrollPickerView");
//
//        mYearView = (StringScrollPicker) this.findViewById(R.id.view_year);
//        mMonthView = (StringScrollPicker) this.findViewById(R.id.view_month);
//        mDayView = (StringScrollPicker) this.findViewById(R.id.view_day);
//
//
//        mPicker01 = (ScrollPickerView) findViewById(R.id.picker_01);
//        mPicker02 = (BitmapScrollPicker) findViewById(R.id.picker_02);
//        mPickerHorizontal = (BitmapScrollPicker) findViewById(R.id.picker_03_horizontal);
//        mPickerHorizontal2 = (BitmapScrollPicker) findViewById(R.id.picker_04_horizontal);
//        mPickerHorizontal3 = (StringScrollPicker) findViewById(R.id.picker_05_horizontal);
//
//        mBtnPlay = (Button) findViewById(R.id.btn_play);
//        mBtnPlay02 = (Button) findViewById(R.id.btn_play02);
//
//        init();
//
//    }
//
//    private void init() {
//        // 设置数据
//        mYearView.setData(Arrays.asList(YEARS));
//        mMonthView.setData(Arrays.asList(MONTHS));
//        mDayView.setData(DateUtil.getMonthDaysArray(ORIGIN_YEAR, ORIGIN_MONTH));
//
//        // 设置初始值
//        mYearView.setSelectedPosition(mYearView.getData().indexOf(
//                "" + ORIGIN_YEAR));
//        mMonthView.setSelectedPosition(ORIGIN_MONTH - 1);
//        mDayView.setSelectedPosition(ORIGIN_DAY - 1);
//
//        // 更改年份
//        mYearView.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
//            @Override
//            public void onSelected(ScrollPickerView view, int position) {
//                LogUtil.i("hzw", "year " + mYearView.getSelectedItem());
//
//                int month = Integer.parseInt(mMonthView.getSelectedItem() + "");
//                // ２月份,更新天数
//                if (month == 2) {
//                    changeMonthDays();
//                }
//            }
//        });
//
//        // 更改月份
//        mMonthView.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
//            @Override
//            public void onSelected(ScrollPickerView view, int position) {
//
//                LogUtil.i("hzw", "month " + mMonthView.getSelectedItem());
//
//                changeMonthDays();
//
//
//            }
//        });
//
//        // 老虎机
//        mSlotMachine = (SlotMachine) findViewById(R.id.slotmachine);
//
//        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_01));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_02));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_03));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_04));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_05));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_06));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_07));
//
//        mSlotMachine.setData(bitmaps);
//        mSlotMachine.setSlotMachineListener(new SlotMachine.SlotMachineListener() {
//            @Override
//            public void onFinish(int pos01, int pos02, int pos03) {
//                mIsPlaying = false;
//                Toast.makeText(getApplicationContext(), pos01 + "," + pos02 + "," + pos03, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public boolean acceptWinResult(int position) {
//                return true;
//            }
//        });
//
//
//        mBtnPlay.setOnClickListener(new View.OnClickListener() {
//            Random mRandom = new Random();
//
//            @Override
//            public void onClick(View v) {
//                if (mIsPlaying) {
//                    return;
//                }
//                mIsPlaying = true;
//                // 开始滚动，模拟50％的中奖概率
//                if (mRandom.nextInt(2) == 0) { // 中奖
//                    mSlotMachine.play(mRandom.nextInt(bitmaps.size()));
//                } else { //
//                    mSlotMachine.play(-1);
//                }
//            }
//        });
//
//
//        mPicker02.setData(bitmaps);
//        mPickerHorizontal.setData(bitmaps);
//        mPickerHorizontal2.setData(bitmaps);
//        List<String> list = DateUtil.getMonthDaysArray(ORIGIN_YEAR, ORIGIN_MONTH);
//        List<CharSequence> newList = new ArrayList<>();
//        for (String s : list) {
//            s = "No." + "<br/>" + "<font color='#ff0000'>" + s + "</font>";
//            newList.add(Html.fromHtml(s));
//        }
//        mPickerHorizontal3.setData(newList);
//
//        mPickerHorizontal3.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
//            @Override
//            public void onSelected(ScrollPickerView scrollPickerView, int position) {
//                Toast.makeText(ScrollPickerViewDemo.this, "" + (position + 1), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        mBtnPlay02.setOnClickListener(new View.OnClickListener() {
//            Random mRandom = new Random();
//
//            @Override
//            public void onClick(View v) {
//                if (mPickerHorizontal.isAutoScrolling() || mPickerHorizontal3.isAutoScrolling()) {
//                    return;
//                }
//                mPickerHorizontal.autoScrollFast(mRandom.nextInt(mPickerHorizontal.getData().size()), 4000);
//                mPickerHorizontal3.autoScrollFast(mRandom.nextInt(mPickerHorizontal3.getData().size()), 5000, mPickerHorizontal.dip2px(0.6f),
//                        new BounceInterpolator() {
//
//                            // 回弹两次
//                            private float rebound(float fraction) {
//                                return fraction * fraction * 4;
//                            }
//
//                            @Override
//                            public float getInterpolation(float input) {
//                                if (input < 0.5)
//                                    return rebound(input);
//                                else if (input < 0.85)
//                                    return rebound(input - 0.675f) + 0.8875f;
//                                else
//                                    return rebound(input - 0.925f) + 0.9775f;
//                            }
//
//                        });
//            }
//        });
//    }
//
//    // 更新天数
//    private void changeMonthDays() {
//        int year = Integer.parseInt(mYearView.getSelectedItem() + "");
//        int month = Integer.parseInt(mMonthView.getSelectedItem() + "");
//        int day = Integer.parseInt(mDayView.getSelectedItem() + "");
//        List<CharSequence> dayList = new ArrayList<CharSequence>(DateUtil.getMonthDaysArray(year, month));
//
//        mDayView.setData(dayList);
//        mDayView.setSelectedPosition(day > dayList.size() ? dayList
//                .size() - 1 : day - 1);
//    }
//
//}