package com.backend.accountmanagement.common.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;


@Getter
public class BaseDomain implements Serializable {

  static final long serialVersionUID = 1234L;

  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  private String createdBy;
  private String updatedBy;

}
