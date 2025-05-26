@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookMark {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String bookMarkName;
	private String bookMarkAccountNumber;
	private String bookMarkBankname;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private User user;

	public BookMarkDto toDto() {
		BookMarkDto dto = new BookMarkDto();
		dto.setBookMarkName(this.bookMarkName);
		dto.setBookMarkAccountNumber(this.bookMarkAccountNumber);
		dto.setBookMarkBankname(this.bookMarkBankname);
		dto.setUser(this.user);
		return dto;
	}
}
