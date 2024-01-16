package com.ga.controller.network.ma;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ga.controller.R;
import com.ga.controller.network.model.MoreAppObj;
import com.ga.controller.query.FirebaseQuery;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MAScreen extends Activity {

    public static final int INSTALL_REQUEST_CODE = 1111;

    private ImageView imgBack;
    private LinearLayout lnNoFile;
    private RecyclerView rvMoreApp;
    private MoreAppAdapter moreAppAdapter;
    private ArrayList<MoreAppObj> mListDocument;

    private Disposable mDisposable;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ma);

        initViews();
    }

    private void initViews() {
        rvMoreApp = findViewById(R.id.rv_list_tab_more_app);
        lnNoFile = findViewById(R.id.ln_no_file);
        imgBack = findViewById(R.id.img_back);

        pm = getPackageManager();

        Observable.create(new ObservableOnSubscribe<ArrayList<MoreAppObj>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<MoreAppObj>> emitter) {
                mListDocument = new ArrayList<>();
                mListDocument.clear();
                mListDocument = FirebaseQuery.getConfigController().listMoreApp;
                emitter.onNext(mListDocument);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<MoreAppObj>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<MoreAppObj> moreAppModels) {
                        if (mListDocument != null && mListDocument.size() > 0) {
                            lnNoFile.setVisibility(View.GONE);
                        } else {
                            lnNoFile.setVisibility(View.VISIBLE);
                        }

                        moreAppAdapter = new MoreAppAdapter(mListDocument, MAScreen.this);
                        rvMoreApp.setLayoutManager(new LinearLayoutManager(MAScreen.this));
                        moreAppAdapter.setOnClickItem(new MoreAppAdapter.onClickItem() {
                            @Override
                            public void onClickItem(int position) {
                                try {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    intent.setData(Uri.parse("market://details?id=" + mListDocument.get(position).getPackageName()));
                                    startActivityForResult(intent, INSTALL_REQUEST_CODE);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        rvMoreApp.setAdapter(moreAppAdapter);
                        moreAppAdapter.notifyDataSetChanged();
                        mDisposable.dispose();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        lnNoFile.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INSTALL_REQUEST_CODE) {
            /*if (moreAppAdapter.isPackageInstalled(mListDocument.get(getPosition()).getPackageName(), pm)) {
                recreate();
            }*/
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
