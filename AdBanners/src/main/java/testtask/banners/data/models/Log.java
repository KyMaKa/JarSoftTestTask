package testtask.banners.data.models;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name ="Logs")
public class Log {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String ipAddress;

  @Column(nullable = false)
  private String userAgent;

  @Column(nullable = false)
  private LocalDateTime date;

  private Long bannerId;

  private String categoriesId;

  private Integer bannerPrice;

  private String noContent;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public Long getBannerId() {
    return bannerId;
  }

  public void setBannerId(Long bannerId) {
    this.bannerId = bannerId;
  }

  public String getCategoriesId() {
    return categoriesId;
  }

  public void setCategoriesId(String categoriesId) {
    this.categoriesId = categoriesId;
  }

  public Integer getBannerPrice() {
    return bannerPrice;
  }

  public void setBannerPrice(Integer bannerPrice) {
    this.bannerPrice = bannerPrice;
  }

  public String getNoContent() {
    return noContent;
  }

  public void setNoContent(String noContent) {
    this.noContent = noContent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Log log)) {
      return false;
    }
    return getId().equals(log.getId()) && getIpAddress().equals(log.getIpAddress())
        && getUserAgent().equals(log.getUserAgent()) && getDate().equals(log.getDate())
        && Objects.equals(getBannerId(), log.getBannerId()) && Objects.equals(
        getCategoriesId(), log.getCategoriesId()) && Objects.equals(getBannerPrice(),
        log.getBannerPrice()) && Objects.equals(getNoContent(), log.getNoContent());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getIpAddress(), getUserAgent());
  }
}
