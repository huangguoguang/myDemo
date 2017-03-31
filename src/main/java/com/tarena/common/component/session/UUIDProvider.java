package com.tarena.common.component.session;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

public class UUIDProvider implements SessionIdProvider {

  @Override
  public String getSessionId(Object... objects) {
    return StringUtils.remove(UUID.randomUUID().toString(), "-");
  }
}
