package most.voip.api;

import most.voip.api.enums.BuddyState;

public interface IBuddy {
   BuddyState getState();
   String getUri();
   String getStatusText();
}
