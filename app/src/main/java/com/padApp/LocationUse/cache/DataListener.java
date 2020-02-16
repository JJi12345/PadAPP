package com.padApp.LocationUse.cache;


import com.padApp.LocationUse.message.AbstractData;

import java.util.List;

/*
 * 文件名：数据回复监听
 * 版权：Copyright 2002-2019 heweisoft Tech. Co. Ltd. All Rights Reserved.
 * 描述： ccom.heweisoft.app.dxcg.BdGpsCtrl
 * 修改时间：2019/11/26
 * 修改内容：新增
 * 创建者：hewei
 */
public interface DataListener {
    /**
     * 数据回复
     * @param data 数据
     */
    void onDataReceived(AbstractData data);
    public AbstractData pop();
    public List<AbstractData> popAll();

}
