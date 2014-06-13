package most.voip.api;

public interface BuddyInterface {
   BuddyState getState();
   String getUri();
   String getStatusText();
}
