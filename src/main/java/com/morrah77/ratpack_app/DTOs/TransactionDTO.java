package com.morrah77.ratpack_app.DTOs;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class TransactionDTO {
    public Date date;
    public String description;
    public Double amount;
    public String currency;

    public TransactionDTO(){}
}
