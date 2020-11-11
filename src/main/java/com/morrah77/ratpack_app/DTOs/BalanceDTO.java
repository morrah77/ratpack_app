package com.morrah77.ratpack_app.DTOs;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class BalanceDTO {
    public Double balance;
    public String currency;

    public BalanceDTO() {}

    public static BalanceDTO of(Object o) {
        try {
            BalanceDTO instance = new BalanceDTO();
            List<String> fieldNames = Arrays.stream(BalanceDTO.class.getDeclaredFields()).map(field -> field.getName()).collect(Collectors.toList());

            Arrays.stream(o.getClass().getDeclaredFields())
                    .filter(field -> fieldNames.contains(field.getName()))
                    .forEach(field -> {
                        try {
                            instance.getClass().getDeclaredField(field.getName()).set(instance, field.get(o));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
            return instance;
        } catch (Exception e) {
            return null;
        }
    }
}
