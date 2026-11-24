package com.novapay.account;

public enum AccountType{
    CHECKINK(true) , SAVING(true) , CREDIT(false) ;

    private final boolean asset;
    AccountType(boolean asset){this.asset = asset;}

    public boolean isAsset(){return asset;}
}
