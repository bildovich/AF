package com.example.impls;

import com.example.exceptions.AmountException;
import com.example.exceptions.CurrencyException;
import com.example.interfaces.Storage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bildovich on 22.10.2016.
 */

public class DefaultStorage implements Storage {

    private String name;

    // сразу инициализируем пустые коллекции, потому что хоть одна валюта будет
    private Map<Currency, BigDecimal> currencyAmounts = new HashMap<Currency, BigDecimal>();
    private List<Currency> currencyList = new ArrayList<>();

    public DefaultStorage(){}

    public DefaultStorage(String name) {
        this.name = name;
    }

    public DefaultStorage(List<Currency> currencyList, Map<Currency, BigDecimal> currencyAmounts, String name) {
        this.currencyList = currencyList;
        this.currencyAmounts = currencyAmounts;
        this.name = name;
    }

    public DefaultStorage(Map<Currency, BigDecimal> currencyAmounts) {
        this.currencyAmounts = currencyAmounts;
    }

    public DefaultStorage(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    public Map<Currency, BigDecimal> getCurrencyAmounts() {
        return currencyAmounts;
    }

    public void setCurrencyAmounts(Map<Currency, BigDecimal> currencyAmounts) {
        this.currencyAmounts = currencyAmounts;
    }




    @Override
    public BigDecimal getAmount(Currency currency) throws CurrencyException {
        checkCurrencyExist(currency); // в Spring через AOP легче внедрять повторяющиеся участки кода
        return currencyAmounts.get(currency);
    }


    // ручное обновление баланса
    @Override
    public void changeAmount(BigDecimal amount, Currency currency) throws CurrencyException {
        checkCurrencyExist(currency);
        currencyAmounts.put(currency, amount);
    }


    // добавление денег в хранилище
    @Override
    public void addAmount(BigDecimal amount, Currency currency) throws CurrencyException {
        checkCurrencyExist(currency);
        BigDecimal oldAmount = currencyAmounts.get(currency);
        currencyAmounts.put(currency, oldAmount.add(amount));
    }


    // проверка, есть ли такая валюта в данном хранилище
    private void checkCurrencyExist(Currency currency) throws CurrencyException {
        if (!currencyAmounts.containsKey(currency)){
            throw new CurrencyException("Currency "+currency+" not exist");
        }
    }


    // отнимаем деньги из хранилища
    @Override
    public void expenseAmount(BigDecimal amount, Currency currency) throws CurrencyException, AmountException {
        checkCurrencyExist(currency);

        BigDecimal oldAmount = currencyAmounts.get(currency);
        BigDecimal newValue = oldAmount.subtract(amount);
        checkAmount(newValue);// не даем балансу уйти в минус
        currencyAmounts.put(currency, newValue);
    }



    // сумма не должна быть меньше нуля (в реальности такое невозможно, мы не можем потратить больше того, что есть)
    private void checkAmount(BigDecimal amount) throws AmountException {

        if (amount.compareTo(BigDecimal.ZERO)<0){
            throw new AmountException("Amount can't be < 0");
        }

    }


    @Override
    public void addCurrency(Currency currency) throws CurrencyException {

        if (currencyAmounts.containsKey(currency)){
            throw new CurrencyException("Currency already exist");// пока просто сообщение на англ, без локализации
        }

        currencyList.add(currency);
        currencyAmounts.put(currency, BigDecimal.ZERO);

    }

    @Override
    public void deleteCurrency(Currency currency)  throws CurrencyException {

        checkCurrencyExist(currency);

        // не даем удалять валюту, если в хранилище есть деньги по этой валюте
        if (!currencyAmounts.get(currency).equals(BigDecimal.ZERO)){
            throw new CurrencyException("Can't delete currency with amount");
        }

        currencyAmounts.remove(currency);
        currencyList.remove(currency);

    }


    @Override
    public List<Currency> getAvailableCurrencies() {
        return currencyList;
    }


    @Override
    public BigDecimal getApproxAmount(Currency currency) {
        // TODO реализовать расчет остатка с приведением в одну валюту
        // реализуем позже
        throw new UnsupportedOperationException("Not implemented");

    }

    @Override
    public Currency getCurrency(String code) throws CurrencyException {
        // количество валют для каждого хранилища будет небольшим - поэтому можно провоить поиск через цикл
        // можно использовать библиотеку Apache Commons Collections

        for (Currency currency : currencyList) {
            if (currency.getCurrencyCode().equals(code)){
                return currency;
            }
        }

        throw new CurrencyException("Currency (code="+code+") not exist in storage");

    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
