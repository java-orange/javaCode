package org.example.streamPrectice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenheng
 * @date 2021/11/13 19:35
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LambdaPractice {
    List<Employee> employees = Arrays.asList(
            new Employee("张三", 18, 9999.99),
            new Employee("李四", 38, 5555.55),
            new Employee("王五", 50, 6666.66),
            new Employee("赵六", 16, 3333.33),
            new Employee("田七", 8, 7777.77)
    );
    /**
     * 给定一个数字列表，如何返回一个由每个数的平方构成的列表呢？
     * 例如，给定【1，2，3，4，5】，应该返回【1，4，9，16，25】
     */
    @Test
    void test01(){
        Integer[] arrays = {1,2,3,4,5};
        List<Integer> list = Arrays.asList(arrays);
        list.stream().map(x -> x*x).forEach(System.out::println);
    }
    /**
     * 怎样用map和reduce方法数一数流中有多少个Employee呢？
     */
    @Test
    void test02(){
        Optional<Integer> sumOptional = employees.stream()
                .map(e -> 1).reduce(Integer::sum);
        System.out.println(sumOptional.get());
        System.out.println("***************");
        System.out.println(employees.size());
    }
    @Test
    void test0201(){
        Optional<Integer> ageSumOptional = employees.stream()
                .map(e -> e.getAge())
                .reduce(Integer::sum);
        System.out.println(ageSumOptional.get());
    }
    List<Transaction> transactions = null;
    @BeforeAll
    public void before(){
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");
        transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2011, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );
    }
    //1、找出2011年发生的所有交易，并按交易额排序（从低到高）
    @Test
    void test0301(){

        List<Transaction> list = transactions.stream()
                .filter(t -> Objects.equals(t.getYear(), 2011))
                .sorted(Comparator.comparing(Transaction::getValue))
                .collect(Collectors.toList());
        list.forEach(System.out::println);
    }
    //2、交易员都在哪些不同的城市工作过？
    @Test
    void test0302(){

        List<Trader> list = transactions.stream()
                .map(Transaction::getTrader)
                .collect(Collectors.toList());
        List<String> cityList = list.stream()
                .map(Trader::getCity)
                .distinct()
                .collect(Collectors.toList());
        cityList.forEach(System.out::println);
    }
    //3、查找所有来自剑桥的交易员，并按姓名排序
    @Test
    void test0303(){


        List<Trader> traders = transactions.stream()
                .map(Transaction::getTrader)
                .distinct()
                .filter(trader -> Objects.equals(trader.getCity(), "Cambridge"))
                .sorted(Comparator.comparing(Trader::getName))
                .collect(Collectors.toList());
        traders.forEach(System.out::println);
    }
    //4、返回所有交易员的姓名字符串，按字母顺序排序
    @Test
    void test0304(){

        List<String> nameList = transactions.stream()
                .map(Transaction::getTrader)
                .distinct()
                .map(Trader::getName)
                .sorted()
                .collect(Collectors.toList());
        nameList.forEach(System.out::println);
    }
    //5、有没有交易员是在米兰工作的？
    @Test
    void test0305(){
        List<Trader> milanList = transactions.stream()
                .map(Transaction::getTrader)
                .distinct()
                .filter(trader -> Objects.equals(trader.getCity(), "Milan"))
                .collect(Collectors.toList());
        milanList.forEach(System.out::println);
    }
    //6、打印生活在剑桥的交易员的所有交易额
    @Test
    void test0306(){
        List<Integer> list = transactions.stream()
                .filter(transaction -> Objects.equals(transaction.getTrader().getCity(), "Cambridge"))
                .map(Transaction::getValue)
                .collect(Collectors.toList());
        list.forEach(System.out::println);
    }
    //7、所有交易中，最高的交易额是多少
    @Test
    void test0307(){
        Integer maxValue = transactions.stream()
                .map(Transaction::getValue)
                .max(Integer::compareTo)
                .get();
        System.out.println("maxValue->" + maxValue);
        System.out.println("********************");
        Integer max = transactions.stream()
                .map(Transaction::getValue)
                .reduce(Integer::max)
                .get();
        System.out.println("max->" + max);
        System.out.println("*******************");
        IntSummaryStatistics iss = transactions.stream()
                .collect(Collectors.summarizingInt(Transaction::getValue));
        int issMax = iss.getMax();
        System.out.println("issMax->" + issMax);
        System.out.println("*****************");
    }
    //8、找到交易额最小的交易（指这个对象）
    @Test
    void test0308(){
        Transaction minTransaction = transactions.stream()
                .min(Comparator.comparing(Transaction::getValue))
                .get();
        System.out.println(minTransaction);
    }
}

