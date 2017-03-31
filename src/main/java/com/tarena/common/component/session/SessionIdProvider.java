package com.tarena.common.component.session;


public interface SessionIdProvider {
  public String getSessionId(Object... objects);
}
