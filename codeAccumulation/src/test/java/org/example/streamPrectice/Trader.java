package org.example.streamPrectice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author chenheng
 * @date 2021/11/13 19:51
 * 交易员类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trader {
    private String name;
    private String city;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trader trader = (Trader) o;
        return Objects.equals(name, trader.name) &&
                Objects.equals(city, trader.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, city);
    }
}

