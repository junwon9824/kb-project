package com.example.demo.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Log extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_banknumber")
    private String recipientBankNumber;

    private String category;

    @Column(name = "sender_banknumber")
    private String senderBankNumber;

    @Column(name = "sender_name")
    private String senderName;

    private Long amount; // 또는 BigDecimal amount;
}
