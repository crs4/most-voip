package most.voip.api.interfaces;

import most.voip.api.enums.BuddyState;

public interface IBuddy {
   BuddyState getState();
   String getUri();
   String getStatusText();
   void refreshStatus();
}
