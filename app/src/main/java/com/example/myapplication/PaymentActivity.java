package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PaymentActivity extends Activity {

    public static final String AUTHORITY = "com.example.myapplication.SQ_PAYMENT";
    public static final Uri CONTENT_URI_PAYMENT = Uri.parse("content://" + AUTHORITY);
    private static final String[] PROJECTION_PAY = new String[]{"pay_no", "pay_name", "pay_amount"};

    private static final int PAGESIZE = 10;

    private MyPaymentAdapter mMyAdapter;
    private LinearLayout mLoadMore;
    private View mCircleView;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private ArrayList<Payment> mListTotal = new ArrayList<>();
    private ArrayList<Payment> mListLoaded = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initView();

        getBillsFromDatabase();
    }

    private void getBillsFromDatabase() {
        new Runnable() {
            @Override
            public void run() {
                Cursor c = getContentResolver().query(CONTENT_URI_PAYMENT, PROJECTION_PAY, null, null, null);

                c.moveToFirst();
                mListTotal.clear();
                while (!c.isAfterLast()) {
                    Payment payment = new Payment();
                    payment.setNo(c.getString(0));
                    payment.setName(c.getString(1));
                    payment.setAmount(c.getString(2));
                    mListTotal.add(payment);
                    c.moveToNext();
                }
                c.close();

                addPageData(0, 10);
            }
        }.run();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mLoadMore = findViewById(R.id.load_more_view);
        mCircleView = findViewById(R.id.circle_view);
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ObjectAnimator animator = setAnimator(mCircleView);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    final int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if (lastPosition == mListLoaded.size() - 1) {
                        mLoadMore.setVisibility(View.VISIBLE);
                        animator.start();
                        mTimerTask = new TimerTask() {

                            @Override
                            public void run() {
                                final boolean b = addPageData(lastPosition + 1, PAGESIZE);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!b) {
                                            Toast.makeText(PaymentActivity.this, getString(R.string.load_more_no_more), Toast.LENGTH_SHORT).show();
                                        }
                                        mMyAdapter.notifyItemRangeChanged(lastPosition + 1, PAGESIZE);
                                        mLoadMore.setVisibility(View.GONE);
                                    }
                                });
                            }
                        };

                        if (mTimer == null) {
                            mTimer = new Timer();
                        }
                        mTimer.schedule(mTimerTask, 2000);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mMyAdapter = new MyPaymentAdapter(mListLoaded);
        recyclerView.setAdapter(mMyAdapter);
    }


    private ObjectAnimator setAnimator(View view) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotation", 500f);
        animation.setDuration(2000);
        return animation;
    }

    private boolean addPageData(int offSet, int pageSize) {
        if(offSet==0){
            mListLoaded.clear();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMyAdapter.notifyDataSetChanged();
                }
            });
        }

        if (mListTotal.size() > 0 && (offSet < mListTotal.size() - 1)) {
            int leftSize = mListTotal.size() - offSet;
            if (leftSize < pageSize) {
                for (int i = 0; i <= leftSize - 1; i++) {
                    mListLoaded.add(mListTotal.get(offSet + i));
                }
            } else {
                for (int i = 0; i <= pageSize - 1; i++) {
                    mListLoaded.add(mListTotal.get(offSet + i));
                }
            }
            return true;
        } else {
            return false;
        }

    }
}
