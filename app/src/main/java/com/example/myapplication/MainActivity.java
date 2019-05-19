package com.example.myapplication;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    public static final String AUTHORITY_BIll = "com.example.myapplication.SQ_BILL";
    public static final String AUTHORITY_PAYMENT = "com.example.myapplication.SQ_PAYMENT";
    public static final Uri CONTENT_URI_BILL = Uri.parse("content://" + AUTHORITY_BIll);
    public static final Uri CONTENT_URI_PAYMENT = Uri.parse("content://" + AUTHORITY_PAYMENT);

    private static final String[] PROJECTION_BILL = new String[]{"bill_no", "bill_name", "bill_amount"};

    private static final int PAGESIZE = 10;
    private int mAccountBalance = 10000000;

    private ArrayList<Bill> mListTotal = new ArrayList<>();
    private ArrayList<Bill> mListLoaded = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private LinearLayout mLoadMore;
    private View mCircleView;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private TextView mPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        getBillsFromDatabase();
    }


    private void deleteBillFromDatabase(Bill bill) {
        getContentResolver().delete(CONTENT_URI_BILL, "Bill", new String[]{"bill_no", bill.getNo()});
    }

    private void getBillsFromDatabase() {
        new Runnable() {
            @Override
            public void run() {
                Cursor c = getContentResolver().query(CONTENT_URI_BILL, PROJECTION_BILL, null, null, null);

                c.moveToFirst();
                mListTotal.clear();
                while (!c.isAfterLast()) {
                    Bill bill = new Bill();
                    bill.setNo(c.getString(0));
                    bill.setName(c.getString(1));
                    bill.setAmount(c.getString(2));
                    mListTotal.add(bill);
                    c.moveToNext();
                }
                c.close();

                addPageData(0, 10);
            }
        }.run();
    }

    private void insertPaymentToDatabase(Payment payment) {
        ContentValues values = new ContentValues();
        values.put("pay_no", payment.getNo());
        values.put("pay_name", payment.getName());
        values.put("pay_amount", payment.getAmount());
        getContentResolver().insert(CONTENT_URI_PAYMENT, values);
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

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mLoadMore = findViewById(R.id.load_more_view);
        mCircleView = findViewById(R.id.circle_view);
        findViewById(R.id.pay_his).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });
        mPageTitle = findViewById(R.id.page_title);
        findViewById(R.id.submit_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        updatePageTitle(mAccountBalance);
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
                                            Toast.makeText(MainActivity.this, getString(R.string.load_more_no_more), Toast.LENGTH_SHORT).show();
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

        mMyAdapter = new MyAdapter(mListLoaded, new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Bill bill = (Bill) buttonView.getTag();
                bill.setChecked(isChecked);
            }
        });
        recyclerView.setAdapter(mMyAdapter);
    }

    private void updatePageTitle(int balance) {
        mPageTitle.setText("Bill List (Balance S$ " + balance + ")");
    }

    private void submit() {
        ArrayList<Payment> pendingPayments = new ArrayList<>();
        ArrayList<Bill> pendingBills = new ArrayList<>();
        for (int i = 0; i < mListLoaded.size() - 1; i++) {
            Bill bill = mListLoaded.get(i);
            boolean checked = bill.isChecked();
            if (checked) {
                Payment payment = new Payment();
                payment.setNo(String.valueOf(bill.getNo()));
                payment.setName(bill.getName());
                payment.setAmount(bill.getAmount());
                pendingPayments.add(payment);
                pendingBills.add(bill);
            }
        }
        Transaction transaction = new Transaction();
        transaction.setBills(pendingBills);
        transaction.setPayments(pendingPayments);


        boolean b = checkValidation(transaction.getTotalAmount());
        // call api to finish the transaction, here assume that api call success, then need to update local database and ui;
        if (b) {
            savePaymentToDatabase(transaction);
            updateBalance(transaction.getTotalAmount());
            updateBillDataBase(transaction);
            updateBillListUI(transaction);
        }
    }

    private void updateBillListUI(Transaction transaction) {
        getBillsFromDatabase();
        mMyAdapter.notifyDataSetChanged();
    }

    private void updateBillDataBase(Transaction transaction) {
        ArrayList<Bill> bills = transaction.getBills();
        for(int i=0; i<bills.size(); i++){
            deleteBillFromDatabase(bills.get(i));
        }
    }

    private void updateBalance(int totalAmount) {
        mAccountBalance -= totalAmount;
        updatePageTitle(mAccountBalance);
    }

    private void savePaymentToDatabase(Transaction transaction) {
        ArrayList<Payment> payments = transaction.getPayments();
        for (int i = 0; i < payments.size(); i++) {
            insertPaymentToDatabase(payments.get(i));
        }
    }


    private boolean checkValidation(int totalAmount) {
        if (totalAmount > mAccountBalance) {
            Toast.makeText(this, "balance exceeded", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private ObjectAnimator setAnimator(View view) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotation", 500f);
        animation.setDuration(2000);
        return animation;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCircleView.clearAnimation();
        if(mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
        if(mTimerTask!=null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}
