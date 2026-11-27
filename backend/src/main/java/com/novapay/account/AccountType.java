package com.novapay.account;

public enum AccountType{
    CHECKING(true) , SAVINGS(true) , CREDIT(false) ;

    private final boolean asset;
    AccountType(boolean asset){this.asset = asset;}

    public boolean isAsset(){return asset;}
}
