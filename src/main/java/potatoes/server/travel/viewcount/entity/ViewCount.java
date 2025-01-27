package potatoes.server.travel.viewcount.entity;

import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ViewCount {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "travel_id")
	private Long travelId;

	@Column(name = "view_count")
	private int viewCount;

}
// FIXME 조회수 로직의 실질적인 조회는 레디스가 담당하고 viewCount엔티티는 데이터 저장용이기 때문에 간접 참조로 사용한다. 다만 위 테이블이 사용될 경우가 생기면 위 로직을 재검토 해야하기 때문에 주석처리를 한다.
