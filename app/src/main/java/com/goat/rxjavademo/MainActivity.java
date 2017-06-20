package com.goat.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.simple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simple();
            }
        });
    }
    //简单使用
    private void simple() {
        //创建 Observer(观察者)
        Observer<String>  observer=new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"Subscribe");
            }

            @Override
            public void onNext(String value) {
                Log.d(TAG,"value="+value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,"Error");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "Completed!");
            }
        };
    //创建 Observable(被观察者)
        Observable<String> observable=Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                            e.onNext("hellow");
                            e.onNext("world");
                            e.onComplete();
            }
        });
        observable.subscribe(observer);
    }

    //简化版
    public void simplify(View view) {
    Observable.just("hellow","world").subscribe(new Consumer<String>() {
    @Override
    public void accept(String s) throws Exception {
        Log.d(TAG, "s="+s);
    }
    });
    }

    // 线程控制
    public void scheduler(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d(TAG,"ThreadName="+Thread.currentThread().getName());
                e.onNext("hellow");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG,"ThreadName="+Thread.currentThread().getName());
            }
        });
    }
    // 变换map使用
    public void map(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("hello");
                e.onComplete();
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return s.hashCode();
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
            Log.d(TAG,"integer="+integer);
            }
        });
    }

    // 变换的原理：lift()
    public void lift(View view) {
        Observable.just("hello","word").lift(new ObservableOperator<Integer, String>() {
            @Override
            public Observer<? super String> apply(final Observer<? super Integer> observer) throws Exception {
                return new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        observer.onSubscribe(d);
                    }

                    @Override
                    public void onNext(String value) {
                        // 将事件序列中的 String 对象转换为 Integer 对象
                        observer.onNext(value.hashCode());
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                };
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG,"执行完成");
                Log.d(TAG,"integer="+integer);
            }
        });
    }


}
