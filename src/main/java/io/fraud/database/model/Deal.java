package io.fraud.database.model;

import com.qaguild.dbeaver.annotations.TableName;

@TableName("public.\"deals\"")
public class Deal {

    //@id
    int id;
    Double amount;
    String currency;
    Double rate;

    //@Column("base_currency")
    String baseCurrency;
    String source;
    String target;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Double getAmount() { return amount; }

    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    public Double getRate() { return rate; }

    public void setRate(Double rate) { this.rate = rate; }

    public String getBaseCurrency() { return baseCurrency; }

    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    public String getTarget() { return target; }

    public void setTarget(String target) { this.target = target; }
}
