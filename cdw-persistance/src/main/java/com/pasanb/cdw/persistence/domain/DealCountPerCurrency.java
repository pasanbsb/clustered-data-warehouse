package com.pasanb.cdw.persistence.domain;

import org.hibernate.annotations.NamedQuery;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Pasan on 6/26/2016.
 */
@Entity
@Table(name = "deal_count_per_currency")
public class DealCountPerCurrency {
    @Id
    private String currency;
    private Integer count;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
