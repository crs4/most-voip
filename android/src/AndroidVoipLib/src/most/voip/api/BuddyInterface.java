package most.voip.api;

import most.voip.api.states.BuddyState;

public interface BuddyInterface {
   BuddyState getState();
   String getUri();
   String getStatusText();
}
