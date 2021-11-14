package com.huangyb.rpc;

import com.huangyb.rpc.message.RpcRequestMessage;

/**
 * @author huangyb
 * @create 2021-11-07 17:35
 */
public interface RpcClient {
    Object sendRpcRequestMessage(RpcRequestMessage rpcRequestMessage);
}
