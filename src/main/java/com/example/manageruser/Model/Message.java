package com.example.manageruser.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MsgType type;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String sender;

	@Column(nullable = false)
	private Date timestamp;

	@ManyToOne
	@JoinColumn(name = "chat_id", nullable = false)
	private Chat chat;
}