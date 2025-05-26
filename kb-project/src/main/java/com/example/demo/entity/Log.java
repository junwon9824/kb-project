
package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@jakarta.persistence.Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Log extends BaseEntity {

    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "sender_id")
    private User sender;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "recipient_id")
    private User recipient;

    private String category;

    private Long amount;
}
