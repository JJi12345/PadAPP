package com.padApp.LocationUse.cache;

import com.padApp.LocationUse.message.AbstractData;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * 文件名：数据缓存
 * 版权：Copyright 2002-2018 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： com.heweisoft.app.dxcg.cache
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public class DataCache implements DataListener {

    private static final int MAX_NUM = 1000;//最多保存数据量
    private List<AbstractData> _cache = new LinkedList<>();

    private static DataCache _instance = null;

    private Lock _lock = new ReentrantLock();
    private Condition _empty = _lock.newCondition();
    private Condition _full = _lock.newCondition();

    private DataCache() {

    }

    public static synchronized DataListener getInstance() {
        if (null == _instance) {
            _instance = new DataCache();
        }
        return _instance;
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void push(AbstractData data) {
        _lock.lock();
        try {
            if (_cache.size() > MAX_NUM) {
                _empty.await();//数据满，等待空信号
            }
            _cache.add(data);
            _full.signal();//新添加数据，唤醒满等待
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            _lock.unlock();
        }
    }

    /**
     * pop一个数据
     *
     * @return
     */
    public AbstractData pop() {
        AbstractData data = null;
        _lock.lock();
        try {
            if (_cache.size() == 0) {
                _full.await();//数据空，等待满信号
            }
            data = _cache.remove(0);
            _empty.signal();//新添加数据，唤醒满等待
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            _lock.unlock();
        }
        return data;
    }

    /**
     * pop所有数据
     *
     * @return
     */
    public List<AbstractData> popAll() {
        List<AbstractData> data = null;
        _lock.lock();
        try {
            data = new LinkedList<>();
            data.addAll(_cache);
            _cache.clear();
            _empty.signal();//新添加数据，唤醒满等待
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            _lock.unlock();
        }
        return data;
    }
    @Override
    public void onDataReceived(AbstractData data) {
        push(data);
    }
}
