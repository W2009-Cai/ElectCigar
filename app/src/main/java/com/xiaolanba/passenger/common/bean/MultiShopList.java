package com.xiaolanba.passenger.common.bean;

import java.util.List;

/**
 * Created by admin on 2018/5/11.
 */

public class MultiShopList {
    public List<SaleItem> sales;
    public List<LeaseItem> lease;
    public List<ShowItem> show;

    public int getTotalSize(){
        return (sales == null?0:sales.size())+ (lease==null?0:lease.size())+(show==null?0:show.size());
    }
}
