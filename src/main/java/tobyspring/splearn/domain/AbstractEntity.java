package tobyspring.splearn.domain;

import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.proxy.HibernateProxy;

import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.ToString;

/**
 * Id는 공통으로 사용하기 때문에 관리 포인트를 하나로 모은다!
 */
@MappedSuperclass
@ToString //자식 클래스에 @ToString(callSuper = true) 붙여야 함
public abstract class AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter(onMethod_ =  { @Nullable})
	private Long id;

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ?
			((HibernateProxy)o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		AbstractEntity that = (AbstractEntity)o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ?
			((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
			getClass().hashCode();
	}
}
