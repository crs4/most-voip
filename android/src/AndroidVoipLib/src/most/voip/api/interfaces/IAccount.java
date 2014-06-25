package most.voip.api.interfaces;

import most.voip.api.enums.AccountState;

public interface IAccount {
public String getUri();
public AccountState getState();
}
