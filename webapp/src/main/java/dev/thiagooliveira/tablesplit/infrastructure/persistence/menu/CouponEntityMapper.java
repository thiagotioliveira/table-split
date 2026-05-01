package dev.thiagooliveira.tablesplit.infrastructure.persistence.menu;

import dev.thiagooliveira.tablesplit.domain.menu.Coupon;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CouponEntityMapper {

  Coupon toDomain(CouponEntity entity);

  CouponEntity toEntity(Coupon domain);

  default Coupon.CouponRule map(CouponRuleEntity entity) {
    if (entity == null) return null;
    return new Coupon.CouponRule(entity.getType(), entity.getValue());
  }

  @Mapping(target = "couponId", ignore = true)
  default CouponRuleEntity map(Coupon.CouponRule domain) {
    if (domain == null) return null;
    CouponRuleEntity entity = new CouponRuleEntity();
    entity.setType(domain.type());
    entity.setValue(domain.value());
    return entity;
  }

  @AfterMapping
  default void linkCouponRulesAndUppercaseCode(@MappingTarget CouponEntity entity, Coupon domain) {
    if (domain.getCode() != null) {
      entity.setCode(domain.getCode().toUpperCase());
    }
    if (entity.getRules() != null && entity.getId() != null) {
      entity.getRules().forEach(rule -> rule.setCouponId(entity.getId()));
    }
  }
}
