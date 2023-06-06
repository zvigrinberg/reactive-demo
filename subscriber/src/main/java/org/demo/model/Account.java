package org.demo.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import lombok.ToString;

import java.time.LocalDateTime;

@MongoEntity
@ToString
public class Account extends ReactivePanacheMongoEntity {

    public String accountHolderName;
    public String accountNumber;
    public String accountType;
    public double balance;
    public String currency;
    public String bankName;
    public boolean isActive;
    public String status = "INACTIVE";
    public LocalDateTime createdAt;
}
