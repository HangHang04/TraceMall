package com.zzx.server.trace.gateway;

public interface ChainAnchorGateway {

    String anchor(String eventHash);

    boolean verify(String anchorId, String eventHash);
}