package most.voip.api.interfaces;

import most.voip.api.enums.ServerState;

public interface IServer {
  ServerState getState();
  String getIp();
}
